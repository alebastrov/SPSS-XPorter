package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 28/5/2008
 * Time: 7:58:30
 * To change this template use File | Settings | File Templates.
 */
public enum Measure {
    UNKNOWN(0), NOMINAL(1), ORDINAL(2), SCALE(3);
    private final int code;

    Measure(int code) {
        this.code = code;
    }

    public static Measure valueOf(int code) {
        if (code >= 0 && code < values().length) return values()[ code ];
        return NOMINAL;
    }

    public int getCode() {
        return code;
    }
}
