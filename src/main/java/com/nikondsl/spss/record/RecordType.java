package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 2:54:43 To change this template use
 * File | Settings | File Templates.
 */
enum RecordType {

    GENERAL_INFORMATION("$FL2"),//GENERAL INFORMATION
    VARIABLE_DICTIONARY(2),//VARIABLE DICTIONARY record
    VALUE_LABEL(3),//VALUE LABEL record
    VARIABLE_INDICES_FOR_VALUE_LABELS(4),//VARIABLE INDICES FOR VALUE LABELS
    CASE_ORDER_AND_UNIQUENESS_RECORD(5),//CASE ORDER AND UNIQUENESS RECORD (Note that this section has never been used or written by SPSS Inc. software.)
    DOCUMENTS(6),//DOCUMENTS record
    TYPE7(7),//Record type added after version 1.
    TYPE_EOF(999);

    private byte[] definition = null;

    RecordType(final String definition) {
        this.definition = definition.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    RecordType(final Integer code) {
        this.definition = SPSSUtil.convert(code);
    }

    public byte[] getDefinition() {
        return definition;
    }

}
