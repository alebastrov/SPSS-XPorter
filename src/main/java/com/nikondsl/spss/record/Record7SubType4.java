package com.nikondsl.spss.record;


import java.io.IOException;

/**
 * Machine Floating-Point Info Record
 * ======================================
 * <p>
 * The floating-point info record, if present, has the following format:
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Data.
 * flt64               sysmis;
 * flt64               highest;
 * flt64               lowest;
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  Always set to 4.
 * <p>
 * `int32 size;'
 * Size of each piece of data in the data part, in bytes.  Always set
 * to 8.
 * <p>
 * `int32 count;'
 * Number of pieces of data in the data part.  Always set to 3.
 * <p>
 * `flt64 sysmis;'
 * The system missing value.
 * <p>
 * `flt64 highest;'
 * The value used for HIGHEST in missing values.
 * <p>
 * `flt64 lowest;'
 * The value used for LOWEST in missing values.
 */
class Record7SubType4 extends Record7 {

    Record7SubType4(String charset) {
        super(charset);
        subtypeCode = SubTypeCode.SPSSReleaseAndMachineSpecificFloatInformation;
        dataTypeCode = 8;
    }

    public void write(final ByteArray array) throws IOException {
//      Record Type Code (=7) (I4)
//      Subtype Code (=4) (I4)
//      Data Type Code (=8) (I4)
        super.write(array);
//      Number of elements of that type following (I4): 3
        array.addBytes(3);
        array.addBytes(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xef, (byte) 0xff });//float sysmis
        array.addBytes(new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xef, (byte) 0x7f });//float highest
        array.addBytes(new byte[] { (byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xef, (byte) 0xff });//float lowest
    }

}
