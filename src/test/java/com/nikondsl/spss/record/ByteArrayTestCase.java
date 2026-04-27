package com.nikondsl.spss.record;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 02.03.12
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayTestCase {
    @Test
    public void testConstructors() {
        assertNotNull(ByteArray.createByteArray(null));
        assertNotNull(ByteArray.createByteArray(""));
        assertNotNull(ByteArray.createByteArray("test"));
        assertNotNull(ByteArray.createByteArray(null, 0));
        assertNotNull(ByteArray.createByteArray("", 0));
        assertNotNull(ByteArray.createByteArray("test", 0));
        assertNotNull(ByteArray.createByteArray(null, 100));
        assertNotNull(ByteArray.createByteArray("", 100));
        assertNotNull(ByteArray.createByteArray("test", 100));
    }

    @Test
    public void testBufferOverflow() {
        ByteArray byteArray = ByteArray.createByteArray("test", 4);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        try {
            byteArray.addByte((byte) 5);
            fail("Exception was not thrown");
        } catch (IllegalStateException ex) {
        }
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(4));
        assertEquals(0, byteArray.shrinkFromEnd(1).length);
    }

    @Test
    public void testBufferTestAEBAPutAndGet() {
        ByteArray byteArray = ByteArray.createByteArray("test");
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(4));
        assertEquals(0, byteArray.shrinkFromEnd(1).length);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] {}, byteArray.shrinkFromEnd(2));
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        byteArray.shrinkFromStart(2);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)1,(byte)2}, byteArray.shrinkFromStart(2));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)3}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)4}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{}, byteArray.shrinkFromStart(1));
        byteArray.addByte((byte) 1);
        try {
            byteArray.shrinkFromStart(2);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        byteArray.addByte((byte) 2);
        assertEquals((byte) 1, byteArray.shrinkFromStartOneByte());
        assertEquals((byte) 2, byteArray.shrinkFromStartOneByte());
        try {
            byteArray.shrinkFromStartOneByte();
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testBufferTestBANTPutAndGet() {
        ByteArray byteArray = ByteArray.createByteArray("test", 4);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(4));
        assertEquals(0, byteArray.shrinkFromEnd(1).length);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] {}, byteArray.shrinkFromEnd(2));
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        byteArray.shrinkFromStart(2);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(2);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)1,(byte)2}, byteArray.shrinkFromStart(2));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)3}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)4}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{}, byteArray.shrinkFromStart(1));

        byteArray.addByte((byte) 99);
        try {
            byteArray.shrinkFromStart(2);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        byteArray.addByte((byte) 98);
        assertEquals((byte) 99, byteArray.shrinkFromStartOneByte());
        assertEquals((byte) 98, byteArray.shrinkFromStartOneByte());
        try {
            byteArray.shrinkFromStartOneByte();
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testBufferTestBAPutAndGet() {
        ByteArray byteArray = new ByteArray("test", 4);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2, (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(4));
        assertEquals(0, byteArray.shrinkFromEnd(1).length);
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 1, (byte) 2 }, byteArray.shrinkFromEnd(2));
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] {}, byteArray.shrinkFromEnd(2));
        byteArray.addByte((byte) 1);
        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        byteArray.shrinkFromStart(2);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
        byteArray.shrinkFromStart(1);
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)1,(byte)2}, byteArray.shrinkFromStart(2));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)3}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{(byte)4}, byteArray.shrinkFromStart(1));
//        DivideUTF8ByBytesTestCase.checkEquals(new byte[]{}, byteArray.shrinkFromStart(1));
        byteArray.addByte((byte) 1);
        try {
            byteArray.shrinkFromStart(2);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        byteArray.addByte((byte) 2);
        assertEquals((byte) 1, byteArray.shrinkFromStartOneByte());
        assertEquals((byte) 2, byteArray.shrinkFromStartOneByte());
        try {
            byteArray.shrinkFromStartOneByte();
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testBufferNonOverflow() {
        ByteArray byteArray = ByteArray.createByteArray("test");
        for (int i = 0; i < 1024; i++) {
            byteArray.addByte((byte) (~((byte) i & 0xFF)));
        }

        byteArray.addByte((byte) 2);
        byteArray.addByte((byte) 3);
        byteArray.addByte((byte) 4);
        assertEquals(1024 + 3, byteArray.getLength());
        DivideUTF8ByBytesTestCase.checkEquals(new byte[] { (byte) 2, (byte) 3, (byte) 4 }, byteArray.shrinkFromEnd(3));
        byteArray.shrinkFromEnd(1024);
        assertEquals(0, byteArray.shrinkFromEnd(1).length);
    }
}
