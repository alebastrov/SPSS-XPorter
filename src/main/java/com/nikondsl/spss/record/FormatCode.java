package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 08.05.2008
 * Time: 4:07:58
 * To change this template use File | Settings | File Templates.
 */
public final class FormatCode {

    private int formatDecimals = 0;
    private int formatWidth;
    private FormatType formatType;
    private int formatZero = 0;

    FormatCode(int formatDecimals, int formatWidth, FormatType formatType, int formatZero) {
        this.formatDecimals = formatDecimals;
        this.formatWidth = formatWidth;
        this.formatType = formatType;
        this.formatZero = formatZero;
    }

    static FormatCode getNumberFormatCode(Variable variable) {
        return new FormatCode(2, variable.getWidth() > 2 ? variable.getWidth() : 8, FormatType.F, 0);
    }

    static FormatCode getCommaFormatCode(Variable variable) {
        return new FormatCode(2, variable.getWidth() > 2 ? variable.getWidth() : 8, FormatType.COMMA, 0);
    }

    static FormatCode getDollarFormatCode(Variable variable) {
        return new FormatCode(2, variable.getWidth() > 2 ? variable.getWidth() : 8, FormatType.DOLLAR, 0);
    }

    static FormatCode getDotFormatCode(Variable variable) {
        return new FormatCode(2, variable.getWidth() > 2 ? variable.getWidth() : 8, FormatType.DOT, 0);
    }

    static FormatCode getStringFormatCode(Variable variable) {
        return new FormatCode(0, variable.getWidth(), FormatType.A, 0);
    }

    public static FormatCode decode(int number) {
        byte decimals = (byte) (number >> 0);
        byte width = (byte) (number >> 8);
        byte codeType = (byte) (number >> 16);
        byte zero = (byte) (number >> 24);
        return new FormatCode(decimals, width, FormatType.valueOf(codeType), zero);
    }

    public int getCode() {
        return (formatZero << 24) +
                (formatType.getCode() << 16) +
                (formatWidth << 8) +
                formatDecimals;
    }

    int getWidth() {
        return formatWidth;
    }

    void setFormatWidth(int formatWidth) {
        this.formatWidth = formatWidth;
    }

    void setFormatZero(int formatZero) {
        this.formatZero = formatZero;
    }

    void setFormatDecimals(int formatDecimals) {
        this.formatDecimals = formatDecimals;
    }

    void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }
}
