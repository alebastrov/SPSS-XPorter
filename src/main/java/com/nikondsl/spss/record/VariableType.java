package com.nikondsl.spss.record;

/**
 * Variable types, supported by SPSS
 * <ul>
 * <li>Numeric. A variable whose values are numbers. Values are displayed in standard numeric format. The Data Editor accepts numeric values in standard format or in scientific notation.
 * <ul>
 * <li>Scientific notation. A numeric variable whose values are displayed with an embedded E and a signed power-of-10 exponent. The Data Editor accepts numeric values for such variables with or without an exponent. The exponent can be preceded by E or D with an optional sign or by the sign alone--for example, 123, 1.23E2, 1.23D2, 1.23E+2, and 1.23+2.
 * <li>Custom currency. A numeric variable whose values are displayed in one of the custom currency formats that you have defined on the Currency tab of the Options dialog box. Defined custom currency characters cannot be used in data entry but are displayed in the Data Editor.
 * <li>Comma. A numeric variable whose values are displayed with commas delimiting every three places and displayed with the period as a decimal delimiter. The Data Editor accepts numeric values for comma variables with or without commas or in scientific notation. Values cannot contain commas to the right of the decimal indicator.
 * <li>Dot. A numeric variable whose values are displayed with periods delimiting every three places and with the comma as a decimal delimiter. The Data Editor accepts numeric values for dot variables with or without periods or in scientific notation. Values cannot contain periods to the right of the decimal indicator.
 * <li>Dollar. A numeric variable displayed with a leading dollar sign ($), commas delimiting every three places, and a period as the decimal delimiter. You can enter data values with or without the leading dollar sign.
 * </ul>
 * <li>String. A variable whose values are not numeric and therefore are not used in calculations. The values can contain any characters up to the defined length. Uppercase and lowercase letters are considered distinct. This type is also known as an alphanumeric variable.
 * <li>Date. A numeric variable whose values are displayed in one of several calendar-date or clock-time formats. Select a format from the list. You can enter dates with slashes, hyphens, periods, commas, or blank spaces as delimiters. The century range for two-digit year values is determined by your Options settings (from the Edit menu, choose Options, and then click the Data tab).
 * </ul>
 */
public enum VariableType {
    NUMERIC("numeric"),
    STRING("string"),
    DATE("date"),
    COMMA("numeric comma-delimited"),
    DOT("numeric dot-delimited"),
    DOLLAR("numeric USD sign");

    private final String name;

    VariableType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
