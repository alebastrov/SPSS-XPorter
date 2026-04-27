package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 17.05.2008
 * Time: 6:37:07
 * <p>
 * Type 7, Subtype 13:  Extended Variable Names
 * As of SPSS release 12, variable names can contain lower case alphabetics and can exceed 8 bytes in length.  In order to preserve backward compatibility, the names in the type 2 records and type 7 subtype 5 and 7 records conform to the legacy restrictions – they are entirely upper case and contain no more than 8 bytes.  Record type 7, subtype 13 contains pairs of variables names which provide the translation between legacy names and extended names.  The translations apply to all variable names in type 2 records and type 7 subtype 5 and 7 records.  They do not apply to the set names in type 7 subtype 5 and 7 records.  The contents of the subtype 13 record are:
 * Record Type Code (=7) (I4)
 * Subtype Code (=13) (I4)
 * Data Type Code (=1) (I4)
 * Number of elements of that width following (I4)
 * ASCII text.  Pairs of variable names with the pairs separated by tab characters (x’09’).  The pairs are of the form <legacy name>=<extended name>.  For example, the ASCII text might be as follows:
 * GENDER=Gender#ETHNICCA=EthnicCategory#EDUCATIO=EducationLevel
 * where the bold pound signs represent tab characters.
 * <p>
 * Long Variable Names Record
 * ==============================
 * <p>
 * If present, the long variable names record has the following format:
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Exactly `count' bytes of data.
 * char                var_name_pairs[];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  Always set to 13.
 * <p>
 * `int32 size;'
 * The size of each element in the `var_name_pairs' member. Always
 * set to 1.
 * <p>
 * `int32 count;'
 * The total number of bytes in `var_name_pairs'.
 * <p>
 * `char var_name_pairs[];'
 * A list of KEY-VALUE tuples, where KEY is the name of a variable,
 * and VALUE is its long variable name.  The KEY field is at most 8
 * bytes long and must match the name of a variable which appears in
 * the variable record (*note Variable Record::).  The VALUE field is
 * at most 64 bytes long.  The KEY and VALUE fields are separated by
 * a `=' byte.  Each tuple is separated by a byte whose value is 09.
 * There is no trailing separator following the last tuple.  The
 * total length is `count' bytes.
 */
class Record7SubType13 extends Record7 {
    private Collection<Variable> variables = Collections.emptyList();

    Record7SubType13(String charset, Collection<Variable> variables) {
        super(charset);
        this.charset = charset;
        if (variables != null && !variables.isEmpty()) this.variables = new ArrayList<Variable>(variables);
        subtypeCode = SubTypeCode.ExtendedVariableNames;
        dataTypeCode = 1;
    }

    private String getPairsOfVariables() {
        StringBuilder result = new StringBuilder(variables.size() * 256);
        for (Variable variable : variables) {
            result.append(variable.getOldNameAsString().trim());
            result.append("=");
            result.append(variable.getFullName().trim());
            result.append('\t');
        }
        if (result.length() > 0) result.setLength(result.length() - 1);
        return result.toString();
    }

    public void write(final ByteArray array) throws IOException {
        if (variables.isEmpty()) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=13) (I4)
//      Data Type Code (=1) (I4)
        super.write(array);
//      Number of elements of that width following (I4)
        String extendedNames = getPairsOfVariables();
        array.addBytes(extendedNames.getBytes(charset).length);
//    array.addBytes(1);
//      ASCII text.  Pairs of variable names with the pairs separated by tab characters (x’09’).  The pairs are of the form <legacy name>=<extended name>.  For example, the ASCII text might be as follows:
//         GENDER=Gender#ETHNICCA=EthnicCategory#EDUCATIO=EducationLevel
//      where the bold pound signs represent tab characters
        array.addBytes(extendedNames.getBytes(charset));
    }
}
