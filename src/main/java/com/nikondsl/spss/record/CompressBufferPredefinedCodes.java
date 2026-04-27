package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 3/13/12
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public enum CompressBufferPredefinedCodes {
    COMPRESS_SKIP_CODE((byte) 0),
    COMPRESS_END_OF_FILE((byte) 252),
    COMPRESS_NOT_COMPRESSED((byte) 253),
    COMPRESS_ALL_BLANKS((byte) 254),
    COMPRESS_MISSING_VALUE((byte) 255);
    private final byte code;

    CompressBufferPredefinedCodes(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
