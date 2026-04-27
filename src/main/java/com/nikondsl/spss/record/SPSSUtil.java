package com.nikondsl.spss.record;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class SPSSUtil {
    private final SPSSWriter spssWriter;

    SPSSUtil(SPSSWriter spssWriter) {
        this.spssWriter = spssWriter;
    }

    static byte[] convert(Number num) {
        if (num instanceof Double) return doubleToByte8((Double) num);
        boolean isInt = (num instanceof Integer);
        byte[] buf = new byte[ isInt ? 4 : 8 ];
        Long var = num.longValue();
        for (int i = 0; i < buf.length; i++) {
            buf[ i ] = var.byteValue();
            var >>= 8;
        }
        return buf;
    }

    static byte[] doubleToByte8(double value) {
        long lvalue = Double.doubleToLongBits(value);
        byte[] result = new byte[ 8 ];
        result[ 7 ] = (byte) ((lvalue >> 56) & 0xFF);
        result[ 6 ] = (byte) ((lvalue >> 48) & 0xFF);
        result[ 5 ] = (byte) ((lvalue >> 40) & 0xFF);
        result[ 4 ] = (byte) ((lvalue >> 32) & 0xFF);
        result[ 3 ] = (byte) ((lvalue >> 24) & 0xFF);
        result[ 2 ] = (byte) ((lvalue >> 16) & 0xFF);
        result[ 1 ] = (byte) ((lvalue >> 8) & 0xFF);
        result[ 0 ] = (byte) ((lvalue >> 0) & 0xFF);
        return result;
    }

    /**
     * http://en.wikipedia.org/wiki/UTF-8
     */
    static byte[] substringByUTF8(String text, int lengthInByte) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        byte[] bt = baos.toByteArray();
        int skip = 0;
        int i = 0;
        for (i = 0; i < bt.length; /*manual counter manipulation*/) {
            skip = 0;
            final byte b = bt[ i ];
            if (b < (byte) 128) skip = 0;
            else if (b >= (byte) 193 && b <= (byte) 223) skip = 1;
            else if (b >= (byte) 224 && b <= (byte) 239) skip = 2;
            else if (b >= (byte) 240 && b <= (byte) 244) skip = 3;
            if (i + skip + 1 > lengthInByte) break;
            i += skip + 1;
        }
        byte[] result = new byte[ i ];
        System.arraycopy(bt, 0, result, 0, i);
        return result;
    }

    static byte[] substring(String charset, String text, int lengthInBytes) throws IOException {
        if ("utf-8".equalsIgnoreCase(charset)) return substringByUTF8(text, lengthInBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
        int size = -1;
        for (Character c : text.toCharArray()) {
            size++;
            try {
                writer.append(c);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream.size() > lengthInBytes) break;
        }
        StringBuilder result = new StringBuilder();
        for (Character c : text.toCharArray()) {
            size--;
            result.append(c);
            if (size <= 0) break;
        }
        return result.toString().getBytes(charset);
    }

    static byte[] substringTillLastValidCharacterByUTF8(String text, int start, int end) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        byte[] bt = baos.toByteArray();
        int fixedStart = 0;
        int skip = 0;
        int i = 0;
        for (i = 0; i < bt.length; /*manual counter manipulation*/) {
            skip = 0;
            final byte b = bt[ i ];
            if (b < (byte) 128) skip = 0;
            else if (b >= (byte) 193 && b <= (byte) 223) skip = 1;
            else if (b >= (byte) 224 && b <= (byte) 239) skip = 2;
            else if (b >= (byte) 240 && b <= (byte) 244) skip = 3;
            final int nextPosition = i + skip + 1;
            if (i < start && nextPosition >= start) fixedStart = nextPosition;
            if (nextPosition >= end) break;
            i = nextPosition;
        }
        byte[] result = new byte[ i ];
        System.arraycopy(bt, fixedStart, result, 0, i - fixedStart);
        return result;
    }

    /**
     * €Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä
     * 3-byte char: €
     * 1-byte char: A
     * 1-byte char: b
     * 2-byte char: ب
     * 2-byte char: л
     * 2-byte char: ώ
     * 2-byte char: и
     * 2-byte char: ב
     * 2-byte char: ¥
     * 2-byte char: £
     * 3-byte char: €
     * 2-byte char: ¢
     * 3-byte char: ₣
     * 3-byte char: ₤
     * 2-byte char: §
     * 3-byte char: ₧
     * 3-byte char: ₪
     * 3-byte char: ₫
     * 3-byte char: 漢
     * 2-byte char: Ä
     * 2-byte char: ©
     * 2-byte char: ó
     * 2-byte char: í
     * 2-byte char: ß
     * 2-byte char: ä
     */
    static void main(String[] args) throws IOException {
        long t = System.currentTimeMillis();
        String text = "€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä€Abبώиב¥£€¢₡₢₣₤₥₦§₧₨₩₪₫₭₮漢Ä©óíßä";
        for (int i = 0; i < 10000; i++) {
            substringTillLastValidCharacter("utf-8",
                    text,
                    15,
                    512);
            substring("utf-8", text, 128);
        }
        System.err.println("old " + (System.currentTimeMillis() - t));

        t = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            substringTillLastValidCharacterByUTF8(text,
                    15,
                    512);
            substringByUTF8(text, 128);
        }
        System.err.println("new " + (System.currentTimeMillis() - t));

    }

    /**
     * отрезает до последнего валидного символа в кодировке
     */
    static byte[] substringTillLastValidCharacter(String charset, String text, int start, int end) throws IOException {
        if ("utf-8".equalsIgnoreCase(charset)) return substringTillLastValidCharacterByUTF8(text, start, end);
        StringBuilder sb = new StringBuilder(text.length());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int position = 0;
        for (Character c : text.toCharArray()) {
            baos.reset();
            try {
                baos.write(("" + c).getBytes(charset));
            } catch (IOException e) {
                log.error("Could not interpret character [" + c + "] in " + charset + " as bytes.\n", e);
            }
            int charBytesLength = baos.size();
            if (position >= start && charBytesLength + position < end) {
                sb.append(c);
            }
            position += charBytesLength;
            if (position >= end) break;
        }
        ByteArray result = ByteArray.createByteArray("substring");
        result.addBytes(sb.toString().getBytes(charset));
        return result.getArray();
    }

    List<byte[]> divideByLength(String charset, String text, int length, int cacheSize) throws UnsupportedEncodingException {
        SPSSUtilHolder<String, Integer> key = new SPSSUtilHolder<String, Integer>(text, length);
        List<byte[]> result = spssWriter.dividedStrings.get(key);
        if (result != null) {
            key.updateLastAccessTime();
            return result;
        }
        result = new ArrayList<byte[]>(2 * text.length() / length);

        ByteArray ba = new AutoExtendedByteArray("temp");

        byte[] restBytes = EmptyArrays.emptyBytes[ 0 ];
        int position = 0;
        for (Character c : text.toCharArray()) {
            final byte[] charBytes = c.toString().getBytes(charset);
            int charBytesLength = charBytes.length;
            if (position + charBytesLength >= length) {
                int rest = (position + charBytesLength) % length;
                ba.addBytes(charBytes);
                restBytes = ba.shrinkFromEnd(rest);
                result.add(ba.getArray());
                ba.clear();
                ba.addBytes(restBytes);
                position = restBytes.length;
                continue;
            } else ba.addBytes(charBytes);
            position += charBytesLength;
        }
        //дописываем остаток
        if (ba.getLength() + restBytes.length > 0) result.add(ba.getArray());
        if (spssWriter.dividedStrings.size() < cacheSize) spssWriter.dividedStrings.put(key, result);
        key.updateLastAccessTime();
        return result;
    }

}
