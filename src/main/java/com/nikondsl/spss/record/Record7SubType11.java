package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Type 7, Subtype 11:  Measurement Level, Column Width, and Alignment
 * There is a “measurement level” associated with each variable.  The level can be one of the following macros:
 * _MLVL_UNK (=0) - unknown
 * _MLVL_NOM (=1) - nominal
 * _MLVL_ORD (=2) - ordinal
 * _MLVL_RAT (=3) - ratio
 * There is also a “column width”.  This is the width (in characters of average width) of the columns in which the Data Editor should display the variable and is independent of the width of the print and write formats.
 * Finally, there is an “alignment” or “justification”.  This is the way in which the displayed values are aligned in the Data Editor.  The alignment can be one of the following macros:
 * _ALIGN_LEFT (=0) – left-aligned
 * _ALIGN_RIGHT (=1) – right-aligned
 * _ALIGN_CENTER (=2)  – centered
 * The combined measurement level, column width, and column alignment information is contained in a single subtype 11 record with the following format:
 * Record Type Code (=7) (I4)
 * Subtype Code (=11) (I4)
 * Data Type Code (=_TYSI) (I4)
 * Number of elements of that width following (I4)
 * Vector of I4’s containing measurement levels, column widths, and alignments.  The vector contains three elements for each named variable - there are no entries for continuations of long strings.  The length of the vector, then, is three times the number of named variables in the file.
 * <p>
 * Variable Display Parameter Record
 * =====================================
 * <p>
 * The variable display parameter record, if present, has the following
 * format:
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Repeated `count' times.
 * int32               measure;
 * int32               width;            //Not always present.
 * int32               alignment;
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  Always set to 11.
 * <p>
 * `int32 size;'
 * The size of `int32'.  Always set to 4.
 * <p>
 * `int32 count;'
 * The number of sets of variable display parameters (ordinarily the
 * number of variables in the dictionary), times 2 or 3.
 * <p>
 * The remaining members are repeated `count' times, in the same order
 * as the variable records.  No element corresponds to variable records
 * that continue long string variables.  The meanings of these members are
 * as follows:
 * <p>
 * `int32 measure;'
 * The measurement type of the variable:
 * 1
 * Nominal Scale
 * <p>
 * 2
 * Ordinal Scale
 * <p>
 * 3
 * Continuous Scale
 * <p>
 * SPSS 14 sometimes writes a `measure' of 0.  PSPP interprets this
 * as nominal scale.
 * <p>
 * `int32 width;'
 * The width of the display column for the variable in characters.
 * <p>
 * This field is present if COUNT is 3 times the number of variables
 * in the dictionary.  It is omitted if COUNT is 2 times the number
 * of variables.
 * <p>
 * `int32 alignment;'
 * The alignment of the variable for display purposes:
 * <p>
 * 0
 * Left aligned
 * <p>
 * 1
 * Right aligned
 * <p>
 * 2
 * Centre aligned
 */
class Record7SubType11 extends Record7 {
    private List<Variable> variables = Collections.emptyList();

    Record7SubType11(String charset, Collection<Variable> variables) {
        super(charset);
        if (variables != null && !variables.isEmpty()) {
            this.variables = new ArrayList<Variable>();
            for (Variable variable : variables) {
                if (variable == null) continue;//continuos fictive variables are able to appear here
                this.variables.add(variable);
            }
        }
        subtypeCode = SubTypeCode.MeasurementLevelAndColumnWidthAndAlignment;
        dataTypeCode = 4;
    }

    public void write(final ByteArray array) throws IOException {
        if (variables.isEmpty()) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=11) (I4)
//      Data Type Code (=4) (I4)
        super.write(array);
//      Number of elements of that width following (I4)
        array.addBytes(3 * variables.size());
//      array.addBytes(1);
        for (Variable variable : variables) {
//          “measurement level”
            array.addBytes(variable.getMeasure().getCode());
//          “column width”.
            int columnWidth = variable.getColumnWidth();
            if (columnWidth == -1) columnWidth = 8;
            array.addBytes(columnWidth);
//          “alignment” or “justification”
            array.addBytes(variable.getAlign().ordinal());
        }
    }

}
