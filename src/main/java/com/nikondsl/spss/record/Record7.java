package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This record type allows later versions of SPSS to write files containing
 * dictionary information which earlier versions don’t expect,
 * and which those earlier versions will simply skip.
 * The Data Type Code referred to is the length of each segment in bytes.
 * The entire record must be composed of data elements of the indicated size.
 * The subtype code indicates which type of record is present
 * <p>
 * Miscellaneous Informational Records
 * ========================================
 * <p>
 * Some specific types of miscellaneous informational records are
 * documented here, but others are known to exist.  PSPP ignores unknown
 * miscellaneous informational records when reading system files.
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Exactly `size * count' bytes of data.
 * char                data[];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  May take any value.  According to Aapi
 * Ha"ma"la"inen, value 5 indicates a set of grouped variables and 6
 * indicates date info (probably related to USE).
 * <p>
 * `int32 size;'
 * Size of each piece of data in the data part.  Should have the
 * value 1, 4, or 8, for `char', `int32', and `flt64' format data,
 * respectively.
 * <p>
 * `int32 count;'
 * Number of pieces of data in the data part.
 * <p>
 * `char data[];'
 * Arbitrary data.  There must be `size' times `count' bytes of data.
 */
class Record7 extends Record {

    protected SubTypeCode subtypeCode;
    protected int dataTypeCode;
    private final List<byte[]> dataArrayOfIndicatedSizeAndType = new ArrayList<byte[]>();

    Record7(String charset) {
        super(charset);
        type = RecordType.TYPE7;
    }

    public void write(final ByteArray array) throws IOException {
//      Record Type Code (=7) (I4)
        super.write(array);
//      Subtype Code (I4)
        array.addBytes(subtypeCode.getSubCode());
//      Data Type Code (I4)
        array.addBytes(dataTypeCode);
    }
}
