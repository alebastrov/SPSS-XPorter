package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 4:08:07 To change this template use
 * File | Settings | File Templates.
 */
enum FileLayoutCode {
    LABEL_FIXED_LENGTH(1),
    LABEL_VARIABLE_LENGTH(2),
    ;
    private final int code; //label’s length is written in front of label


    FileLayoutCode(final int code) {
        this.code = code;
    }

    public int get() {
        return code;
    }

    public int getDefinition() {
        return code;
    }
}
