package com.nikondsl.spss.record;


import com.nikondsl.spss.IVariable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is information defining the variable sets in the file.
 * A variable set is a named subset of the variables on the file, and the definitions are in the form:  <set name> = <list of variables>.
 * The set names are lexically equivalent to variable names and may be up to 64 byte in length.
 * The variable names in the list are separated by spaces and are short names, i.e. those contained in the type 2 records.
 * Each list (including the last) is terminated by a "newline" (linefeed) character or by both a "return" (carriage return) character and a newline character.
 * <p>
 * Use Variable Sets restricts the variables displayed in the Data Editor and in dialog box variable lists to the variables in the selected (checked) sets.
 */
class Record7SubType5 extends Record7 {

    private Map<String, List<IVariable>> variablesSets = Collections.emptyMap();

    Record7SubType5(String charset, Map<String, List<IVariable>> variablesSets) {
        super(charset);
        subtypeCode = SubTypeCode.VariableSetsInformation;
        dataTypeCode = 1;
        if (variablesSets != null && !variablesSets.isEmpty()) {
            this.variablesSets = new HashMap<String, List<IVariable>>(variablesSets);
        }
    }

    private String getSetsDefinition() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder(512);
        for (Map.Entry<String, List<IVariable>> entry : variablesSets.entrySet()) {
            FixedLengthString nameOfSet = new FixedLengthString(charset, entry.getKey(), 64);
            result.append(nameOfSet.getTruncated());
            result.append("=");
            for (IVariable variable : entry.getValue()) {
                result.append(((Variable) variable).getOldName().getTruncated().trim());
                result.append(" ");//delimiter of variable name
            }
            result.append("\\n");
        }
        return result.toString();
    }

    public void write(final ByteArray array) throws IOException {
        if (variablesSets.isEmpty()) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=5) (I4)
//      Data Type Code (=1) (I4)
        super.write(array);
        String setOfVariables = getSetsDefinition();
//      Number of elements of that type following (I4)
        array.addBytes(setOfVariables.getBytes(charset).length);
//      Data Array:  The definitions of the variable sets in character format.
        array.addBytes(setOfVariables.getBytes(charset));
    }

}
