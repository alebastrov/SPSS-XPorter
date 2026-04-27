package com.nikondsl.spss.record;


import java.io.IOException;

/**
 * Type 7, Subtype 16:  64-bit Number of Cases
 * As of SPSS release 14, the number of cases can exceed 2**31.  When possible, this record will contain the actual number of cases as a 64-bit integer.  If the actual number of cases fits in a 32 bit integer, record 1 will also have the actual number of cases.  If the actual number of cases exceeds 2**31, record 1 will contain –1.  The “when possible” hedging means that the number of cases can be recorded if the number is known when writing begins or the output medium supports seeks, i.e. most of the time.
 * Record 7.16 also contains an “endian” flag so that the byte ordering for this record can be detected even when an SPSS data file has been “round-tripped” by a legacy system.
 * Record Type Code (=7) (I4)
 * Subtype Code (=16) (I4)
 * Data Type Code (=8) (I4)
 * Number of elements of that width following (2) (I4)
 * Data Array:  two 64-bit integers:
 * the constant “1” in the byte order of the originating system
 * the number of cases in the same byte order as the previous item
 */
class Record7SubType16 extends Record7 {
    private final Record1 record1;

    Record7SubType16(String charset, Record1 record1) {
        super(charset);
        this.record1 = record1;
        subtypeCode = SubTypeCode.NumberOfCasesIn64bit;
        dataTypeCode = 8;
    }

    public void write(final ByteArray array) throws IOException {
        Long realNumberOfCases = record1.getRealNumberOfCases();
        if (realNumberOfCases == null) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=16) (I4)
//      Data Type Code (=8) (I4)
        super.write(array);
//      Number of elements of that type following (=2) (I4)
        array.addBytes(2);
//        Data Array:  two 64-bit integers:
//            the constant “1” in the byte order of the originating system
//            the number of cases in the same byte order as the previous item
        array.addBytes(new byte[] { 0x1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 });//constant =1
        array.addBytes(SPSSUtil.convert(realNumberOfCases));//number of cases
    }

}
