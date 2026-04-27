package com.nikondsl.spss.record;

/**
 * This is the definition of date variable format, supported by SPSS
 */
public enum DateTimeFormat {
    TIRE_ddMMMyyyy(0x140b00, "dd-MMM-yyyy"),
    TIRE_ddMMMyy(0x140900, "dd-MMM-yy"),
    SLASH_yyyyMMdd(0x270a00, "yyyy/MM/dd"),
    SLASH_MMddyyyy(0x170a00, "MM/dd/yyyy"),
    SLASH_MMddyy(0x170800, "MM/dd/yy"),
    DOT_ddMMyyyy(0x260a00, "dd.MM.yyyy"),
    DOT_ddMMyy(0x260700, "dd.MM.yy"),
    yyDDD(0x180500, "yyDDD"),
    yyyyDDD(0x180700, "yyyyDDD"),
    SPACE_qQyyyy(0x1d0800, "q Q yyyy"),
    SPACE_qQyy(0x1d0600, "q Q yy"),
    SPACE_MMMyyyy(0x1c0800, "MMM yyyy"),
    SPACE_MMMyy(0x1c0600, "MMM yy"),
    SPACE_wwWKyyyy(0x1e0a00, "ww WK yyyy"),
    SPACE_wwWKyy(0x1e0800, "ww WK yy"),
    TIRE_ddMMMyyyyhhmm(0x161100, "dd-MMM-yyyy hh:mm"),
    TIRE_ddMMMyyyyhhmmss(0x161400, "dd-MMM-yyyy hh:mm:ss"),
    TIRE_ddMMMyyyyhhmmssSS(0x161702, "dd-MMM-yyyy hh:mm:ss:SS"),
    SEMICOLON_hhmm(0x150500, "hh:mm"),
    SEMICOLON_hhmmss(0x150800, "hh:mm:ss"),
    SEMICOLON_hhmmssSS(0x150b02, "hh:mm:ss:SS"),
    SEMICOLON_DDDhhmm(0x190900, "DDD hh:mm"),
    SEMICOLON_DDDhhmmss(0x190c00, "DDD hh:mm:ss"),
    SEMICOLON_DDDhhmmssSS(0x190f02, "DDD hh:mm:ss:SS"),
    EEEEEEEE(0x1a0900, "EEEEEEEE"),
    EEE(0x1a0300, "EEE"),
    MMMMMMMM(0x1b0900, "MMMMMMMM"),
    MMM(0x1b0300, "MMM");

    private final int codeType;
    private final String format;

    DateTimeFormat(int codeType, String format) {
        this.codeType = codeType;
        this.format = format;
    }

    public static DateTimeFormat create(int codeType) {
        for (DateTimeFormat elem : values()) {
            if (elem.getCodeType() == codeType) return elem;
        }
        throw new IllegalArgumentException("There is no 'Date Format' supported by SPSS with such codeType (" + codeType + ")");
    }

    public static DateTimeFormat create(String format) {
        if (format == null || format.length() == 0)
            throw new IllegalArgumentException("You have to define FormatType to one of the right date/time types.");
        for (DateTimeFormat elem : values()) {
            if (elem.getFormat().equals(format)) return elem;
        }
        throw new IllegalArgumentException("There is no 'Date Format' supported by SPSS with such format (" + format + ")");
    }

    public String getFormat() {
        return format;
    }

    public int getCodeType() {
        return codeType;
    }
}
