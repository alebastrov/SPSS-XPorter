package com.nikondsl.spss;

import com.nikondsl.spss.record.SPSSCase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ISPSSWriter {
    /**
     * xcall this method with 'true' as parameter if you know total count of SPSS cases andwant to generate data file concurrently
     * with creating SPSS cases
     *
     * @param lineByLineMode
     */
    void setLineByLineMode(boolean lineByLineMode);

    /**
     * due to raw SPSS file format you should define count of SPSS cases BEFORE adding cases
     *
     * @param numberOfCases
     */
    void setNumberOfCases(int numberOfCases);

    /**
     * it is helper method to set callback information about overall exporting process
     *
     * @param listener
     */
    void setProgressListener(IProgressListener listener);

    /**
     * it is helper method to set callback information about internal buffer status
     *
     * @param listener
     */
    void setBufferProgressListener(IBufferProgressListener listener);

    /**
     * it is helper method to set callback information about internal file buffer status
     *
     * @param listener
     */
    void setFileBufferProgressListener(IBufferProgressListener listener);

    /**
     * each SPSS data file can hold additional information about the file. Up to 64 characters in ASCII charset
     *
     * @param text
     */
    void setFileLabel(String text) throws UnsupportedEncodingException;

    /**
     * you may define your own generation time instead of automatic time setting
     *
     * @param date
     */
    void setGenerationDate(Date date);

    /**
     * you may add SPSS variables by one
     *
     * @param variable
     */
    void addVariable(IVariable variable);

    /**
     * there is main procedure for creating SPSS data file:
     * - create and add SPSS variables (a.k.a Dictionary)
     * - create and add SPSS cases (a.k.a Data Section)
     * - create and add finish pointer (a.k.a Finish Section)
     * <p>
     * This method is called with SPSS cases in automatic mode, otherwise should be called with 'null'
     *
     * @throws IOException
     */
    void generateDictionary() throws IOException;

    void generate(List<SPSSCase> cases) throws IOException;

    void generateFinishSection() throws IOException;

    /**
     * you may add all variables at once. Note: all variables will be output in order of adding
     *
     * @param variables
     */
    void addVariables(List<IVariable> variables);

    void addVariablesSets(Map<String, List<IVariable>> variablesSets);

    void close() throws IOException;

    IVariable findVariableByIndex(int variableIndex);

    void setHighCompatibilityMode(boolean highCompatibilityMode);

    List<IVariable> getVariables();

    void setDichotomousMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet, Long dichotomousValue);

    void setCategoricalMultipleResponseSets(Map<NameAndLabel, List<IVariable>> variablesInSet);
}
