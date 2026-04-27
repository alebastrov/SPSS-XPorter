package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 21/5/2008
 * Time: 7:06:34
 * To change this template use File | Settings | File Templates.
 */
final class AutoExtendedByteArray extends ByteArray {

    AutoExtendedByteArray(String arrayName) {
        super(arrayName);
        array = new byte[ 1024 ];
    }

    @Override
    public void addByte(byte add) {
        if (current_length + 1 >= array.length) {
            byte[] extendedArray;
            //extends array (+1024 bytes)
            try {
                extendedArray = new byte[ current_length + 1 + 1024 ];
            } catch (OutOfMemoryError oom) {
                throw new IllegalArgumentException("No memory left for array size " + (current_length + 1 + 1024) + " bytes.");
            }
            System.arraycopy(array, 0, extendedArray, 0, current_length);
            array = extendedArray;
        }
        array[ current_length ] = add;
        current_length++;
    }

    @Override
    public void addBytes(byte[] add) {
        byte[] extendedArray = array;
        if (current_length + add.length >= array.length) {
            //extends array (+1024 bytes)
            try {
                extendedArray = new byte[ current_length + add.length + 1024 ];
            } catch (OutOfMemoryError oom) {
                throw new IllegalArgumentException("No memory left for array size " + (current_length + add.length + 1024) + " bytes.");
            }
            System.arraycopy(array, 0, extendedArray, 0, current_length);
        }
        System.arraycopy(add, 0, extendedArray, current_length, add.length);
        array = extendedArray;
        current_length += add.length;
    }

}
