package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Record type 5 -- Case order and uniqueness record
 * Note that this section has never been used or written by SPSS Inc. software.
 * Record type code (=5)
 * Length of sort key in words (I4)
 * Uniqueness index (no of key variables required for unique identification of each case. 0 if unknown) (I4)
 * First sort key (sign indicates direction ,- means descending, magnitude is index of key variable.) (I4)
 * Second sort key (I4)
 * ...
 */
class Record5 extends Record {
    private final List<Integer> sortKeys = new ArrayList<Integer>();//sign indicates direction ,- means descending, magnitude is index of key variable.

    Record5(String charset) {
        super(charset);
        type = RecordType.CASE_ORDER_AND_UNIQUENESS_RECORD;
    }

    //no of key variables required for unique identification of each case. 0 if unknown
    public int getUniquenessIndex() {
        return sortKeys.size();
    }

    public void write(final ByteArray array) throws IOException {
        super.write(array);
    }
}
