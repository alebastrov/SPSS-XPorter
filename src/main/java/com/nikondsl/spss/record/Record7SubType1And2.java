package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA. User: User Date: 13.03.2008 Time: 6:20:07 To change this template use
 * File | Settings | File Templates.
 */
class Record7SubType1And2 extends Record7 {
    private int dictionaryGenerationNumber;
    private int dataGenerationNumber;

    Record7SubType1And2(String charset) {
        super(charset);
        subtypeCode = SubTypeCode.DataEntryInformation;
        dataTypeCode = 4;
    }

    public int getNumberOfElementsOfThatTypeFollowing() {
        return 2;
    }
}
