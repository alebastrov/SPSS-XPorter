package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 6:12:06 To change this template use
 * File | Settings | File Templates.
 */
class FixedLengthVariableLabel {
    protected final String charset;
    private final FixedLengthString label;

    FixedLengthVariableLabel(String charset, String text) throws UnsupportedEncodingException {
        this.charset = charset;
        label = new FixedLengthString(charset, text, 40);
    }

    public static FixedLengthVariableLabel createVariableLabel(Record1 record1, String label) throws UnsupportedEncodingException {
        if (record1.getFileLayoutCode() == FileLayoutCode.LABEL_FIXED_LENGTH) {
            return new FixedLengthVariableLabel(record1.charset, label);
        }
        return new VariableLengthVariableLabel(record1.charset, label);
    }

    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
//        label itself (40 bytes)
        array.addBytes(label.get());
    }

    public String toString() {
        return "FixedLengthVariableLabel{" +
                "label=" + label +
                '}';
    }
}