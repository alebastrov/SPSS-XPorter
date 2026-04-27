package com.nikondsl.spss.record;


import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.NameAndLabel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * The records must appear in the following order:
 * <p>
 * File header record.
 * Variable records.
 * All pairs of value labels records and value label variables records, if present.
 * Document record, if present.
 * Any of the following records, if present, in any order:
 * - Machine integer info record.
 * - Machine floating-point info record.
 * - Variable display parameter record.
 * - Long variable names record.
 * - Miscellaneous informational records.
 * Dictionary termination record.
 * Data record.
 */

/**
 Data Record
 ================

 Data records must follow all other records in the system file.  There
 must be at least one data record in every system file.

 The format of data records varies depending on whether the data is
 compressed.  Regardless, the data is arranged in a series of 8-byte
 elements.

 When data is not compressed, each element corresponds to the
 variable declared in the respective variable record (*note Variable
 Record::).  Numeric values are given in `flt64' format; string values
 are literal characters string, padded on the right when necessary to
 fill out 8-byte units.

 Compressed data is arranged in the following manner: the first 8
 bytes in the data section is divided into a series of 1-byte command
 codes.  These codes have meanings as described below:

 0
 Ignored.  If the program writing the system file accumulates
 compressed data in blocks of fixed length, 0 bytes can be used to
 pad out extra bytes remaining at the end of a fixed-size block.

 1 through 251
 A number with value CODE - BIAS, where CODE is the value of the
 compression code and BIAS is the variable `bias' from the file
 header.  For example, code 105 with bias 100.0 (the normal value)
 indicates a numeric variable of value 5.

 252
 End of file.  This code may or may not appear at the end of the
 data stream.  PSPP always outputs this code but its use is not
 required.

 253
 A numeric or string value that is not compressible.  The value is
 stored in the 8 bytes following the current block of command
 bytes.  If this value appears twice in a block of command bytes,
 then it indicates the second group of 8 bytes following the
 command bytes, and so on.

 254
 An 8-byte string value that is all spaces.

 255
 The system-missing value.

 When the end of the an 8-byte group of command bytes is reached, any
 blocks of non-compressible values indicated by code 253 are skipped,
 and the next element of command bytes is read and interpreted, until
 the end of the file or a code with value 252 is reached.
 */
@Slf4j
final class RecordsProcessor {
    private final SPSSWriter spssWriter;
    private final Record1 record1;
    private final LinkedHashSet<Variable> variables;
    private final OutputStream result;
    private boolean logWasWarn = false;

    RecordsProcessor(SPSSWriter spssWriter, Record1 record1, LinkedHashSet<Variable> variables, OutputStream result) {
        this.spssWriter = spssWriter;
        this.record1 = record1;
        this.variables = variables;
        this.result = result;
    }

    List<Variable> processingRecord2(boolean write) throws IOException {
        List<Variable> trueVariables = new ArrayList<Variable>();
        for (Variable variable : variables) {
            if (variable.getType() == VariableType.STRING && variable.getRealWidth() > 255) {
                //new scheme for very long string
                //create 'lentgh/252' variables
                int additionalVariables = variable.getRealWidth() / 252;
                //похоже, что имя переменной влияет на разбиение по переменным
                //в виде CONTI1 (То есть 5 букв оригинального имени переменной - работает нормально)
                final byte[] bytes = SPSSUtil.substring(spssWriter.charset,
                        variable.getOldNameAsString(),
                        6);
                String addVariableName = new String(bytes, spssWriter.charset);
                for (int i = 0; i < additionalVariables; i++) {
                    Variable additionalVariable = new Variable(record1.charset,
                            i == 0
                                    ? variable.getOldNameAsString()
                                    : spssWriter.generateUniqueName(addVariableName),
                            variable.getLabel(),
                            255);
                    additionalVariable.setColumnWidth(50);
                    Record2 record2 = new Record2(record1, additionalVariable, variable.getMissingValue(), trueVariables);
                    ByteArray variableBytes = ByteArray.createByteArray("record type 2 for " + additionalVariable);
                    record2.write(variableBytes);
                    if (write) {
                        result.write(variableBytes.getArray());
                    }
                }
                //the last variable
                Variable additionalVariable = StringValueProcessor.createLastVariable(spssWriter,
                        variable,
                        addVariableName + (additionalVariables - 1) + "   ",
                        variable.getRealWidth() - additionalVariables * 252);
                additionalVariable.setColumnWidth(50);
                Record2 record2 = new Record2(record1, additionalVariable, variable.getMissingValue(), trueVariables);
                ByteArray variableBytes = ByteArray.createByteArray("record type 2 for " + additionalVariable);
                record2.write(variableBytes);
                if (write) {
                    result.write(variableBytes.getArray());
                }
            } else {
                Record2 record2 = new Record2(record1, variable, variable.getMissingValue(), trueVariables);
                ByteArray variableBytes = ByteArray.createByteArray("record type 2 for " + variable);
                record2.write(variableBytes);
                if (write) {
                    result.write(variableBytes.getArray());
                }
            }
        }
        return trueVariables;
    }

    void processingRecord3(List<Variable> trueVariables) throws IOException {
        for (Variable variable : variables) {
            if (spssWriter.highCompatibilityMode && variable.getType() == VariableType.STRING) {
                if (!logWasWarn) {
                    log.info("'High compatibility mode' prohibits value labels for string variables. (It means that " +
                            variable.getOldNameAsString() + " and others variables will not have any value labels)");
                    logWasWarn = true;
                }
                continue;
            }
            Record3 record3 = new Record3(record1, trueVariables, variable);
            if (variable.getType() == VariableType.STRING) {
                //value labels for string variable
                final Map<String, String> valueLabels = variable.getStringValueLabels();
                List<String> keys = new ArrayList<String>(valueLabels.keySet());
                Collections.sort(keys);
                for (String key : keys) {
                    record3.addValueLabel(FixedLengthStringValueLabel.createValueLabel(record1, key, valueLabels.get(key)));
                }
            } else {
                //value labels for numeric variable
                final Map<Double, String> valueLabels = variable.getNumericValueLabels();
                List<Double> keys = new ArrayList<Double>(valueLabels.keySet());
                Collections.sort(keys);
                for (Double key : keys) {
                    record3.addValueLabel(FixedLengthNumericValueLabel.createValueLabel(record1, key, valueLabels.get(key)));
                }
            }
            ByteArray labelBytes = ByteArray.createByteArray("record type 3 labels for " + variable);
            record3.write(labelBytes);
            result.write(labelBytes.getArray());
        }
    }

    void processingRecord6(List<String> documentaryInformation) throws IOException {
        Record6 record6 = new Record6(record1.charset);
        for (String row : documentaryInformation) {
            FixedLengthString line = new FixedLengthString(record1.charset, row, 80);
            record6.addLine(line);
        }
        ByteArray documentaryBytes = ByteArray.createByteArray("record type 6 Documents record", record6.getLength());
        record6.write(documentaryBytes);
        result.write(documentaryBytes.getArray());
    }

    void processingRecord7SubType3(String buildNumber) throws IOException {
        Record7SubType3 record7SubType3 = new Record7SubType3(record1.charset);
        record7SubType3.setBuildNumber(buildNumber);
        ByteArray record7SubType3Bytes = ByteArray.createByteArray("record type 7.3 (release and machine-specific integer information)");
        record7SubType3.write(record7SubType3Bytes);
        result.write(record7SubType3Bytes.getArray());
    }

    void processingRecord7SubType4() throws IOException {
        Record7SubType4 record7SubType4 = new Record7SubType4(record1.charset);
        ByteArray record7SubType4Bytes = ByteArray.createByteArray("record type 7.4 (release and machine-specific floating-point)");
        record7SubType4.write(record7SubType4Bytes);
        result.write(record7SubType4Bytes.getArray());
    }

    void processingRecord7SubType5(Map<String, List<IVariable>> variablesSets) throws IOException {
        Record7SubType5 record7SubType5 = new Record7SubType5(record1.charset, variablesSets);
        ByteArray record7SubType5Bytes = ByteArray.createByteArray("record type 7.5 (Variable Sets Information)");
        record7SubType5.write(record7SubType5Bytes);
        result.write(record7SubType5Bytes.getArray());
    }

    void processingRecord7SubType6(List<Variable> variables) throws IOException {
        Record7SubType6 record7SubType6 = new Record7SubType6(record1.charset, variables);
        ByteArray record7SubType6Bytes = ByteArray.createByteArray("record type 7.6 (Trends Date Information)");
        record7SubType6.write(record7SubType6Bytes);
        result.write(record7SubType6Bytes.getArray());
    }

    void processingRecord7SubType7(MultipleResponseSetType type, Map<NameAndLabel, List<IVariable>> variablesInSet, Long dichotomousValue) throws IOException {
        Record7SubType7 record7SubType7 = new Record7SubType7(record1.charset, type, variablesInSet, dichotomousValue);
        ByteArray record7SubType7Bytes = ByteArray.createByteArray("record type 7.7 (Multiple Response Sets)");
        record7SubType7.write(record7SubType7Bytes);
        result.write(record7SubType7Bytes.getArray());
    }

    void processingRecord1() throws IOException {
        ByteArray dictionaryBytes = ByteArray.createByteArray("record type 1", 176);
        record1.write(dictionaryBytes);
        result.write(dictionaryBytes.getArray());
    }

    void processingRecord7SubType11(List<Variable> trueVariables) throws IOException {
        Record7SubType11 record7SubType11 = new Record7SubType11(record1.charset, trueVariables);
        ByteArray record7SubType11Bytes = ByteArray.createByteArray("record type 7.11 (Measurement Level, Column Width, and Alignment)");
        record7SubType11.write(record7SubType11Bytes);
        result.write(record7SubType11Bytes.getArray());
    }

    void processingRecord7SubType13() throws IOException {
        Record7SubType13 record7SubType13 = new Record7SubType13(record1.charset, variables);
        ByteArray record7SubType13Bytes = ByteArray.createByteArray("record type 7.13 (long variable names)");
        record7SubType13.write(record7SubType13Bytes);
        result.write(record7SubType13Bytes.getArray());
    }

    void processingRecord7SubType14() throws IOException {
        Record7SubType14 record7SubType14 = new Record7SubType14(record1.charset, variables);
        ByteArray record7SubType14Bytes = ByteArray.createByteArray("record type 7.14 (Extended Strings)");
        record7SubType14.write(record7SubType14Bytes);
        result.write(record7SubType14Bytes.getArray());
    }

    void processingRecord7SubType16() throws IOException {
        Record7SubType16 record7SubType16 = new Record7SubType16(record1.charset, record1);
        ByteArray record7SubType16Bytes = ByteArray.createByteArray("record type 7.16 (64-bit Number of Cases)");
        record7SubType16.write(record7SubType16Bytes);
        result.write(record7SubType16Bytes.getArray());
    }

    void processingRecord7SubType20(String charset) throws IOException {
        Record7SubType20 record7SubType20 = new Record7SubType20(charset);
        ByteArray record7SubType20Bytes = ByteArray.createByteArray("record type 7.20 (Code page)");
        record7SubType20.write(record7SubType20Bytes);
        result.write(record7SubType20Bytes.getArray());
    }

    void processingRecord999() throws IOException {
        Record999 record999 = new Record999(record1.charset);
        ByteArray finalBytes = ByteArray.createByteArray("record type 999", 8);
        record999.write(finalBytes);
        result.write(finalBytes.getArray());
    }
}
