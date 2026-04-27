package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 21.03.2008 Time: 9:55:20 To change this template use
 * File | Settings | File Templates.
 */
final class VariableLengthVariableLabel extends FixedLengthVariableLabel {

    private final VariableLengthString label;

    VariableLengthVariableLabel(String charset, String label) throws UnsupportedEncodingException {
        super(charset, label);
        this.label = new VariableLengthString(charset, label);
    }

    @Override
    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
        array.addBytes(label.getAlignedTo4());
    }

    public String toString() {
        return "VariableLengthNumericValueLabel{" +
                "label=" + label +
                '}';
    }
}
