package com.nikondsl.spss.record;

enum FormatType {
    /**
     * Alphanumeric
     */
    A(1),
    /**
     * Alphanumeric hexadecimal
     */
    AHEX(2),
    /**
     * F Format with commas
     */
    COMMA(3),
    /**
     * Commas and floating dollar sign
     */
    DOLLAR(4),
    /**
     * Default Numeric Format
     */
    F(5),
    /**
     * Integer binary
     */
    IB(6),
    /**
     * Positive integer binary - hex
     */
    PIBHEX(7),
    /**
     * Packed decimal
     */
    P(8),
    /**
     * Positive integer binary unsigned
     */
    PIB(9),
    /**
     * Positive integer binary unsigned
     */
    PK(10),
    /**
     * Floating point binary
     */
    RB(11),
    /**
     * Floating point binary hex
     */
    RBHEX(12),
    /**
     * Zoned decimal
     */
    Z(15),
    /**
     * N Format- unsigned with leading 0s
     */
    N(16),
    /**
     * E Format- with explicit power of 10
     */
    E(17),
    /**
     * Date format dd-mmm-yyyy
     */
    DATE(20),
    /**
     * Time format hh:mm:ss.s
     */
    TIME(21),
    /**
     * Date and Time
     */
    DATE_TIME(22),
    /**
     * Date format mm/dd/yyyy
     */
    ADATE(23),
    /**
     * Julian date - yyyyddd
     */
    JDATE(24),
    /**
     * Date-time dd hh:mm:ss.s
     */
    DTIME(25),
    /**
     * Day of the week
     */
    WKDAY(26),
    /**
     * Month
     */
    MONTH(27),
    /**
     * mmm yyyy
     */
    MOYR(28),
    /**
     * q Q yyyy
     */
    QYR(29),
    /**
     * ww WK yyyy
     */
    WKYR(30),
    /**
     * Percent - F followed by %
     */
    PCT(31),
    /**
     * Like COMMA, /* switching dot for comma
     */
    DOT(32),
    /**
     * User Programmable currency format
     */
    CCA(33),
    /**
     * User Programmable currency format
     */
    CCB(34),
    /**
     * User Programmable currency format
     */
    CCC(35),
    /**
     * User Programmable currency format
     */
    CCD(36),
    /**
     * User Programmable currency format
     */
    CCE(37),
    /**
     * Date in dd.mm.yyyy style
     */
    EDATE(38),
    /**
     * Date in yyyy/mm/dd style
     */
    SDATE(39);

    private final int code;

    FormatType(int code) {
        this.code = code;
    }

    public static FormatType valueOf(int code) {
        for (FormatType formatType : values()) {
            if (formatType.getCode() == code) return formatType;
        }
        throw new IllegalArgumentException("No such FormatCode with code " + code);
    }

    public int getCode() {
        return code;
    }

}

