package com.nikondsl.spss.record;


import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 6:31:09 To change this template use
 * File | Settings | File Templates.
 */
final class VariableLengthValue extends FixedLengthValue {
    private final VariableLengthString labelInformation;

    VariableLengthValue(String charset, String text) throws UnsupportedEncodingException {
        super(charset, text);
        labelInformation = new VariableLengthString(charset, text);
    }

    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
        array.addBytes(labelInformation.getAlignedTo8());
    }

}
