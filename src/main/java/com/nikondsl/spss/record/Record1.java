package com.nikondsl.spss.record;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Record 1 -- General information (176 bytes)
 * Record 1 is always present and is always the first record on the file. File layout code = 1 if variable and value labels have fixed length (40 - variable labels, 20 - value labels); file layout code = 2 if these labels have variable length and label’s length is written in front of label.  File layout code 3 is like layout code 2 except that layout 3 might have type 7, subtype 8 records.  If it is known that the implementation uses four byte integers in the data file, the file layout code can be inspected to determine whether the most significant bit in an integer is the first or the last as the valid codes are small integers beginning with 1.  The compression switch applies only to the data portion of the file; the dictionary records are never compressed.
 * File Header Record
 * ======================
 * <p>
 * The file header is always the first record in the file.  It has the
 * following format:
 * <p>
 * char                rec_type[4];
 * char                prod_name[60];
 * int32               layout_code;
 * int32               nominal_case_size;
 * int32               compressed;
 * int32               weight_index;
 * int32               ncases;
 * flt64               bias;
 * char                creation_date[9];
 * char                creation_time[8];
 * char                file_label[64];
 * char                padding[3];
 * <p>
 * `char rec_type[4];'
 * Record type code, set to `$FL2'.
 * <p>
 * `char prod_name[60];'
 * Product identification string.  This always begins with the
 * characters `@(#) SPSS DATA FILE'.  PSPP uses the remaining
 * characters to give its version and the operating system name; for
 * example, `GNU pspp 0.1.4 - sparc-sun-solaris2.5.2'.  The string is
 * truncated if it would be longer than 60 characters; otherwise it
 * is padded on the right with spaces.
 * <p>
 * `int32 layout_code;'
 * Normally set to 2, although a few system files have been spotted in
 * the wild with a value of 3 here.  PSPP use this value to determine
 * the file's integer endianness (*note System File Format::).
 * <p>
 * `int32 nominal_case_size;'
 * Number of data elements per case.  This is the number of variables,
 * except that long string variables add extra data elements (one for
 * every 8 characters after the first 8).  However, string variables
 * do not contribute to this value beyond the first 255 bytes.
 * Further, system files written by some systems set this value to
 * -1.  In general, it is unsafe for systems reading system files to
 * rely upon this value.
 * <p>
 * `int32 compressed;'
 * Set to 1 if the data in the file is compressed, 0 otherwise.
 * <p>
 * `int32 weight_index;'
 * If one of the variables in the data set is used as a weighting
 * variable, set to the dictionary index of that variable, plus 1
 * (*note Dictionary Index::).  Otherwise, set to 0.
 * <p>
 * `int32 ncases;'
 * Set to the number of cases in the file if it is known, or -1
 * otherwise.
 * <p>
 * In the general case it is not possible to determine the number of
 * cases that will be output to a system file at the time that the
 * header is written.  The way that this is dealt with is by writing
 * the entire system file, including the header, then seeking back to
 * the beginning of the file and writing just the `ncases' field.
 * For `files' in which this is not valid, the seek operation fails.
 * In this case, `ncases' remains -1.
 * <p>
 * `flt64 bias;'
 * Compression bias, ordinarily set to 100.  Only integers between `1
 * - bias' and `251 - bias' can be compressed.
 * <p>
 * By assuming that its value is 100, PSPP uses `bias' to determine
 * the file's floating-point format and endianness (*note System File
 * Format::).  If the compression bias is not 100, PSPP cannot
 * auto-detect the floating-point format and assumes that it is IEEE
 * 754 format with the same endianness as the system file's integers,
 * which is correct for all known system files.
 * <p>
 * `char creation_date[9];'
 * Date of creation of the system file, in `dd mmm yy' format, with
 * the month as standard English abbreviations, using an initial
 * capital letter and following with lowercase.  If the date is not
 * available then this field is arbitrarily set to `01 Jan 70'.
 * <p>
 * `char creation_time[8];'
 * Time of creation of the system file, in `hh:mm:ss' format and
 * using 24-hour time.  If the time is not available then this field
 * is arbitrarily set to `00:00:00'.
 * <p>
 * `char file_label[64];'
 * File label declared by the user, if any (*note FILE LABEL:
 * (pspp)FILE LABEL.).  Padded on the right with spaces.
 * <p>
 * `char padding[3];'
 * Ignored padding bytes to make the structure a multiple of 32 bits
 * in length.  Set to zeros.
 */
class Record1 extends Record {
    private static final byte[] paddingForAlignment = EmptyArrays.emptyBytes[ 3 ];
    private final FixedLengthString fileDump;
    private final FileLayoutCode fileLayoutCode;
    private int numberOfVariables = 0;
    private final Compression compressionSwitch = Compression.ON;
    private final int indexOfTheCaseWeightVariable = 0;
    private int numberOfCases = -1;
    private Long realNumberOfCases = null;//64-bit
    private final double compressionBias = 100D;
    private Date date = getCreationDate();
    private FixedLengthString fileLabel;

    Record1(String charset, String fileLabel, FileLayoutCode fileLayoutCode, SPSSWriter spssWriter) throws UnsupportedEncodingException {
        super(charset);
        type = RecordType.GENERAL_INFORMATION;
        this.fileLayoutCode = fileLayoutCode;
        this.fileLabel = new FixedLengthString(charset, "", 64);
        this.fileDump = new FixedLengthString(charset, "@(#) SPSS DATA FILE NIkon DSL(c). SPSS/PSPP XPorter v" + spssWriter.getBuildNumber().replaceAll("\\s*build\\s*\\d++", ""), 60);
        if (fileLabel != null) this.fileLabel.append(fileLabel);
    }

    FileLayoutCode getFileLayoutCode() {
        return fileLayoutCode;
    }

    void setGenerationTimestamp(final Date date) {
        this.date = new Date(date.getTime());
    }

    void setFileLabel(final String fileLabel) throws UnsupportedEncodingException {
        this.fileLabel = new FixedLengthString(charset, fileLabel, 64);
    }

    public void write(final ByteArray array) throws IOException {
//      Record type code (=’$FL2’) (four bytes)
        super.write(array);
//      An INTEGER array long enough for 60 characters, beginning with ’SPSS SYSTEM FILE.’, followed by text identifying the machine, operating system, and version of SPSS which produced this file. This string is for file dumps; it is not checked by the program.
        array.addBytes(fileDump.get());
//      File layout code (I4)
        array.addBytes(fileLayoutCode.getDefinition());
//      Number of Variables (I4)
        array.addBytes(numberOfVariables);
//      Compression Switch (0 = "not compressed; 1 = compressed") (I4)
        array.addBytes(compressionSwitch.getDefinition());
//      Index of the case weight variable, or zero (I4)
        array.addBytes(indexOfTheCaseWeightVariable);
//      Number of Cases, or -1 if unknown (I4)
        array.addBytes(numberOfCases);
//      Compression bias (F8)
        array.addBytes(compressionBias);
        FixedLengthString creationDate = new FixedLengthString(charset, new Formatter(Locale.US).format("%1$td %1$tb %1$ty", date).toString(), 9);
        FixedLengthString creationTime = new FixedLengthString(charset, new Formatter(Locale.US).format("%1$tH:%1$tM:%1$tS", date).toString(), 8);
//      Creation Date (’dd MMM yy’) (9 bytes)
        array.addBytes(creationDate.get());
//      Creation time (’hh:mm:ss’,24-hour clock) (8 bytes)
        array.addBytes(creationTime.get());
//      File label (64 bytes)
        array.addBytes(fileLabel.get());
//      Padding for alignment (3 bytes)
        array.addBytes(paddingForAlignment);
    }

    boolean isCompressed() {
        return compressionSwitch == Compression.ON;
    }

    double getCompressionBias() {
        return compressionBias;
    }

    void setNumberOfCases(int numberOfCases) {
        if (((long) numberOfCases) > (2L << 31)) {
            this.realNumberOfCases = (long) numberOfCases;
            return;
        }
        this.numberOfCases = numberOfCases;
        this.realNumberOfCases = (long) numberOfCases;
    }

    Long getRealNumberOfCases() {
        return realNumberOfCases;
    }

    void setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }

    Date getCreationDate() {
        return new Date();
    }
}
