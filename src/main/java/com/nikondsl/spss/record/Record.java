package com.nikondsl.spss.record;


import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 2:58:50 To change this template use
 * File | Settings | File Templates.
 */
abstract class Record {
    protected RecordType type;
    protected String charset;

    public Record(String charset) {
        this.charset = charset;
    }

    public void write(final ByteArray array) throws IOException {
        array.addBytes(type.getDefinition());
    }

}
