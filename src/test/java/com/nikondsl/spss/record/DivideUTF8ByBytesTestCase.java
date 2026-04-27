package com.nikondsl.spss.record;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 02.02.2011
 * Time: 10:01:46
 * To change this template use File | Settings | File Templates.
 */
public class DivideUTF8ByBytesTestCase {

    static void checkEquals(byte[] expected, byte[] toCheck) {
        assertEquals(expected.length, toCheck.length, "Lengths are different");
        for (int i = 0; i < expected.length; i++) {
            byte exp = expected[ i ];
            byte act = toCheck[ i ];
            assertEquals(exp, act, "Bytes at index [0x" + Long.toHexString(i) + "] are different.");
        }
    }

    @Test
    public void testCompare() {
        checkEquals(new byte[] {}, new byte[] {});
        checkEquals(new byte[] { 1 }, new byte[] { 1 });
        checkEquals(new byte[] { 2, 3 }, new byte[] { 2, 3 });
        try {
            checkEquals(new byte[] { 32 }, new byte[] { 23 });
            fail();
        } catch (AssertionFailedError ex) {
        }
    }
}
