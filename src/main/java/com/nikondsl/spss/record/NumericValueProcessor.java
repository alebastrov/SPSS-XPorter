package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 03.02.2011
 * Time: 13:12:08
 * To change this template use File | Settings | File Templates.
 */
public class NumericValueProcessor {

    private final SPSSWriter spssWriter;
    private final SPSSBuffer buffer;
    private final Variable variable;
    private final double compressionBias;

    public NumericValueProcessor(SPSSWriter spssWriter, SPSSBuffer buffer, Variable variable, double compressionBias) {
        this.spssWriter = spssWriter;
        this.buffer = buffer;
        this.variable = variable;
        this.compressionBias = compressionBias;
    }

    public void writeNumericValue(Object obj) {
        if (obj == null) obj = 0;
        if (obj instanceof Double) addDoubleValue((Double) obj);
        else if (obj instanceof Number) addLongValue(((Number) obj).longValue());
        else spssWriter.throwException("Cannot cast value [" + obj + "] to NUMBER for " + variable);
    }

    private void addLongValue(Long value) {
        if (value >= -99L && value <= 151L) {
            buffer.addCompressedByte((byte) (value + compressionBias));
            return;
        }
        buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode());
        buffer.addDataBytes(SPSSUtil.doubleToByte8(value));
    }

    private void addDoubleValue(Double value) {
        if (value.longValue() == value &&
                value < Long.MAX_VALUE &&
                value > Long.MIN_VALUE) {
            addLongValue(value.longValue());
            return;
        }
        buffer.addCompressedByte(CompressBufferPredefinedCodes.COMPRESS_NOT_COMPRESSED.getCode());
        buffer.addDataBytes(SPSSUtil.doubleToByte8(value));
    }
}
