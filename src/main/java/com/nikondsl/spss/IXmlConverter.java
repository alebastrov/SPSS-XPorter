package com.nikondsl.spss;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IXmlConverter {
    void setOut(OutputStream out);

    void setCharset(String charset);

    void addDictionarySection() throws Exception;

    void addDictionarySection(int numberOfCases) throws Exception;

    void addDictionarySection(String header, String fileLabel) throws Exception;

    void addDictionarySection(String header, int numberOfVars, int numberOfCases, String fileLabel, boolean storeFormat) throws Exception;

    void addDictionarySection(String header,
                              int numberOfVars,
                              int numberOfCases,
                              int caseWeightVar,
                              int compressionSwitch,
                              String fileLabel,
                              Date date,
                              boolean storeFormat) throws Exception;

    void addDataSection() throws Exception;

    void addStringVar(String name, int width, String label) throws Exception;

    void addStringVar(String name, int length, String label, int columns, int align, int measure) throws Exception;

    void addNumericVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws Exception;

    void addNumericVar(String name, int width, int decimals, String label) throws Exception;

    void addNumericVar(String name, int width, int decimals, String label, IMissingValue missingValue) throws Exception;

    void addValueLabels(int variableIndex, IValueLabels valueLabels) throws Exception;

    void addDollarVar(String name, int width, int decimals, String label) throws Exception;

    void addDollarVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws Exception;

    void addDotVar(String name, int width, int decimals, String label) throws Exception;

    void addDotVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws Exception;

    void addCommaVar(String name, int width, int decimals, String label) throws IOException;

    void addCommaVar(String name, int width, int decimals, String label, int columns, int align, int measure) throws Exception;

    void addDateVar(String name, int dateCode, String label) throws Exception;

    void addDateVar(String name, int dateCode, String label, int columns, int align, int measure) throws Exception;

    void addDateVar(String name, String dateType, String label) throws Exception;

    void addDateVar(String name, String dateType, String label, int columns, int align, int measure) throws Exception;

    void addFinishSection() throws Exception;

    void addData(Object value) throws Exception;

    void setHighCompatibilityMode(boolean highCompatibilityMode);

    void addVariablesSets(Map<String, List<IVariable>> variablesSets);

    List<IVariable> getVariables();

    void setDichotomousMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet, Long dichotomousValue);

    void setCategoricalMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet);
}

