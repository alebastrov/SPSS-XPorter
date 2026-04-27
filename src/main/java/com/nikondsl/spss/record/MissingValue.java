package com.nikondsl.spss.record;

import com.nikondsl.spss.IMissingValue;


/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 07.05.2008
 * Time: 9:43:29
 * To change this template use File | Settings | File Templates.
 */
abstract class MissingValue implements IMissingValue {
    protected MissingValueFormatCode formatCode;
    protected double[] values = new double[ 3 ];

    public abstract double[] getValues();

    public MissingValueFormatCode getFormatCode() {
        return formatCode;
    }

    public int getType() {
        return formatCode.getCode();
    }
}
