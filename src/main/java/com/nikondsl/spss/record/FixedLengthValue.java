package com.nikondsl.spss.record;


import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 6:12:06 To change this template use
 * File | Settings | File Templates.
 */
class FixedLengthValue {
    protected double value;
    private final FixedLengthString labelInformation;

    FixedLengthValue(String charset, String text) throws UnsupportedEncodingException {
        labelInformation = new FixedLengthString(charset, text, 20);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
        array.addBytes(labelInformation.get());
        array.addBytes(EmptyArrays.emptyBytes[ 4 ]);
    }
}
