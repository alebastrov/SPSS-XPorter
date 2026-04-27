package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 26.06.2008
 * Time: 4:01:48
 * To change this template use File | Settings | File Templates.
 */
class FixedLengthStringValueLabel {
    protected final String charset;
    protected FixedLengthString value;
    private final FixedLengthString label;

    FixedLengthStringValueLabel(String charset, String value, String label) throws UnsupportedEncodingException {
        this(charset, label);
        this.value = new FixedLengthString(charset, value, 8);//limited length to 8 ascii chars
    }

    private FixedLengthStringValueLabel(String charset, String label) throws UnsupportedEncodingException {
        this.charset = charset;
        this.label = new FixedLengthString(charset, label, 20);
    }

    static FixedLengthStringValueLabel createValueLabel(Record1 record1, String value, String label) throws UnsupportedEncodingException {
        if (record1.getFileLayoutCode() == FileLayoutCode.LABEL_FIXED_LENGTH) {
            return new FixedLengthStringValueLabel(record1.charset, value, label);
        }
        return new VariableLengthStringValueLabel(record1.charset, value, label);
    }

    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
//        label itself (20 bytes)
        array.addBytes(label.get());
//        4 empty bytes (always ignored)
        array.addBytes(EmptyArrays.emptyBytes[ 4 ]);
    }

    public byte[] getValue() throws UnsupportedEncodingException {
        return value.get();
    }
}
