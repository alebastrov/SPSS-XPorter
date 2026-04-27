package com.nikondsl.spss.record;

import com.nikondsl.spss.IVariable;
import com.nikondsl.spss.NameAndLabel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 13.07.11
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
class Record7SubType7 extends Record7 {

    private final MultipleResponseSetType type;
    private final Map<NameAndLabel, List<IVariable>> sets;
    private Long dichotomousValue = 0L;

    Record7SubType7(String charset,
                    MultipleResponseSetType type,
                    Map<NameAndLabel, List<IVariable>> variablesInSet,
                    Long dichotomousValue) {
        super(charset);
        subtypeCode = SubTypeCode.MultipleResponseGroupsInformation;
        dataTypeCode = 1;
        this.charset = charset;
        this.type = type;
        this.sets = new HashMap<NameAndLabel, List<IVariable>>(variablesInSet);
        this.dichotomousValue = dichotomousValue;
    }

    public void write(final ByteArray array) throws IOException {
        if (sets.isEmpty()) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=7) (I4)
//      Data Type Code (=1) (I4)
        super.write(array);
//      Number of elements of that width following (I4)
        ByteArray local = new AutoExtendedByteArray("Record7SubType7");
        for (Map.Entry<NameAndLabel, List<IVariable>> set : sets.entrySet()) {
            local.addBytes(new byte[] { 0x24 });//$
            local.addBytes(SPSSUtil.substring(charset, set.getKey().getName(), 64));//name of set max 64 bytes
            local.addBytes(new byte[] { 0x3d });//=
            switch (type) {
                case CATEGORICAL:
                    local.addBytes(new byte[] { 0x43 });//C
                    break;
                case DICHOTOMOUS:
                    local.addBytes(new byte[] { 0x44 });//D
                    local.addBytes(("" + dichotomousValue.toString().length()).getBytes("ascii7"));//length of dichotomous value
                    local.addBytes(new byte[] { 0x20 });//space
                    local.addBytes(dichotomousValue.toString().getBytes(charset));//dichotomous value
                    break;
                default:
                    throw new IllegalArgumentException("Supported only Dichotomous and Categorical");
            }
            local.addBytes(new byte[] { 0x20 });//space
            VariableLengthStringWithoutChecking label = new VariableLengthStringWithoutChecking(charset, set.getKey().getLabel());
            local.addBytes(("" + label.get().length).getBytes("ascii7"));//label length
            local.addBytes(new byte[] { 0x20 });//space
            local.addBytes(label.get());//label
            local.addBytes(new byte[] { 0x20 });//space
            for (IVariable variable : set.getValue()) {
                local.addBytes(new VariableLengthString(charset, ((Variable) variable).getOldNameAsString().trim()).get());//variable short name
                local.addBytes(new byte[] { 0x20 });//space
            }
        }
        byte[] localArray = local.getArray();
        array.addBytes(localArray.length);
        array.addBytes(localArray);
    }
}
