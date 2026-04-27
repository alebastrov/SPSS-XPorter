package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 9:48:11
 * To change this template use File | Settings | File Templates.
 */
public final class MissingValueOne extends MissingValue {
    public MissingValueOne(double value) {
        formatCode = MissingValueFormatCode.DISCRETE_MISSING_ONE_VALUE;
        values[ 0 ] = value;
    }

    public double[] getValues() {
        return new double[] { values[ 0 ] };
    }
}
