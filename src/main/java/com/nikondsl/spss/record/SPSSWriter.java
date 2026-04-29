package com.nikondsl.spss.record;

import com.nikondsl.spss.IBufferProgressListener;
import com.nikondsl.spss.IProgressListener;
import com.nikondsl.spss.ISPSSWriter;
import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.NameAndLabel;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class SPSSWriter implements ISPSSWriter {
    private static final String buildNumber = "1.9 build 147";
    static VariableNameGenerator generator = new VariableNameGenerator();
    //if ignored, then all limitations will be ignored with automatic truncations
    static boolean ignoreLimitations = true;
    final String charset;
    final Map<HolderKey, Holder> stings = new HashMap<>();
    final Map<VariableKey, Variable> lastVariablesCached = new HashMap<>();
    final Map<SPSSUtilHolder<String, Integer>, List<byte[]>> dividedStrings = new HashMap<>();
    private final OutputStream outputStream;

    private final SPSSBuffer buffer;
    private final Set<String> uniqueNames = new HashSet<>(256);
    private final Set<String> variablesLegacyNames = new HashSet<>();
    private final List<String> documentaryInformation = Collections.emptyList();
    private final Map<String, List<IVariable>> variablesSets = new HashMap<>();
    private final long startTime = System.currentTimeMillis();
    private final java.util.concurrent.ConcurrentMap<Variable, Integer> stringVariablesRecommendedLengths = new ConcurrentHashMap<>();
    private final Map<Variable, Integer> stringVariables = new HashMap<>();
    public boolean highCompatibilityMode;
    int queueSize = 5000;
    int cacheSize = 1000;
    CountDownLatch countDownLatch = new CountDownLatch(1);
    Map<VariableHolderKey, Variable> additionalVariables = new HashMap<>();
    private long loggingEveryMilliseconds = 15000L;
    private ArrayBlockingQueue<SPSSCase> queue = null;
    private volatile boolean fileClosed = false;
    private Thread consumer = new ConsumerThread(this);
    private volatile boolean consumerStarted = false;
    private Record1 record1 = null;
    private LinkedHashSet<Variable> variables = null;
    private volatile boolean needAutoSetStringWidth = false;
    private volatile boolean lineByLineMode;//Starts CONSUMER thread, uses QUEUE, non-blocking
    private int totalCasesGenerated = 0;
    private long lastStatisticsPrintTime = System.currentTimeMillis();
    private IProgressListener mainListener = null;
    private IBufferProgressListener bufferListener = null;
    private IBufferProgressListener fileBufferListener = null;
    private volatile boolean variablesChecked = false;
    private volatile boolean dictionaryHasBeenGenerated = false;
    private MultipleResponseSetType type = null;
    private Map<NameAndLabel, List<IVariable>> variablesInSet;
    private Long dichotomousValue;
    private volatile boolean stringsLengthFixed = false;

    SPSSWriter(String charset, String header, OutputStream externalOutputStream) {
        try {
            init();
            this.charset = charset;
            FileLayoutCode layoutCode = FileLayoutCode.LABEL_VARIABLE_LENGTH;
            record1 = createRecord1(charset, header, layoutCode);
            variables = new LinkedHashSet<>();
            buffer = new SPSSBuffer(65536);
            if (externalOutputStream != null) outputStream = externalOutputStream;
            else outputStream = new ByteArrayOutputStream();
        } catch (UnsupportedEncodingException ex) {
            SPSSWriter.log.error("Invalid encoding [" + charset + "] provided:", ex);
            throw new NoSuchMethodError(ex.getMessage());
        }
    }

    SPSSWriter(String charset, String header) {
        this(charset, header, null);
    }

    void init() {
        try {
            queueSize = Integer.parseInt(System.getProperty("X!P@o#r$t$%e%r@.!q@u#e$u$e!".replaceAll("[^a-z.A-Z0-9 ]++", "")));
        } catch (Exception ex) {
            SPSSWriter.log.info("Warning: invalid queue size");
        }
        try {
            cacheSize = Integer.parseInt(System.getProperty("X!P@o#r$t$%e%r@.!c@a#c$h$e!".replaceAll("[^a-z.A-Z0-9 ]++", "")));
        } catch (Exception ex) {
            SPSSWriter.log.info("Warning: invalid cache size");
        }
        try {
            loggingEveryMilliseconds = Long.parseLong(System.getProperty("X!P@o#r$t$%e%r@.!l@o#g$!".replaceAll("[^a-z.A-Z0-9 ]++", "")));
        } catch (Exception ex) {
            SPSSWriter.log.info("Warning: invalid log time");
        }
        if (queueSize <= 0) {
            queueSize = 1;
            SPSSWriter.log.info("Warning: queue size cannot be less than 1. Queue length has been set to 1.");
        }
        queue = new ArrayBlockingQueue<SPSSCase>(queueSize);
    }

    public synchronized List<IVariable> getVariables() {
        return Collections.unmodifiableList(new ArrayList<IVariable>(variables));
    }

    public synchronized void setLineByLineMode(boolean lineByLineMode) {
        checkState();
        if (lineByLineMode) {
            SPSSWriter.log.info("You can use different threads to produce results (producer threads) and to write them (consumer thread) in 'LineByLine' mode.");
        } else {
            SPSSWriter.log.error("SPSSWriter uses the same thread to produce results and to write them. It might be slower than 'LineByLine' mode, because XPorter has to wait until producer unblock.");
        }
        this.lineByLineMode = lineByLineMode;
    }

    public synchronized void setNumberOfCases(int numberOfCases) {
        checkState();
        if (numberOfCases < 0) return;
        record1.setNumberOfCases(numberOfCases);
    }

    public synchronized void close() throws IOException {
        outputStream.close();
    }

    Variable createVariable(VariableType type, String name, String label, Map<Double, String> labels) throws UnsupportedEncodingException {
        checkState();
        return new Variable(charset, type, name, label, labels);
    }

    Variable createVariable(String name, String label) throws UnsupportedEncodingException {
        checkState();
        return new Variable(charset, name, label);
    }

    Variable createVariable(String name, String label, int width) throws UnsupportedEncodingException {
        checkState();
        return new Variable(charset, name, label, width);
    }

    Record1 createRecord1(String charset, String header, FileLayoutCode layoutCode) throws UnsupportedEncodingException {
        return new Record1(charset, header, layoutCode, this);
    }

    public synchronized void setFileLabel(String text) throws UnsupportedEncodingException {
        checkState();
        record1.setFileLabel(text);
    }

    public synchronized void setGenerationDate(Date date) {
        checkState();
        record1.setGenerationTimestamp(date);
    }

    public synchronized void addVariable(IVariable variable) {
        checkState();
        if (!variables.add((Variable) variable))
            throwException("SPSS " + variable + " has already been added!");
        if (variable.getType() == VariableType.STRING && ((Variable) variable).getWidth() == 0) {
            needAutoSetStringWidth = true;
        }
        uniqueNames.add(((Variable) variable).getOldNameAsString().trim());
        SPSSWriter.log.info("Add SPSS " + variable);
    }

    public synchronized void generateDictionary() throws IOException {
        if (dictionaryHasBeenGenerated) throwException("Dictionary has already been added!");
        dictionaryHasBeenGenerated = true;
        checkState();
        RecordsProcessor recordsProcessor = new RecordsProcessor(this, record1, variables, outputStream);
        log.info("\n=======================================================================\n" +
                "(c) XPorter " + getBuildNumber() + " is written by NIkon DSL. All rights reserved.\n" +
                "=======================================================================\n");
        //========================
        //write dictionary section
        //========================
        //write definitions (record type 1)
        if (record1.getRealNumberOfCases() == null) {
            throwException("You have to set number of cases manually by calling method setNumberOfCases().");
        } else log.info("You have set NumberOfCases to " + record1.getRealNumberOfCases());
        List<Variable> trueVariables = recordsProcessor.processingRecord2(false);
        record1.setNumberOfVariables(trueVariables.size());
        recordsProcessor.processingRecord1();
        //write variables (record type 2)
        trueVariables = recordsProcessor.processingRecord2(true);
        //write value labels (record types 3,4)
        recordsProcessor.processingRecord3(trueVariables);

        //write Documents record (record type 6)
        if (!documentaryInformation.isEmpty()) {
            recordsProcessor.processingRecord6(documentaryInformation);
        }

        //write release and machine-specific integer information (record type 7 subtype 3)
        recordsProcessor.processingRecord7SubType3(getBuildNumber());

        //write release and machine-specific floating-point information (record type 7 subtype 4)
        recordsProcessor.processingRecord7SubType4();

        //Variable Sets Information (record type 7 subtype 5)
        recordsProcessor.processingRecord7SubType5(variablesSets);

        //Trends Date Information (record type 7 subtype 6)
//        recordsProcessor.processingRecord7SubType6(trueVariables);

        //Multiple Response Sets (record type 7 subtype 7)
        if (type != null) {
            recordsProcessor.processingRecord7SubType7(type, variablesInSet, dichotomousValue);
        }

        //write Measurement Level, Column Width, and Alignment (record type 7 subtype 11)
        recordsProcessor.processingRecord7SubType11(trueVariables);

        //write long variable names (record type 7 subtype 13)
        recordsProcessor.processingRecord7SubType13();

        //write Extended Strings (record type 7 subtype 14)
        recordsProcessor.processingRecord7SubType14();

        //write 64-bit Number of Cases (record type 7 subtype 16)
        recordsProcessor.processingRecord7SubType16();

        //write Character Encoding (Code Page) (record type 7 subtype 20)
        recordsProcessor.processingRecord7SubType20(charset);

        //write last dictionary section (record type 999)
        recordsProcessor.processingRecord999();
    }

    void addCase(final SPSSCase spssCase) {
        if (spssCase == null) return;
        if (spssCase == ConsumerThread.MARKER_EOF) return;
        try {
            addSingleCase(spssCase);
        } catch (Exception ex) {
            SPSSWriter.log.error("Could not add more SPSSCase:", ex);
            if (getBufferListener() != null) getBufferListener().finish();
        }
    }

    public void generate(List<SPSSCase> casesArg) throws IOException {
        checkState();
        checkAndFixStringValues(casesArg);
        synchronized (this) {
            if (!stringsLengthFixed) {
                for (Map.Entry<Variable, Integer> entry : stringVariablesRecommendedLengths.entrySet()) {
                    entry.getKey().setWidth(entry.getValue());
                }
                stringsLengthFixed = true;
            }
        }
        if (lineByLineMode) {
            if (casesArg == null) {
                throwException("'LineByLine' mode needs SPSSCases to be defined in method generate()");
            }
            synchronized (this) {
                if (!consumerStarted) {
                    consumerStarted = true;
                    consumer.start();
                }
            }
            for (SPSSCase spssCase : casesArg) {
                if (spssCase == null) continue;
                try {
                    int timeout = Math.abs((queueSize + 1) / 30);
                    if (!queue.offer(spssCase, timeout < 100 ? 100 : timeout, TimeUnit.SECONDS)) {
                        //timeout occurs here
                        SPSSWriter.log.error("It seems that the XPorter consumer thread has died. Could not add another SPSSCase for " + timeout + " seconds. Maximum queue size is " + queueSize + ", current queue size is " + queue.size());
                        Thread.currentThread().interrupt();
                    }
                } catch (InterruptedException ex) {
                    SPSSWriter.log.error("Writing was interrupted", ex);
                    Thread.currentThread().interrupt();
                    break;
                }
                if (fileBufferListener != null) {
                    try {
                        int currentLength = buffer.getCurrentLength();
                        fileBufferListener.status(currentLength, buffer.getMaxLength() - currentLength);
                    } catch (Exception ex) {
                        SPSSWriter.log.error("Unexpected exception occurred while sending NEXT signal to File Buffer Progress Listener:", ex);
                    }
                }
            }
            return;
        }
        synchronized (this) {
            List<SPSSCase> cases = new ArrayList<SPSSCase>(casesArg);
            addCases(cases);
        }
    }

    private void addCases(List<SPSSCase> cases) {
        try {
            for (SPSSCase spssCase : cases) {
                if (!addSingleCase(spssCase)) break;
            }
        } catch (Exception ex) {
            SPSSWriter.log.error("Could not add more SPSSCases:", ex);
        }
    }

    String generateUniqueName(final String addVariableName) {
        int index = uniqueNames.size();
        String newName = addVariableName + Integer.toString(index, 10);
        while (!uniqueNames.add(newName.toUpperCase())) {
            index++;
            newName = addVariableName + Integer.toString(index, 10);
        }
        return newName.toUpperCase();
    }

    Variable createAdditionalVariable(Variable variable,
                                      String addVariableName,
                                      int index) throws UnsupportedEncodingException {
        VariableHolderKey key = new VariableHolderKey(addVariableName, index);
        Variable result = additionalVariables.get(key);
        if (result != null) {
            return result;
        }
        result = new Variable(charset,
                generateUniqueName(addVariableName),
                variable.getLabel(),
                255);
        additionalVariables.put(key, variable);
        return result;
    }

    private boolean addSingleCase(SPSSCase spssCase) throws IOException {
        if (spssCase == null) return true;
        logPercent();
        //write the values
        totalCasesGenerated++;
        int index = 0;
        for (Variable variable : variables) {
            Object obj = spssCase.getValue(index);
            index++;
            try {
                if (obj == null && variable.getType() == VariableType.STRING && variable.getRealWidth() < 255 ||
                        obj == null && variable.getType() != VariableType.STRING) {
                    addNullValue(variable);
                } else if (variable.getType() == VariableType.NUMERIC ||
                        variable.getType() == VariableType.DOT ||
                        variable.getType() == VariableType.COMMA ||
                        variable.getType() == VariableType.DOLLAR) {
                    NumericValueProcessor numericValueProcessor = new NumericValueProcessor(this, buffer, variable, record1.getCompressionBias());
                    numericValueProcessor.writeNumericValue(obj);
                } else if (variable.getType() == VariableType.STRING) {
                    StringValueProcessor stringValueProcessor = new StringValueProcessor(this, buffer);
                    stringValueProcessor.writeStringValue(variable, (String) obj);
                } else if (variable.getType() == VariableType.DATE) {
                    DateValueProcessor dateValueProcessor = new DateValueProcessor(buffer);
                    if (obj instanceof Date)
                        dateValueProcessor.addDateValue(variable, UnmodifiableDate.getInstance(((Date) obj).getTime()));
                    else if (obj instanceof UnmodifiableDate)
                        dateValueProcessor.addDateValue(variable, ((UnmodifiableDate) obj));
                    else throwException("*** Could not cast value [" + obj + "] to DATE for " + variable);
                } else {
                    throwException("*** Could not add the value [" + obj + "] for " + variable);
                }
            } catch (Exception ex) {
                SPSSWriter.log.error("*** An error has occurred while processing variable #" + index + ":" + variable + " and value:[" + obj + "]", ex);
            }
            if (buffer.getCompressedBytesLength() > buffer.getMaxLength() / 1.75) buffer.flushBuffer(outputStream);
        }
        buffer.flushBuffer(outputStream);
        if (fileBufferListener != null) {
            try {
                int currentLength = buffer.getCurrentLength();
                fileBufferListener.status(currentLength, buffer.getMaxLength() - currentLength);
            } catch (Exception ex) {
                SPSSWriter.log.error("Unexpected exception occurred while sending NEXT signal to File Buffer Progress Listener:", ex);
            }
        }
        if (mainListener != null) {
            try {
                if (spssCase != ConsumerThread.MARKER_EOF) mainListener.nextStep();
            } catch (Exception ex) {
                SPSSWriter.log.error("Unexpected exception occurred while sending NEXT signal to Progress Listener:", ex);
            }
        }
        return true;
    }

    private void logPercent() {
        if (System.currentTimeMillis() - lastStatisticsPrintTime <= loggingEveryMilliseconds) return;
        lastStatisticsPrintTime = System.currentTimeMillis();
        String percent = record1.getRealNumberOfCases() != null ? " " + String.format("%4.2f", (totalCasesGenerated) * 100.0 / record1.getRealNumberOfCases()) + "%" : "";
        SPSSWriter.log.info(getCurrentLogMessage(percent));
    }

    private String getCurrentLogMessage(String percent) {
        if ("100.00 %".equals(percent)) {
            SPSSWriter.log.debug("Used cache info:\n[Divided String " + dividedStrings.size() + " items]\n[Dates " + UnmodifiableDate.cache.size() + " items]\n[LastVariables " + lastVariablesCached.size() + " items]");
        }
        double kph = (3600.0 * (totalCasesGenerated) / (System.currentTimeMillis() - startTime + 1.0));
        boolean mph = false;
        if (kph > 1000) {
            mph = true;
            kph /= 1000.0;
        }
        return "Total " +
                String.format("%7.0f", (double) totalCasesGenerated) + " (" +
                percent + ") cases have been written in " +
                String.format("%7.1f", (System.currentTimeMillis() - startTime) / 1000.0) +
                " sec. " +
                "[" + String.format("%5.2f", kph) + " " + (mph ? "Mph" : "Kph") + "]";
    }

    public synchronized void generateFinishSection() throws IOException {
        if (lineByLineMode) {
            generate(Collections.singletonList(ConsumerThread.MARKER_EOF));
            //we have to wait until full queue write happens
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throwException("Writing interrupted.");
            }
        }
        countDownLatch.countDown();
        addFinishSection();
        SPSSWriter.log.info(getCurrentLogMessage("100.00 %"));
        fileClosed = true;
    }

    private void checkAndFixStringValues(List<SPSSCase> cases) throws UnsupportedEncodingException {
        synchronized (this) {
            if (!variablesChecked) {
                int index = -1;
                for (Variable variable : variables) {
                    index++;
                    if (variable.getType() == VariableType.STRING) {
                        stringVariables.put(variable, index);
                        if (variable.getWidth() <= 0 && lineByLineMode) {
                            throwException("'LineByLine' mode requires complete variable definitions! (Set width please for STRING variables)");
                        }
                    }
                    int attemptNumber = 0;
                    while (!variablesLegacyNames.add(variable.getOldNameAsString())) {
                        //need to change the legacy name
                        //generate legacy name
                        final String generatedName = "d" + generator.getNext();
                        variable.setName(generatedName);
                        variable.name = new FixedLengthString(charset, generatedName, 8);
                        variable.oldName = new FixedLengthString(charset, generatedName.toUpperCase(), 8);
                        variable.oldNameAsString = variable.oldName.toString();
                        attemptNumber++;
                        if (attemptNumber > 10) {
                            throwException("Could not generate unique name for " + variable + ", made 10 attempts.");
                        }
                    }
                }
                variablesChecked = true;
            }
        }
        int caseNumber = 0;

        for (SPSSCase singleCase : cases) {
            if (singleCase == null) continue;
            if (singleCase == ConsumerThread.MARKER_EOF) return;
            caseNumber++;
            int totalValues = singleCase.getValues().size();
            if (totalValues != variables.size()) {
                throwException("There is a SPSS case #" + caseNumber + " with unrelated values (" + totalValues + ") and variables (" + variables.size() + ")");
            }
            if (!needAutoSetStringWidth) continue;
            //try to find out how length strings are

            for (Map.Entry<Variable, Integer> entry : stringVariables.entrySet()) {
                String value = (String) singleCase.getValue(entry.getValue());
                Integer realWidth = value.getBytes(charset).length;
                if (realWidth % 2 == 1) realWidth++;

                Integer oldWidth = stringVariablesRecommendedLengths.get(entry.getKey());
                if (oldWidth == null || oldWidth < realWidth)
                    oldWidth = stringVariablesRecommendedLengths.putIfAbsent(entry.getKey(), realWidth);
                if (oldWidth == null || oldWidth < realWidth)
                    stringVariablesRecommendedLengths.put(entry.getKey(), realWidth);
            }
        }
    }

    private void addFinishSection() throws IOException {
        try {
            countDownLatch.await();
            if (!highCompatibilityMode) {
                buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_END_OF_FILE.getCode());
            }
            while (buffer.getCompressedBytesLength() % 8 != 0) {
                buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_SKIP_CODE.getCode());
            }
            buffer.flushBuffer(outputStream);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            SPSSWriter.log.error("Writing interrupted.", ex);
            buffer.clear();
        }
        if (fileBufferListener != null) {
            try {
                fileBufferListener.finish();
            } catch (Exception ex) {
                SPSSWriter.log.error("Unexpected exception occurred while sending NEXT signal to File Buffer Progress Listener:", ex);
            }
        }
        if (bufferListener != null) {
            try {
                if (getBufferListener() != null) bufferListener.finish();
            } catch (Exception ex) {
                SPSSWriter.log.error("An unexpected exception has occurred while sending FINISH signal to Queue State Listener", ex);
            }
        }
        if (mainListener == null) return;
        try {
            mainListener.finish();
        } catch (Exception ex) {
            SPSSWriter.log.error("An unexpected exception has occurred while sending FINISH signal to Progress Listener", ex);
        }
    }

    private void addNullValue(Variable variable) {
        if (variable.getType() != VariableType.STRING) {
            buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_MISSING_VALUE.getCode());
            return;
        }
        int width = variable.getWidth();
        byte[] compressed = new byte[ (width % 8 != 0 ? (width / 8 + 1) * 8 : width) / 8 ];
        Arrays.fill(compressed, CompressBufferPredefinedCodes.COMPRESS_ALL_BLANKS.getCode());
        buffer.addCompressedBytes(compressed);
    }

    public synchronized void addVariables(List<IVariable> variables) {
        checkState();
        for (IVariable variable : variables) {
            addVariable(variable);
        }
    }

    public synchronized void addVariablesSets(Map<String, List<IVariable>> variablesSets) {
        checkState();
        this.variablesSets.putAll(variablesSets);
    }

    private void checkState() {
        if (!fileClosed) {
            return;
        }
        throwException("Output has already been closed!\n\n The main procedure should look like that:\n" +
                "        writer.addVariables(List<IVariable> variables);\n" +
                "        writer.generate(List<SPSSCase> cases);\n" +
                "        writer.generateFinishSection();\n" +
                "        writer.close();\n" +
                "OR\n" +
                "        writer.setLineByLineMode(true);\n" +
                "        writer.addVariables(List<IVariable> variables);\n" +
                "        writer.setNumberOfCases(numberOfCases);\n" +
                "        writer.generateDictionary(null);\n" +
                "        writer.generate(List<SPSSCase> cases);\n" +
                "        writer.generate(List<SPSSCase> cases);\n" +
                "        writer.generate(List<SPSSCase> cases);\n" +
                "        writer.generateFinishSection();\n" +
                "        writer.close();\n" +
                "\n");
    }

    void throwException(String text) {
        queue.clear();
        if (consumer != null) {
            consumer.interrupt();
            consumer = null;
        }
        throw new IllegalStateException(text);
    }

    public void setProgressListener(IProgressListener listener) {
        mainListener = listener;
    }

    public void setBufferProgressListener(IBufferProgressListener listener) {
        bufferListener = listener;
    }

    BlockingQueue<SPSSCase> getQueue() {
        return queue;
    }

    IBufferProgressListener getBufferListener() {
        return bufferListener;
    }

    public void setFileBufferProgressListener(IBufferProgressListener listener) {
        this.fileBufferListener = listener;
    }

    public synchronized IVariable findVariableByIndex(int variableIndex) {
        int index = variableIndex - 1;
        return (IVariable) variables.toArray()[ index ];
    }

    public synchronized void setHighCompatibilityMode(boolean highCompatibilityMode) {
        if (highCompatibilityMode) {
            log.info("*** 'High compatibility' mode prohibits value labels for string variables and limits text variables to 255 characters");
        } else log.info("*** 'High compatibility' mode is OFF");
        this.highCompatibilityMode = highCompatibilityMode;
    }

    public void setDichotomousMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet,
                                                   Long dichotomousValue) {
        setMultipleResponseSets(MultipleResponseSetType.DICHOTOMOUS, variablesInSet, dichotomousValue);
    }

    public void setCategoricalMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet) {
        setMultipleResponseSets(MultipleResponseSetType.CATEGORICAL, variablesInSet, 0L);
    }

    private synchronized void setMultipleResponseSets(MultipleResponseSetType type, Map<NameAndLabel, List<IVariable>> variablesInSet, Long dichotomousValue) {
        this.type = type;
        this.variablesInSet = variablesInSet;
        this.dichotomousValue = dichotomousValue;
    }

    boolean getRandomText() {
        return Math.random() > 0.5d;
    }

    String getBuildNumber() {
        return buildNumber;
    }
}