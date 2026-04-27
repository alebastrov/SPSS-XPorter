package com.nikondsl.spss.record;


import java.io.IOException;

/**
 * Dictionary Termination Record
 * ==================================
 * <p>
 * The dictionary termination record separates all other records from the
 * data records.
 * <p>
 * int32               rec_type;
 * int32               filler;
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 999.
 * <p>
 * `int32 filler;'
 * Ignored padding.  Should be set to 0.
 */
class Record999 extends Record {

    Record999(String charset) {
        super(charset);
        type = RecordType.TYPE_EOF;
    }

    public void write(final ByteArray array) throws IOException {
        super.write(array);
        array.addBytes(0);
    }
}
