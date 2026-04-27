package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 9:49:46
 * To change this template use File | Settings | File Templates.
 */
public final class MissingValueTwo extends MissingValue {
    public MissingValueTwo(double val1, double val2) {
        formatCode = MissingValueFormatCode.DISCRETE_MISSING_TWO_VALUES;
        values[ 0 ] = val1;
        values[ 1 ] = val2;
    }

    public double[] getValues() {
        return new double[] { values[ 0 ], values[ 1 ] };
    }
}
