package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 10:05:51
 * To change this template use File | Settings | File Templates.
 */
public final class MissingValueRange extends MissingValue {
    public MissingValueRange(double val1, double val2) {
        formatCode = MissingValueFormatCode.MISSING_VALUE_RANGE;
        values[ 0 ] = val1;
        values[ 1 ] = val2;
    }

    public double[] getValues() {
        return new double[] { values[ 0 ], values[ 1 ] };
    }
}
