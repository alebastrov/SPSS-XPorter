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
class ByteArray {
    protected byte[] array;
    protected int current_length = 0;
    protected int max_length;
    protected String name = "undefined";

    ByteArray(String arrayName) {
        if (arrayName != null && arrayName.length() > 0) name = arrayName;
        current_length = 0;
    }

    ByteArray(String arrayName, int totalSize) {
        this(arrayName);
        try {
            array = new byte[ totalSize ];
        } catch (OutOfMemoryError oom) {
            throw new IllegalArgumentException("No memory left for array size " + totalSize + " bytes.");
        }
        max_length = totalSize;
    }

    public static ByteArray createByteArray(String arrayName) {
        return new AutoExtendedByteArray(arrayName);
    }

    public static ByteArray createByteArray(String arrayName, int totalSize) {
        return new ByteArrayNT(arrayName, totalSize);
    }

    public int getLength() {
        return current_length;
    }

    public byte getByteAt(int i) {
        if (i >= 0 && i < current_length) return array[ i ];
        throw new IndexOutOfBoundsException("Array index [" + i + "] is out of bound [" + 0 + ".." + (current_length - 1) + "]");
    }

    public byte[] getArray() {
        byte[] result = new byte[ current_length ];
        System.arraycopy(array, 0, result, 0, current_length);
        return result;
    }

    protected void addByte(byte add) {
        if (current_length > max_length) throw new IllegalStateException("Buffer " + name + " oveflow.");
        array[ current_length ] = add;
        current_length++;
    }

    public void addBytes(byte[] add) {
        if (current_length > max_length)
            throw new IllegalStateException("Buffer " + name + " oveflow (current size " + current_length + " is greather, than " + max_length + " maximum size).");
        if (current_length + add.length > max_length)
            throw new IllegalStateException("Buffer " + name + " oveflow (current size " + current_length + " not allowed to put " + add.length + " elements. Out of bound, because maximum size is " + max_length + ")");
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
        if (getLength() == 0) throw new IllegalArgumentException("buffer is empty!");
        byte result = array[ 0 ];
        shrinkFromStart(1);
        return result;
    }

    public void shrinkFromStart(int length) {
        if (current_length == 0 || length <= 0) return;
        if (current_length == length) {
            current_length = 0;
            return;
        }
        if (current_length - length < 0) throw new IllegalArgumentException();
        System.arraycopy(array, length, array, 0, current_length - length);
        current_length -= length;
    }


    //урезает байт массив на определенную длину (отезает от конца length байт)
    //возвращает отрезанные байты
    public byte[] shrinkFromEnd(int length) {
        if (current_length == 0 || length <= 0) return EmptyArrays.emptyBytes[ 0 ];
        if (current_length <= length) {   //вернуть весь массив
            byte[] result = getArray();
            current_length = 0;
            return result;
        }
        byte[] result = new byte[ length ];
        System.arraycopy(array, current_length - length, result, 0, length);
        current_length -= length;
        return result;
    }

    public void clear() {
        current_length = 0;
    }

    public String getName() {
        return name;
    }

    public void write(OutputStream out, int off, int len) throws IOException {
        if (len + off > current_length) throw new IllegalStateException();
        out.write(array, off, len);
    }
}