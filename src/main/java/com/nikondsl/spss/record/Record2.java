package com.nikondsl.spss.record;


import com.nikondsl.spss.IMissingValue;

import java.io.IOException;
import java.util.List;

/**
 * Variable Record
 * ===================
 * <p>
 * There must be one variable record for each numeric variable and each
 * string variable with width 8 bytes or less.  String variables wider
 * than 8 bytes have one variable record for each 8 bytes, rounding up.
 * The first variable record for a long string specifies the variable's
 * correct dictionary information.  Subsequent variable records for a long
 * string are filled with dummy information: a type of -1, no variable
 * label or missing values, print and write formats that are ignored, and
 * an empty string as name.  A few system files have been encountered that
 * include a variable label on dummy variable records, so readers should
 * take care to parse dummy variable records in the same way as other
 * variable records.
 * <p>
 * The "dictionary index" of a variable is its offset in the set of
 * variable records, including dummy variable records for long string
 * variables.  The first variable record has a dictionary index of 0, the
 * second has a dictionary index of 1, and so on.
 * <p>
 * The system file format does not directly support string variables
 * wider than 255 bytes.  Such very long string variables are represented
 * by a number of narrower string variables.  *Note Very Long String
 * Record::, for details.
 * <p>
 * int32               rec_type;
 * int32               type;
 * int32               has_var_label;
 * int32               n_missing_values;
 * int32               print;
 * int32               write;
 * char                name[8];
 * <p>
 * // Present only if `has_var_label' is 1.
 * int32               label_len;
 * char                label[];
 * <p>
 * Present only if `n_missing_values' is nonzero.
 * flt64               missing_values[];
 * <p>
 * `int32 rec_type;'
 * Record type code.  Always set to 2.
 * <p>
 * `int32 type;'
 * Variable type code.  Set to 0 for a numeric variable.  For a short
 * string variable or the first part of a long string variable, this
 * is set to the width of the string.  For the second and subsequent
 * parts of a long string variable, set to -1, and the remaining
 * fields in the structure are ignored.
 * <p>
 * `int32 has_var_label;'
 * If this variable has a variable label, set to 1; otherwise, set to
 * 0.
 * <p>
 * `int32 n_missing_values;'
 * If the variable has no missing values, set to 0.  If the variable
 * has one, two, or three discrete missing values, set to 1, 2, or 3,
 * respectively.  If the variable has a range for missing variables,
 * set to -2; if the variable has a range for missing variables plus
 * a single discrete value, set to -3.
 * <p>
 * `int32 print;'
 * Print format for this variable.  See below.
 * <p>
 * `int32 write;'
 * Write format for this variable.  See below.
 * <p>
 * `char name[8];'
 * Variable name.  The variable name must begin with a capital letter
 * or the at-sign (`@').  Subsequent characters may also be digits,
 * octothorpes (`#'), dollar signs (`$'), underscores (`_'), or full
 * stops (`.').  The variable name is padded on the right with spaces.
 * <p>
 * `int32 label_len;'
 * This field is present only if `has_var_label' is set to 1.  It is
 * set to the length, in characters, of the variable label, which
 * must be a number between 0 and 120.
 * <p>
 * `char label[];'
 * This field is present only if `has_var_label' is set to 1.  It has
 * length `label_len', rounded up to the nearest multiple of 32 bits.
 * The first `label_len' characters are the variable's variable label.
 * <p>
 * `flt64 missing_values[];'
 * This field is present only if `n_missing_values' is not 0.  It has
 * the same number of elements as the absolute value of
 * `n_missing_values'.  For discrete missing values, each element
 * represents one missing value.  When a range is present, the first
 * element denotes the minimum value in the range, and the second
 * element denotes the maximum value in the range.  When a range plus
 * a value are present, the third element denotes the additional
 * discrete missing value.  HIGHEST and LOWEST are indicated as
 * described in the chapter introduction.
 * <p>
 * The `print' and `write' members of sysfile_variable are output
 * formats coded into `int32' types.  The least-significant byte of the
 * `int32' represents the number of decimal places, and the next two bytes
 * in order of increasing significance represent field width and format
 * type, respectively.  The most-significant byte is not used and should
 * be set to zero.
 * <p>
 * Format types are defined as follows:
 * <p>
 * Value   Meaning
 * ---------------------
 * 0       Not used.
 * 1       `A'
 * 2       `AHEX'
 * 3       `COMMA'
 * 4       `DOLLAR'
 * 5       `F'
 * 6       `IB'
 * 7       `PIBHEX'
 * 8       `P'
 * 9       `PIB'
 * 10      `PK'
 * 11      `RB'
 * 12      `RBHEX'
 * 13      Not used.
 * 14      Not used.
 * 15      `Z'
 * 16      `N'
 * 17      `E'
 * 18      Not used.
 * 19      Not used.
 * 20      `DATE'
 * 21      `TIME'
 * 22      `DATETIME'
 * 23      `ADATE'
 * 24      `JDATE'
 * 25      `DTIME'
 * 26      `WKDAY'
 * 27      `MONTH'
 * 28      `MOYR'
 * 29      `QYR'
 * 30      `WKYR'
 * 31      `PCT'
 * 32      `DOT'
 * 33      `CCA'
 * 34      `CCB'
 * 35      `CCC'
 * 36      `CCD'
 * 37      `CCE'
 * 38      `EDATE'
 * 39      `SDATE'
 */
class Record2 extends Record {
    private static final byte[] relx018 = { 0x1, 0x1D, 0x1, 0x0 };
    //Variable dictionary record
    private final Record1 record1;
    private final List<Variable> trueVariables;
    private final Variable variable;
    private final IMissingValue missingValue;
    private FormatCode printFormatCode; //RELX018
    private final FormatCode writeFormatCode; //RELX018

    Record2(Record1 record1, Variable variable) {
        this(record1, variable, new MissingValueAbsent(), null);
    }

    Record2(Record1 record1, Variable variable, IMissingValue missingValue, List<Variable> trueVariables) {
        super(record1.charset);
        this.record1 = record1;
        this.variable = variable;
        this.trueVariables = trueVariables;
        if (missingValue == null) missingValue = new MissingValueAbsent();
        this.missingValue = missingValue;
        type = RecordType.VARIABLE_DICTIONARY;
        if (variable.getType() == VariableType.NUMERIC) {
            printFormatCode = FormatCode.getNumberFormatCode(variable);
            if (variable.getDecimals() > 0) printFormatCode.setFormatDecimals(variable.getDecimals());
            if (variable.getFormatWidth() > 0) printFormatCode.setFormatWidth(variable.getFormatWidth());
        } else if (variable.getType() == VariableType.DOLLAR) {
            printFormatCode = FormatCode.getDollarFormatCode(variable);
        } else if (variable.getType() == VariableType.COMMA) {
            printFormatCode = FormatCode.getCommaFormatCode(variable);
        } else if (variable.getType() == VariableType.DOT) {
            printFormatCode = FormatCode.getDotFormatCode(variable);
        } else if (variable.getType() == VariableType.STRING) {
            printFormatCode = FormatCode.getStringFormatCode(variable);
        } else if (variable.getType() == VariableType.DATE) {
            if (variable.getFormatCode() == null)
                throw new IllegalArgumentException("You have to set formatCode before adding variable. Call the method setDateFormatStyle(SPSSFacade.DateTimeFormat) first.");
            printFormatCode = FormatCode.decode(variable.getDateFormatCode());
//        printFormatCode.setFormatZero(2);
            variable.setColumnWidth(printFormatCode.getWidth());
        } else if (variable.getFormatCode() != null) printFormatCode = variable.getFormatCode();
        writeFormatCode = printFormatCode;
    }

    //up to 255 bytes string variable can be output
    public void write(final ByteArray array) throws IOException {
//      Record type code (=2)(I4)
        trueVariables.add(variable);
        super.write(array);
//      Internal type code K: (I4)
//      K = 0 for numeric variables,
//      0<K<=255 for string variables of length K,
//      K = -1 for continuations of string variables.
        array.addBytes(variable.guessInternalTypeCode());
//      1 if variable label follows, 0 otherwise (I4)
        array.addBytes(variable.getLabel() == null || variable.getLabel().equals(variable.getFullName()) ? 0 : 1);
//      Missing value format code (I4):
//      0 means no missing values
//      1-3 means that number of discrete missing values
//      -2 means a missing value range
//      -3 means a range plus a value
        array.addBytes(missingValue.getType());
//      Print format code (see memo RELX018) (I4) 0x02, 0x08, 0x05, 0x00
        array.addBytes(printFormatCode.getCode());
//      Write format code (see memo RELX018) (I4) 0x02, 0x08, 0x05, 0x00
        array.addBytes(writeFormatCode.getCode());
//      Variable name (8 bytes) (UPPER CASE ONLY!)
        array.addBytes(variable.getOldName().get());
//      Variable label information (see below)
//      Structure of variable label information depends on file layout code. If file layout code = 1, variable label information contains just label itself (40 bytes). If file layout code = 2, variable label information contains variable label’s length L (I4) and variable label (L rounded up to a multiple of four bytes).
//      Value labels are represented by two types of records: label records and variable index records. All the value labels in a set of label records apply to all the variables named in the variable index record which follows.
        if (variable.getLabel() != null && variable.getLabel().length() > 0 && !variable.getLabel().equals(variable.getFullName())) {
            FixedLengthVariableLabel.createVariableLabel(record1, variable.getLabel()).writeLabel(array);
        }
//      continuations of string chunk by 8 bytes
        for (int currentLength = 8; variable.getType() == VariableType.STRING && currentLength < variable.getWidth(); currentLength += 8) {
            trueVariables.add(null);
            array.addBytes(2);
            array.addBytes(-1);//-1 for continuations of string variables.
            array.addBytes(0); //1 if variable label follows, 0 otherwise (I4)
            array.addBytes(0);//0 means no missing values
//      Print format code (see memo RELX018) (I4)
            array.addBytes(relx018);
//      Write format code (see memo RELX018) (I4)
            array.addBytes(relx018);
            FixedLengthString fictiveName = new FixedLengthString(record1.charset, "        ", 8);//8 spaces length name
            array.addBytes(fictiveName.get());
        }
        if (missingValue.getValues() == null) return;
//      Missing values (0 to 3 OB cells. The number of them is given by the absolute value of the missing value format code.)
        for (double value : missingValue.getValues()) {
            array.addBytes(value);
        }

    }

}
