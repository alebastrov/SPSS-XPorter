package com.nikondsl.spss.adapter;

import com.nikondsl.spss.IBufferProgressListener;
import com.nikondsl.spss.IMissingValue;
import com.nikondsl.spss.IXmlConverter;
import com.nikondsl.spss.IProgressListener;
import com.nikondsl.spss.ISPSSWriter;
import com.nikondsl.spss.IValueLabels;
import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.NameAndLabel;
import com.nikondsl.spss.record.Alignment;
import com.nikondsl.spss.record.DateTimeFormat;
import com.nikondsl.spss.record.FormatCode;
import com.nikondsl.spss.record.Measure;
import com.nikondsl.spss.record.SPSSCase;
import com.nikondsl.spss.record.SPSSFacade;
import com.nikondsl.spss.record.UnmodifiableDate;
import com.nikondsl.spss.record.VariableType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 21.05.2008
 * Time: 5:31:17
 * To give the same methods, as pmstation library
 * note: use only in single thread, non-thread safe at all!
 */
@Slf4j
public class XmlConverterHelper implements IXmlConverter {
    private final String charset;
    private ISPSSWriter writer;
    private final List<SPSSCase> cases = new ArrayList<>();
    private OutputStream outputStream = null;
    private List<IVariable> variables = null;
    private SPSSCase currentCase = new SPSSCase();
    private int indexOfVariable = 0;

    private XmlConverterHelper() {
        charset = "utf-8";
    }

    public XmlConverterHelper(String charset) {
        this.charset = charset;
    }

    public XmlConverterHelper(OutputStream outputStream, String encoding) {
        this.outputStream = outputStream;
        this.charset = encoding;
    }

    public void setOut(OutputStream out) {
        throw new NoSuchMethodError("Use constructor instead");
    }

    public void setCharset(String charset) {
        throw new IllegalStateException("Use constructor instead");
    }

    public void addDictionarySection() throws IOException {
        writer = SPSSFacade.createWriter(charset, "PMStationAdapter", outputStream);
        writer.setLineByLineMode(true);
    }

    public void addDictionarySection(int numberOfCases) throws IOException {
        writer = SPSSFacade.createWriter(charset, "PMStationAdapter", outputStream);
        writer.setLineByLineMode(true);
        writer.setNumberOfCases(numberOfCases);
    }

    public void addDictionarySection(String header, String fileLabel) throws IOException {
        writer = SPSSFacade.createWriter(charset, "PMStationAdapter" + header, outputStream);
        writer.setLineByLineMode(true);
        writer.setFileLabel(fileLabel);
    }

    public void addDictionarySection(String header, int numberOfVars, int numberOfCases, String fileLabel, boolean storeFormat) throws IOException {
        writer = SPSSFacade.createWriter(charset, "PMStationAdapter" + header, outputStream);
        writer.setLineByLineMode(true);
        writer.setFileLabel(fileLabel);
        writer.setNumberOfCases(numberOfCases);
    }

    public void addDictionarySection(String header, int numberOfVars, int numberOfCases, String fileLabel, boolean storeFormat, ISPSSWriter externalSPSSWriter) throws IOException {
        writer = externalSPSSWriter;
        writer.setFileLabel("PMStationAdapter" + header);
        writer.setLineByLineMode(true);
        writer.setFileLabel(fileLabel);
        writer.setNumberOfCases(numberOfCases);
    }

    public void addDictionarySection(String header,
                                     int numberOfVars,
                                     int numberOfCases,
                                     int caseWeightVar,
                                     int compressionSwitch,
                                     String fileLabel,
                                     Date date,
                                     boolean storeFormat) throws IOException {
        writer = SPSSFacade.createWriter(charset, "PMStationAdapter" + header, outputStream);
        writer.setLineByLineMode(true);
        writer.setFileLabel(fileLabel);
        writer.setNumberOfCases(numberOfCases);
        writer.setGenerationDate(date);
    }

    public void addDataSection() throws IOException {
        if (writer == null) throw new IllegalStateException("Dictionary section has not been added");
        if (SPSSFacade.getVariables(writer).isEmpty())
            throw new IllegalStateException("There are no variables defined");
        writer.generateDictionary();
    }

    public void addVarFloat(String name, String label) throws IOException {
        writer.addVariable(SPSSFacade.createVariable(writer, VariableType.NUMERIC, name, label, null));
    }

    public void addVarInt(String name, String label, Map<Integer, String> valueLabels) throws IOException {
        Map<Double, String> valueLabelsRepacked = new HashMap<Double, String>();
        for (Map.Entry<Integer, String> entry : valueLabels.entrySet()) {
            valueLabelsRepacked.put(entry.getKey().doubleValue(), entry.getValue());
        }
        writer.addVariable(SPSSFacade.createVariable(writer, VariableType.NUMERIC, name, label, valueLabelsRepacked));
    }

    public void addVarString(String name, String label) throws IOException {
        writer.addVariable(SPSSFacade.createVariable(writer, name, label));
    }

    public void addVarDate(String name, String label) throws IOException {
        writer.addVariable(SPSSFacade.createVariable(writer, VariableType.DATE, name, label, null));
    }

    public void addLine() throws IOException {
        //add new case here
        SPSSCase singleCase = new SPSSCase();
        cases.add(singleCase);
    }

    public void addCell(String s) throws IOException {
        cases.get(cases.size() - 1).addValue(s);
    }

    public void addCell(double f) throws IOException {
        cases.get(cases.size() - 1).addValue(f);
    }

    public void addCell(int i) throws IOException {
        cases.get(cases.size() - 1).addValue(i);
    }

    public void addCell(Date d) throws IOException {
        cases.get(cases.size() - 1).addValue(d);
    }

    public void addNullInt() throws IOException {
        cases.get(cases.size() - 1).addValue(null);
    }

    //=============================

    public void addNullDouble() throws IOException {
        cases.get(cases.size() - 1).addValue(null);
    }

    public void close() throws IOException {
        writer.generate(cases);
    }

    public void addStringVar(String name, int width, String label) throws UnsupportedEncodingException {
        checkStateDictionary();
        IVariable stringVariable = SPSSFacade.createVariable(writer, name, label, width);
        writer.addVariable(stringVariable);
    }

    public void addStringVar(String name, int length, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addStringVar(name, length, label);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable stringVariable = variables.get(variables.size() - 1);
        stringVariable.setWidth(columns);
        stringVariable.setAlign(Alignment.valueOf(align));
        stringVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addNumericVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addNumericVar(name, width, decimals, label, null);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable numericVariable = variables.get(variables.size() - 1);
        numericVariable.setColumnWidth(columns);
        numericVariable.setAlign(Alignment.valueOf(align));
        numericVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addNumericVar(String name, int width, int decimals, String label) throws UnsupportedEncodingException {
        checkStateDictionary();
        IVariable numericVariable = SPSSFacade.createVariable(writer, VariableType.NUMERIC, name, label, null);
        numericVariable.setWidth(width);
        numericVariable.setDecimals(decimals);
        writer.addVariable(numericVariable);
    }

    public void addNumericVar(String name, int width, int decimals, String label, IMissingValue missingValue) throws UnsupportedEncodingException {
        checkStateDictionary();
        IVariable numericVariable = SPSSFacade.createVariable(writer, VariableType.NUMERIC, name, label, null);
        numericVariable.setWidth(width);
        numericVariable.setDecimals(decimals);
        numericVariable.setMissingValue(missingValue);
        writer.addVariable(numericVariable);
    }

    public void addValueLabels(int variableIndex, IValueLabels valueLabels) throws UnsupportedEncodingException {
        checkStateDictionary();
        Map<Double, String> valueLabelsInternal = new HashMap<Double, String>();
        valueLabelsInternal.putAll(valueLabels.getValueLabels());
        final IVariable variable = writer.findVariableByIndex(variableIndex);
        variable.setNumericValueLabels(valueLabelsInternal);
    }

    public void addDollarVar(String name, int width, int decimals, String label) throws UnsupportedEncodingException {
        checkStateDictionary();
        IVariable numericVariable = SPSSFacade.createVariable(writer, VariableType.DOLLAR, name, label, null);
        numericVariable.setWidth(width);
        numericVariable.setDecimals(decimals);
        writer.addVariable(numericVariable);
    }

    public void addDollarVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addDollarVar(name, width, decimals, label);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable dollarVariable = variables.get(variables.size() - 1);
        dollarVariable.setColumnWidth(columns);
        dollarVariable.setDecimals(decimals);
        dollarVariable.setAlign(Alignment.valueOf(align));
        dollarVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addDotVar(String name, int width, int decimals, String label) throws UnsupportedEncodingException {
        checkStateDictionary();
        IVariable numericVariable = SPSSFacade.createVariable(writer, VariableType.DOT, name, label, null);
        numericVariable.setWidth(width);
        numericVariable.setDecimals(decimals);
        writer.addVariable(numericVariable);
    }

    public void addDotVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addDotVar(name, width, decimals, label);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable dotVariable = variables.get(variables.size() - 1);
        dotVariable.setColumnWidth(columns);
        dotVariable.setDecimals(decimals);
        dotVariable.setAlign(Alignment.valueOf(align));
        dotVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addCommaVar(String name, int width, int decimals, String label) throws IOException {
        checkStateDictionary();
        IVariable commaVariable = SPSSFacade.createVariable(writer, VariableType.COMMA, name, label, null);
        commaVariable.setWidth(width);
        commaVariable.setDecimals(decimals);
        writer.addVariable(commaVariable);
    }

    public void addCommaVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addCommaVar(name, width, decimals, label);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable commaVariable = variables.get(variables.size() - 1);
        commaVariable.setColumnWidth(columns);
        commaVariable.setDecimals(decimals);
        commaVariable.setAlign(Alignment.valueOf(align));
        commaVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addDateVar(String name, int dateCode, String label) throws IOException {
        checkStateDictionary();
        IVariable dateVariable = SPSSFacade.createVariable(writer, VariableType.DATE, name, label, null);
        dateVariable.setDateFormatStyle(DateTimeFormat.create(dateCode));
        writer.addVariable(dateVariable);
    }

    public void addDateVar(String name, int dateCode, String label, int columns, int align, int measure) throws IOException {
        checkStateDictionary();
        addDateVar(name, dateCode, label);
        List<IVariable> variables = SPSSFacade.getVariables(writer);
        IVariable dateVariable = variables.get(variables.size() - 1);
        dateVariable.setAlign(Alignment.valueOf(align));
        dateVariable.setMeasure(Measure.valueOf(measure));
    }

    public void addDateVar(String name, String dateType, String label) throws IOException {
        addDateVar(name, dateType, label, -1, -1, -1);
    }

    public void addDateVar(String name, String dateType, String label, int columns, int align, int measure) throws IOException {
        addDateVar(name, FormatCode.decode(DateTimeFormat.create(dateType).getCodeType()).getCode(), label, columns, align, measure);
    }

    public void addFinishSection() throws IOException {
        checkStateDictionary();
        if (variables == null || variables.isEmpty()) {
            if (!cases.isEmpty()) throw new IllegalStateException("Dictionary section was not added.");
            writer.generateFinishSection();
            writer.close();
            return;
        }
        //we have to check if there are all wariables have their values
        getNextCaseIfNeeds();//last record
        if (indexOfVariable != 1 && indexOfVariable - 1 != variables.size()) {
            throw new IllegalArgumentException("Synchronization between variables (" + variables.size() + ") and values (" + (indexOfVariable - 1) + ") failed!");
        }
        writer.generateFinishSection();
        writer.close();
    }

    private void checkStateDictionary() {
        if (writer == null) {
            throw new IllegalStateException("You should have added a dictionary section");
        }
    }

    public void finishCurrentLine() {
        indexOfVariable = 0;
    }

    private SPSSCase getNextCaseIfNeeds() {
        indexOfVariable++;
        if (indexOfVariable > variables.size()) {
            try {
                if (log.isDebugEnabled()) log.debug("Add case:" + currentCase);
                writer.generate(Collections.singletonList(currentCase));
                indexOfVariable = 1;
                currentCase = new SPSSCase();
            } catch (IOException ex) {
                log.error("", ex);
            }
        }
        return currentCase;
    }

    public void addData(Object value) {
        if (variables == null) variables = SPSSFacade.getVariables(writer);
        SPSSCase singleCase = getNextCaseIfNeeds();
        final IVariable currentVariable = variables.get(indexOfVariable - 1);
        if (value != null && currentVariable.getType() == VariableType.STRING && !(value instanceof String) ||
                value != null && currentVariable.getType() == VariableType.DATE && !(value instanceof Date)) {
            throw new IllegalArgumentException("Synchronization between variables and values has been corrupted! Value is " + value + ", " +
                    "but variable is " + currentVariable + "\nAll values before this are:\n" + singleCase.getValues());
        }
        if (value instanceof Date) singleCase.addValue(UnmodifiableDate.getInstance(((Date) value).getTime()));
        else singleCase.addValue(value);
    }

    public void setHighCompatibilityMode(boolean highCompatibilityMode) {
        writer.setHighCompatibilityMode(highCompatibilityMode);
    }

    public void addVariablesSets(Map<String, List<IVariable>> variablesSets) {
        writer.addVariablesSets(variablesSets);
    }

    public List<IVariable> getVariables() {
        return writer.getVariables();
    }

    public void setBufferProgressListener(IBufferProgressListener listener) {
        writer.setBufferProgressListener(listener);
    }

    public void setProgressListener(IProgressListener listener) {
        writer.setProgressListener(listener);
    }

    public void setCategoricalMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet) {
        writer.setCategoricalMultipleResponseSets(variablesInSet);
    }

    public void setDichotomousMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet,
                                                   Long dichotomousValue) {
        writer.setDichotomousMultipleResponseSets(variablesInSet, dichotomousValue);
    }
}
