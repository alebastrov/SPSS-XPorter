package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The document record, if present, has the following format:
 * <p>
 * int32               rec_type;
 * int32               n_lines;
 * char                lines[][80];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 6.
 * <p>
 * `int32 n_lines;'
 * Number of lines of documents present.
 * <p>
 * `char lines[][80];'
 * Document lines.  The number of elements is defined by `n_lines'.
 * Lines shorter than 80 characters are padded on the right with
 * spaces.
 */
class Record6 extends Record {

    private final List<FixedLengthString> linesOfDocumentaryInformation = new ArrayList<FixedLengthString>();

    Record6(String charset) {
        super(charset);
        type = RecordType.DOCUMENTS;
    }

    public void write(final ByteArray array) throws IOException {
        if (linesOfDocumentaryInformation.isEmpty()) return;
//      Record type code (=6) (I4)
        super.write(array);
//      Number of 80-byte lines of documentary information to follow (I4)
        array.addBytes(linesOfDocumentaryInformation.size());
//      First line, Second line, ... (80 bytes)
        for (FixedLengthString line : linesOfDocumentaryInformation) {
            array.addBytes(line.get());
        }
    }

    public void addLine(FixedLengthString line) {
        linesOfDocumentaryInformation.add(line);
    }

    public int getLength() {
        return 8 + linesOfDocumentaryInformation.size() * 80;
    }
}
