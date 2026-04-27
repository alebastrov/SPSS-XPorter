package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Labels Records
 * ========================
 * <p>
 * The value label record has the following format:
 * <p>
 * int32               rec_type;
 * int32               label_count;
 * <p>
 * Repeated `label_cnt' times.
 * char                value[8];
 * char                label_len;
 * char                label[];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 3.
 * <p>
 * `int32 label_count;'
 * Number of value labels present in this record.
 * <p>
 * The remaining fields are repeated `count' times.  Each repetition
 * specifies one value label.
 * <p>
 * `char value[8];'
 * A numeric value or a short string value padded as necessary to 8
 * bytes in length.  Its type and width cannot be determined until the
 * following value label variables record (see below) is read.
 * <p>
 * `char label_len;'
 * The label's length, in bytes.
 * <p>
 * `char label[];'
 * `label_len' bytes of the actual label, followed by up to 7 bytes
 * of padding to bring `label' and `label_len' together to a multiple
 * of 8 bytes in length.
 * <p>
 * The value label record is always immediately followed by a value
 * label variables record with the following format:
 * <p>
 * int32               rec_type;
 * int32               var_count;
 * int32               vars[];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 4.
 * <p>
 * `int32 var_count;'
 * Number of variables that the associated value labels from the value
 * label record are to be applied.
 * <p>
 * `int32 vars[];'
 * A list of dictionary indexes of variables to which to apply the
 * value labels (*note Dictionary Index::).  There are `var_count'
 * elements.
 * <p>
 * String variables wider than 8 bytes may not have value labels.
 */
class Record3 extends Record {
    private final Record1 record1;
    private final List<Variable> variables;
    private final Variable variable;
    private final List<FixedLengthNumericValueLabel> numericVariableLabels = new ArrayList<FixedLengthNumericValueLabel>();
    private final List<FixedLengthStringValueLabel> stringVariableLabels = new ArrayList<FixedLengthStringValueLabel>();

    Record3(Record1 record1, List<Variable> variables, Variable variable) {
        super(record1.charset);
        this.record1 = record1;
        this.variables = variables;
        this.variable = variable;
        type = RecordType.VALUE_LABEL;
    }

    public void write(final ByteArray array) throws IOException {
        if (numericVariableLabels.isEmpty() && stringVariableLabels.isEmpty()) return;
        if (!numericVariableLabels.isEmpty() && !stringVariableLabels.isEmpty())
            throw new IllegalStateException("Both numeric and string value labels exist");
//      Record type code (=3) (I4)
        super.write(array);
        if (!numericVariableLabels.isEmpty() && stringVariableLabels.isEmpty()) {
            //      Number of value numericVariableLabels for variable (I4)
            array.addBytes(numericVariableLabels.size());
            for (FixedLengthNumericValueLabel label : numericVariableLabels) {
//        First value (OB) - double
                array.addBytes(label.getValue());
//        Structure of value label information depends on file layout code. If file layout code = 1, value label information contains just label itself (24 bytes, of which only the first 20 are used). If file layout code = 2, value label information contains label’s length L, and label. The label’s length is kept in first character, it equals integer representation of that character; label starts from second character. In this case OB space should be long enough for L+1 characters.
                label.writeLabel(array);
            }
        } else {
            //      Number of value stringVariableLabels for variable (I4)
            array.addBytes(stringVariableLabels.size());
            for (FixedLengthStringValueLabel label : stringVariableLabels) {
//        First value (OB) - double
                array.addBytes(label.getValue());
//        Structure of value label information depends on file layout code. If file layout code = 1, value label information contains just label itself (24 bytes, of which only the first 20 are used). If file layout code = 2, value label information contains label’s length L, and label. The label’s length is kept in first character, it equals integer representation of that character; label starts from second character. In this case OB space should be long enough for L+1 characters.
                label.writeLabel(array);
            }
        }
        Record4 record4 = new Record4(record1.charset, variables, variable);
        record4.write(array);
    }

    void addValueLabel(FixedLengthNumericValueLabel label) {
        numericVariableLabels.add(label);
    }

    void addValueLabel(FixedLengthStringValueLabel label) {
        stringVariableLabels.add(label);
    }
}
