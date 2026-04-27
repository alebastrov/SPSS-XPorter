package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 03.01.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
class VariableLengthStringWithoutChecking extends FixedLengthString {

    VariableLengthStringWithoutChecking(final String charset, final String text) throws UnsupportedEncodingException {
        super(charset,
                text == null || text.length() == 0 ? " " : text,
                text == null || text.length() == 0 ? 1 : text.getBytes(charset).length);
    }

    public byte[] getAlignedTo8() throws UnsupportedEncodingException {
        final byte[] textAsBytes = text.toString().getBytes(charset);
        int textLength = textAsBytes.length;
        int mode8 = textLength % 8;
        ByteArray result = ByteArray.createByteArray("variable length string to 8 bytes", 1 + textLength + 8 - mode8);
        //start of text
        result.addByte((byte) textLength);
        result.addBytes(textAsBytes);
        //finish of text (8-bytes chunk with used only n bytes)
        if (mode8 != 0) {
            result.addBytes(EmptyArrays.emptyBytes[ 8 - mode8 ]); //add 0 byte(s) to fit the edge
        }
        return result.getArray();
    }

    public byte[] getAlignedTo4() throws UnsupportedEncodingException {
        final byte[] textAsBytes = text.toString().getBytes(charset);
        int textLength = textAsBytes.length;
        int mod4 = textLength % 4;
        ByteArray result = ByteArray.createByteArray("variable length string to 4 bytes", 4 + textLength + 4 - mod4);
        result.addBytes(textLength);//add 4 bytes as length
        result.addBytes(textAsBytes);//add text
        if (mod4 != 0) {
            result.addBytes(EmptyArrays.spacesBytes[ 4 - mod4 ]); //add space(s) to text to fit the edge without gap
        }
        return result.getArray();
    }
}
