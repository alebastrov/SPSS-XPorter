package com.nikondsl.spss.record;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Type 7, Subtype 14:  Extended Strings
 * As of SPSS release 13, string variables can be up to 32767 bytes long. For compatibility with earlier releases, these extended strings are written out to a system file as “chunks” of long strings. These long strings are physically contiguous. The type 14 record has the name of the extended strings, and their original length.  The variable’s short name (8 bytes or less) is used here.
 * When an extended string variable is chunked, enough space is created for it so that 3-byte multi-byte characters can be handled without splitting a character.  All the chunks except the last are 255-byte variables; the last variable may be of any length.  The total length is based on the possibility that only 252 bytes can actually be fit into each of those 255-byte variables.  When it is necessary to place fewer than 255 bytes in a 255-byte variable, the extra space is filled with null bytes; null bytes in those positions are removed when the extended strings are reassembled.
 * <p>
 * Very Long String Record
 * ===========================
 * <p>
 * Old versions of SPSS limited string variables to a width of 255 bytes.
 * For backward compatibility with these older versions, the system file
 * format represents a string longer than 255 bytes, called a "very long
 * string", as a collection of strings no longer than 255 bytes each.  The
 * strings concatenated to make a very long string are called its
 * "segments"; for consistency, variables other than very long strings are
 * considered to have a single segment.
 * <p>
 * A very long string with a width of W has N = (W + 251) / 252
 * segments, that is, one segment for every 252 bytes of width, rounding
 * up.  It would be logical, then, for each of the segments except the
 * last to have a width of 252 and the last segment to have the remainder,
 * but this is not the case.  In fact, each segment except the last has a
 * width of 255 bytes.  The last segment has width W - (N - 1) * 252; some
 * versions of SPSS make it slightly wider, but not wide enough to make
 * the last segment require another 8 bytes of data.
 * <p>
 * Data is packed tightly into segments of a very long string, 255 bytes
 * per segment.  Because 255 bytes of segment data are allocated for every
 * 252 bytes of the very long string's width (approximately), some unused
 * space is left over at the end of the allocated segments.  Data in
 * unused space is ignored.
 * <p>
 * Example: Consider a very long string of width 20,000.  Such a very
 * long string has 20,000 / 252 = 80 (rounding up) segments.  The first 79
 * segments have width 255; the last segment has width 20,000 - 79 * 252 =
 * 92 or slightly wider (up to 96 bytes, the next multiple of 8).  The
 * very long string's data is actually stored in the 19,890 bytes in the
 * first 78 segments, plus the first 110 bytes of the 79th segment (19,890
 * + 110 = 20,000).  The remaining 145 bytes of the 79th segment and all
 * 92 bytes of the 80th segment are unused.
 * <p>
 * The very long string record explains how to stitch together segments
 * to obtain very long string data.  For each of the very long string
 * variables in the dictionary, it specifies the name of its first
 * segment's variable and the very long string variable's actual width.
 * The remaining segments immediately follow the named variable in the
 * system file's dictionary.
 * <p>
 * The very long string record, which is present only if the system file
 * contains very long string variables, has the following format:
 * <p>
 * Header.
 * int32               rec_type;
 * int32               subtype;
 * int32               size;
 * int32               count;
 * <p>
 * Exactly `count' bytes of data.
 * char                string_lengths[];
 * <p>
 * `int32 rec_type;'
 * Record type.  Always set to 7.
 * <p>
 * `int32 subtype;'
 * Record subtype.  Always set to 14.
 * <p>
 * `int32 size;'
 * The size of each element in the `string_lengths' member. Always
 * set to 1.
 * <p>
 * `int32 count;'
 * The total number of bytes in `string_lengths'.
 * <p>
 * `char string_lengths[];'
 * A list of KEY-VALUE tuples, where KEY is the name of a variable,
 * and VALUE is its length.  The KEY field is at most 8 bytes long
 * and must match the name of a variable which appears in the
 * variable record (*note Variable Record::).  The VALUE field is
 * exactly 5 bytes long. It is a zero-padded, ASCII-encoded string
 * that is the length of the variable.  The KEY and VALUE fields are
 * separated by a `=' byte.  Tuples are delimited by a two-byte
 * sequence {00, 09}.  After the last tuple, there may be a single
 * byte 00, or {00, 09}.  The total length is `count' bytes.
 */
class Record7SubType14 extends Record7 {
    private Collection<Variable> variables = Collections.emptyList();

    Record7SubType14(String charset, Collection<Variable> variables) {
        super(charset);
        if (variables != null && !variables.isEmpty()) this.variables = new ArrayList<Variable>(variables);
        subtypeCode = SubTypeCode.ExtendedStrings;
        dataTypeCode = 1;
    }

    private String getPairsOfVariables() {
        StringBuilder result = new StringBuilder(variables.size() * 256);
        for (Variable variable : variables) {
            if (variable.getType() != VariableType.STRING) continue;
            if (variable.getRealWidth() < 256) continue;
            result.append(variable.getOldNameAsString().trim());
            result.append("=");
            result.append(variable.getRealWidth());
            result.append((char) 0);
            result.append('\t');
        }
        return result.toString();
    }

    public void write(final ByteArray array) throws IOException {
        String extendedStringLengthes = getPairsOfVariables();
        if (extendedStringLengthes.length() == 0) return;
//      Record Type Code (=7) (I4)
//      Subtype Code (=14) (I4)
//      Data Type Code (=1) (I4)
        super.write(array);
//      Number of elements of that type following (I4): 8
        array.addBytes(extendedStringLengthes.getBytes(charset).length);
//      array.addBytes(1);
//      ASCII text.  Pairs of variable name/length separated by tab characters(x’09’).  The pairs are of the form <variable name>=<length>.  For example, the ASCII text might be as follows:
//        ANSWER1=556#RESPONSE2=32765
//     where the bold pound signs represent tab characters.
        array.addBytes(extendedStringLengthes.getBytes(charset));
    }
}
