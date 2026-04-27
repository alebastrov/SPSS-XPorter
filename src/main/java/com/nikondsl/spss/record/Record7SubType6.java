package com.nikondsl.spss.record;


import com.nikondsl.spss.IVariable;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 29/5/2008
 * Time: 7:24:51
 * To change this template use File | Settings | File Templates.
 */
class Record7SubType6 extends Record7 {
    private final List<Variable> dateVariables;

    Record7SubType6(String charset, List<Variable> dateVariables) {
        super(charset);
        this.dateVariables = dateVariables;
        subtypeCode = SubTypeCode.TrendsDateVariableInformation;
        dataTypeCode = 4;
    }

    public void write(final ByteArray array) throws IOException {
//      Record Type Code (=7) (I4)
//      Subtype Code (=6) (I4)
//      Data Type Code (=4) (I4)
        super.write(array);
//        Number of elements of that type following (=6 + 2*NV) (I4)
        array.addBytes(6 + 2 * (dateVariables.size() + 1));//18
//        six integer words of fixed information
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//1.	QPSET - Explicit period switch (1 = "Period set via TSET PERIOD")
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//2.	PERIOD - Period
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//3.	NDATEV - Number of date variables not including DATE_
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//4.	DATINC – Inter-case increment of lowest level date variable
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//5.	DATST1 - Starting value of highest level date variable
        array.addBytes(new byte[] { 0x0, 0x0, 0xc, 0x0 });//6.	QDATE - Whether any date variables on the file (1 = "Yes")
//        three integer words of information for each date variable including DATE_
//        Information from CMNTIM on each date variable including DATE_:
        dateVariables.add(dateVariables.get(dateVariables.size() - 1));
        for (IVariable variable : dateVariables) {
            array.addBytes(new byte[] { 0x5, 0x0, 0x0, 0x0 });//1.	DATIDX - Dictionary index of date variable
            array.addBytes(new byte[] { 0xb, 0x0, 0x0, 0x0 });//2.	DATTYP - Type of date variable (2 = "Year"; 5 = "Week"; 11 = _DATE; etc.)
            array.addBytes(new byte[] { 0xc, 0x0, 0x0, 0x0 });//3.	DATPER - Periodicity of date variable
        }


    }
}
