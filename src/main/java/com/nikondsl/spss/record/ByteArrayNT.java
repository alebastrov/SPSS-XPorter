package com.nikondsl.spss.record;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <p>Product: Key Survey's Online Survey Application.<br>
 * Author: Igor Nikonov aka NIkon DSL (nikonoff).
 * </p>
 * Date: Jan 17, 2006
 */
final class ByteArrayNT extends ByteArray {
    private int pointer = 0;

    ByteArrayNT(String arrayName) {
        super(arrayName);
    }

    ByteArrayNT(String arrayName, int totalSize) {
        super(arrayName, totalSize);
    }

    public int getLength() {
        return current_length - pointer;
    }

    public byte getByteAt(int i) {
        return array[ pointer + i ];
    }

    public byte[] getArray() {
        byte[] result = new byte[ current_length - pointer ];
        System.arraycopy(array, pointer, result, 0, current_length - pointer);
        return result;
    }

    public void addByte(byte add) {
        if (current_length >= max_length)
            throw new IllegalStateException("Buffer " + name + " overflow (current size " + current_length + " is greater or equal to maximum size " + max_length + ").");
        array[ current_length ] = add;
        current_length++;
    }

    public void addBytes(byte[] add) {
        if (current_length >= max_length)
            throw new IllegalStateException("Buffer " + name + " overflow (current size " + current_length + " is greater or equal to maximum size " + max_length + ").");
        if (current_length + add.length > max_length)
            throw new IllegalStateException("Buffer " + name + " overflow (current size (" + current_length + ") is not allowed to put " + add.length + " elements. Maximum size is " + max_length + ")");
        System.arraycopy(add, 0, array, current_length, add.length);
        current_length += add.length;
    }

    public void addBytes(Integer number) {
        addBytes(SPSSUtil.convert(number));
    }

    public void addBytes(Double number) {
        addBytes(SPSSUtil.convert(Double.doubleToLongBits(number)));
    }

    public void addBytes(Date date) throws UnsupportedEncodingException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
        addBytes(sdf.format(date).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        sdf.applyPattern("HH:mm:ss");
        addBytes(sdf.format(date).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public byte shrinkFromStartOneByte() {
        if (getLength() == 0) throw new IllegalArgumentException("Buffer is empty! Cannot shrink one more byte.");
        byte result = array[ pointer ];
        shrinkFromStart(1);
        return result;
    }

    public void shrinkFromStart(int length) {
        if (current_length == 0 || length <= 0) return;
        if (current_length == length) {
            current_length = 0;
            pointer = 0;
            return;
        }
        if (current_length - pointer - length < 0) throw new IllegalArgumentException();
        pointer += length;
        if (pointer < current_length * 0.9) return;

        System.arraycopy(array, pointer, array, 0, current_length - pointer);
        current_length -= pointer;
        pointer = 0;
    }

    //урезает байт массив на определенную длину (отезает от конца length байт)
    //возвращает отрезанные байты
    public byte[] shrinkFromEnd(int length) {
        if (current_length == 0 || length <= 0) return EmptyArrays.emptyBytes[ 0 ];
        if (current_length <= length) {   //вернуть весь массив
            byte[] result = getArray();
            current_length = 0;
            pointer = 0;
            return result;
        }
        byte[] result = new byte[ length ];
        System.arraycopy(array, current_length - length, result, 0, length);
        current_length -= length;
        return result;
    }

    public void clear() {
        current_length = 0;
        pointer = 0;
    }

    public String getName() {
        return name;
    }

    public void write(OutputStream out, int off, int len) throws IOException {
        if (pointer + len + off > current_length) throw new IllegalStateException();
        out.write(array, pointer + off, len);
    }
}