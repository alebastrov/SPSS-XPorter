package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 9:46:48
 * To change this template use File | Settings | File Templates.
 */
public final class MissingValueAbsent extends MissingValue {
    public MissingValueAbsent() {
        formatCode = MissingValueFormatCode.NO_MISSING_VALUES;
    }

    public double[] getValues() {
        return null;
    }
}
