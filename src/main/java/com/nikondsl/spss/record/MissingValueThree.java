package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 10:04:15
 * To change this template use File | Settings | File Templates.
 */
public final class MissingValueThree extends MissingValue {
    public MissingValueThree(double val1, double val2, double val3) {
        formatCode = MissingValueFormatCode.DISCRETE_MISSING_TWO_VALUES;
        values[ 0 ] = val1;
        values[ 1 ] = val2;
        values[ 2 ] = val3;
    }

    public double[] getValues() {
        return new double[] { values[ 0 ], values[ 1 ], values[ 2 ] };
    }
}
