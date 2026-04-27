package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 17.05.2008
 * Time: 8:21:57
 * To change this template use File | Settings | File Templates.
 */
class FixedLengthNumericValueLabel {
    protected final String charset;
    protected double value;
    private FixedLengthString label;

    FixedLengthNumericValueLabel(String charset, double value) {
        this.charset = charset;
        this.value = value;
    }

    FixedLengthNumericValueLabel(String charset, double value, String text) throws UnsupportedEncodingException {
        this(charset, value);
        label = new FixedLengthString(charset, text, 20);
    }

    public static FixedLengthNumericValueLabel createValueLabel(Record1 record1, double value, String label) throws UnsupportedEncodingException {
        if (record1.getFileLayoutCode() == FileLayoutCode.LABEL_FIXED_LENGTH) {
            return new FixedLengthNumericValueLabel(record1.charset, value, label);
        }
        return new VariableLengthNumericValueLabel(record1.charset, value, label);
    }

    public static FixedLengthStringValueLabel createValueLabel(Record1 record1, String value, String label) throws UnsupportedEncodingException {
        if (record1.getFileLayoutCode() == FileLayoutCode.LABEL_FIXED_LENGTH) {
            return new FixedLengthStringValueLabel(record1.charset, value, label);
        }
        return new VariableLengthStringValueLabel(record1.charset, value, label);
    }

    public double getValue() {
        return value;
    }

    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
//        label itself (20 bytes)
        array.addBytes(label.get());
//        4 empty bytes (always ignored)
        array.addBytes(EmptyArrays.emptyBytes[ 4 ]);
    }

    public String toString() {
        return "FixedLengthVariableLabel{" +
                "value=" + value +
                ", label=" + label +
                '}';
    }
}
