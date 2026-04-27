package com.nikondsl.spss.record;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 3:00:34 To change this template use
 * File | Settings | File Templates.
 */
@Slf4j
class FixedLengthString {

    private final int length;
    protected StringBuilder text;
    protected String charset;

    FixedLengthString(String charset, CharSequence text, int length) throws UnsupportedEncodingException {
        if (text == null || text.length() == 0) text = " ";
        this.charset = charset;

        int rightEdge = length + length % 2;
        if (rightEdge <= text.length()) text = text.subSequence(0, rightEdge);
        CharSequence origText = text;
        boolean fixed = false;
        int fixedLength = text.toString().getBytes(charset).length;
        int charsPerCicle = 1;
        if (fixedLength > length) {
            while (fixedLength > length) {
                fixed = true;
                text = text.subSequence(0, text.length() - charsPerCicle);
                fixedLength = text.toString().getBytes(charset).length;
            }
        }
        this.text = new StringBuilder(text);
        StringBuilder backedUpText = new StringBuilder(text);
        while (this.text.length() < length && origText.length() > this.text.length()) {
            final char nextCharacter = origText.charAt(this.text.length());
            this.text.append(nextCharacter);
            if (origText.length() > fixedLength && this.text.toString().getBytes(charset).length < length) {
                //we can add a character safetily
                backedUpText.append(nextCharacter);
            } else break;
        }
        this.text = backedUpText;
        if (fixed) length = this.text.toString().getBytes(charset).length;
        this.length = length;
    }

    public void append(CharSequence toAppend) {
        text.append(toAppend);
    }

    public byte[] get() throws UnsupportedEncodingException {
        StringBuilder internalText = new StringBuilder(text);
        int stringLength = internalText.toString().getBytes(charset).length;
        while (stringLength < length) {
            internalText.append(" ");
            stringLength++;
        }
        internalText.setLength(length);
        if (stringLength > length) internalText.setLength(length);
        byte[] bytes = internalText.toString().getBytes(charset);
        if (bytes.length == length) return bytes;
        byte[] copy = new byte[ length ];
        System.arraycopy(bytes, 0, copy, 0, Math.min(bytes.length, length));
        return copy;
    }

    public String toString() {
        try {
            return new String(get(), charset);
        } catch (UnsupportedEncodingException ex) {
            log.error("Invalid encoding [" + charset + "] provided:", ex);
            throw new NoSuchMethodError(ex.getMessage());
        }
    }

    public String getTruncated() throws UnsupportedEncodingException {
        if (length >= text.toString().getBytes(charset).length) return text.toString();
        return text.substring(0, length);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof com.nikondsl.spss.record.FixedLengthString that)) return false;

        if (length != that.length) return false;
        if (!charset.equals(that.charset)) return false;
        return text != null ? text.toString().contentEquals(that.text) : that.text == null;
    }

    public int hashCode() {
        int result;
        result = (text != null ? text.toString().hashCode() : 0);
        result = 31 * result + length;
        result = 31 * result + charset.hashCode();
        return result;
    }
}
