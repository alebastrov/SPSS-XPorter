package com.nikondsl.spss.record;


import java.io.IOException;

/**
 * Type 7, Subtype 20:  Character Encoding (Code Page)
 * Beginning with SPSS 15.1 (as opposed to 15.0.1), this extension record contains the name of the code page in which all text is encoded.  “All text” includes data values, variable names, variable and value labels, group names, attribute names, etc.  The code page names are represented as 7-bit ASCII text and are acceptable to the icu libraries, e.g. ISO-8859-1, UTF-8, Big5, or windows-932.  If this record is present, it supercedes the “character representation code” in the type 7, subtype 3 record.
 * Record Type Code (=7) (I4)
 * Subtype Code (=20) (I4)
 * Data Type Code (=1) (I4)
 * Number of elements of that width following (I4)
 * Data Array:  a string without a null terminator
 */
class Record7SubType20 extends Record7 {

    Record7SubType20(String charset) {
        super(charset);
        subtypeCode = SubTypeCode.CharacterEncodingOrCodePage;
        dataTypeCode = 1;
        this.charset = charset;
    }

    public void write(final ByteArray array) throws IOException {
        if (charset == null) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=10) (I4)
//      Data Type Code (=1) (I4)
        super.write(array);
//      Number of elements of that width following (I4)
        array.addBytes(charset.length());
//      Data Array:  a string without a null terminator
        array.addBytes(charset.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }
}
