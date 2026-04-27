package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Type7, Subtype 3: release and machine-specific  integer information
 * Record Type Code (=7) (I4)
 * Subtype Code (=3) (I4)
 * Data Type Code (=4) (I4)
 * Number of elements of that type following (I4): 8
 * Data Array: (I4)
 * 1.	Release number (example: for Release 5.8, this = 5)
 * 2.	Release sub-number (example: for 5.8, this = 8)
 * 3.	Special release identifier number (normally = 0, could be used to identify a recut of a system, for example after fixing a bug.)
 * 4.	Machine code (QA Machine Code, e.g., 271 = DEC VMS)
 * 5.	Floating-point representation code:
 * 1 = IEEE
 * 2 = IBM 370
 * 3 = VAX
 * 4 = ...
 * 6.	Compression scheme code
 * 1 = ’regular’
 * 2 = ...
 * 7.	Big/Little-endian code
 * 1 = Big-endian
 * 2 = Little-endian
 * 8.	Character representation code
 * 1 = EBCDIC
 * 2 = 7-bit ASCII
 * 3 = 8-bit ASCII (including all ASCII-based multibyte encodings)
 * 4 = DEC Kanji
 * 5 = ...
 * Items 7 and 8 of the data array are being included for documentary purposes.  With some effort, it is possible to use this information to read a foreign file.  Some implementations have used the file layout code in record 1 to guess whether integers are big- or little-endian.  That is sufficient information to read as far as the type 7, subtype 3 record.  In order to make sense of the floating point information in intermediate records, the file has to be rewound and reread after the floating point representation has been determined.
 * Beginning with SPSS 15.0, item 8 may contain the number of the Windows code page in which all text (names, labels, and data) is represented.  If the value of item 8 is greater than 99, it should be assumed to be a code page number.  More recent releases of SPSS store the actual encoding in a type 7, subtype 20, record.
 * <p>
 * Machine Integer Info Record
 * ===============================
 * <p>
 * The integer info record, if present, has the following format:
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Data.
 * int32               version_major;
 * int32               version_minor;
 * int32               version_revision;
 * int32               machine_code;
 * int32               floating_point_rep;
 * int32               compression_code;
 * int32               endianness;
 * int32               character_code;
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  Always set to 3.
 * <p>
 * `int32 size;'
 * Size of each piece of data in the data part, in bytes.  Always set
 * to 4.
 * <p>
 * `int32 count;'
 * Number of pieces of data in the data part.  Always set to 8.
 * <p>
 * `int32 version_major;'
 * PSPP major version number.  In version X.Y.Z, this is X.
 * <p>
 * `int32 version_minor;'
 * PSPP minor version number.  In version X.Y.Z, this is Y.
 * <p>
 * `int32 version_revision;'
 * PSPP version revision number.  In version X.Y.Z, this is Z.
 * <p>
 * `int32 machine_code;'
 * Machine code.  PSPP always set this field to value to -1, but other
 * values may appear.
 * <p>
 * `int32 floating_point_rep;'
 * Floating point representation code.  For IEEE 754 systems this is
 * 1.  IBM 370 sets this to 2, and DEC VAX E to 3.
 * <p>
 * `int32 compression_code;'
 * Compression code.  Always set to 1.
 * <p>
 * `int32 endianness;'
 * Machine endianness.  1 indicates big-endian, 2 indicates
 * little-endian.
 * <p>
 * `int32 character_code;'
 * Character code.  1 indicates EBCDIC, 2 indicates 7-bit ASCII, 3
 * indicates 8-bit ASCII, 4 indicates DEC Kanji.  Windows code page
 * numbers are also valid.
 */
class Record7SubType3 extends Record7 {

    private final Data data = new Data();

    Record7SubType3(String charset) {
        super(charset);
        subtypeCode = SubTypeCode.SPSSReleaseAndMachineSpecificIntegerInformation;
        dataTypeCode = 4;
    }

    public void write(final ByteArray array) throws IOException {
//      Record Type Code (=7) (I4)
//      Subtype Code (=3) (I4)
//      Data Type Code (=4) (I4)
        super.write(array);
//      Number of elements of that type following (I4): 8
        array.addBytes(8);
        array.addBytes(data.releaseNumber);
        array.addBytes(data.releaseSubNumber);
        array.addBytes(data.specialReleaseIdentifierNumber);
        array.addBytes(data.machineCode);
        array.addBytes(data.floatingPointRepresentationCode);
        array.addBytes(data.compressionSchemeCode);
        array.addBytes(data.bigLittleEndianCode);
        array.addBytes(data.characterRepresentationCode);
    }

    //like 1.2 build 12 or 1.2
    public void setBuildNumber(String text) {
        Pattern pattern = Pattern.compile("^(?i)(\\d+)\\.(\\d+)(?:\\s*build\\s+(\\d+))?$");
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) return;
        data.releaseNumber = Integer.parseInt(matcher.group(1));
        data.releaseSubNumber = Integer.parseInt(matcher.group(2));
        if (matcher.group(3) != null) {
            data.specialReleaseIdentifierNumber = Integer.parseInt(matcher.group(3));
        }
    }

    private static class Data {
        private int releaseNumber = 16;//example: for Release 5.8, this = 5
        private int releaseSubNumber = 0;//example: for 5.8, this = 8
        private int specialReleaseIdentifierNumber = 0;//normally = 0, could be used to identify a recut of a system, for example after fixing a bug.
        private final int machineCode = 720; //QA Machine Code, e.g., 271 = DEC VMS
        private final int floatingPointRepresentationCode = 1;//1 = IEEE, 2 = IBM 370, 3 = VAX, 4=...
        private final int compressionSchemeCode = 1;//1 = ’regular’, 2 = ...
        private final int bigLittleEndianCode = 2;//1 = Big-endian, 2 = Little-endian
        private final int characterRepresentationCode = 65001;//1 = EBCDIC,2 = 7-bit ASCII,3 = 8-bit ASCII (including all ASCII-based multibyte encodings),4 = DEC Kanji
    }
}
