package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 26.06.2008
 * Time: 4:02:23
 * To change this template use File | Settings | File Templates.
 */
final class VariableLengthStringValueLabel extends FixedLengthStringValueLabel {
    private final VariableLengthString label;

    VariableLengthStringValueLabel(String charset, String value, String label) throws UnsupportedEncodingException {
        super(charset, value, label);
        if (label.getBytes(charset).length > 255)
            throw new IllegalArgumentException("Too long value label (must be <255 bytes)");
        this.label = new VariableLengthString(charset, label);
    }

    @Override
    public void writeLabel(ByteArray array) throws UnsupportedEncodingException {
        final byte[] labelBytes = label.get();
//      variable label information length L
        array.addByte((byte) labelBytes.length);
//      variable label Information (length+1)%8 must be == 0
        ByteArray valueLabel = new ByteArray("temp", labelBytes.length + 8);
        valueLabel.addBytes(labelBytes);
        while ((valueLabel.getLength() + 1) % 8 != 0) valueLabel.addByte((byte) 0x00);
        array.addBytes(valueLabel.getArray());
    }

}
