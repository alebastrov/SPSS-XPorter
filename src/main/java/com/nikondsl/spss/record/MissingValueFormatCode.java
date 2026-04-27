package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 5:03:25 To change this template use
 * File | Settings | File Templates.
 */
public enum MissingValueFormatCode {
    NO_MISSING_VALUES(0),
    DISCRETE_MISSING_ONE_VALUE(1),
    DISCRETE_MISSING_TWO_VALUES(2),
    DISCRETE_MISSING_THREE_VALUES(3),
    MISSING_VALUE_RANGE(-2),
    RANGE_PLUS_VALUE(-3);
    private final int code;

    MissingValueFormatCode(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
