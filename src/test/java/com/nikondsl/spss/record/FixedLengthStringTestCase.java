package com.nikondsl.spss.record;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: Apr 26, 2010
 * Time: 4:02:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixedLengthStringTestCase {

    @Test
    public void testFixedLength() throws UnsupportedEncodingException {
        FixedLengthString fls = new FixedLengthString("UTF-8", "RespondentNumber", 8);
        assertEquals("Responde", fls.getTruncated());

//    FixedLengthString fls1=new FixedLengthString("UTF-8","Кому-то обалдеть как херовато",8);
//    assertEquals("Кому".getBytes("UTF-8"), fls1.getTruncated().getBytes("UTF-8"));
//
        FixedLengthString fls2 = new FixedLengthString("UTF-8", "12345678", 3);
        assertEquals("123", fls2.getTruncated());

        FixedLengthString fls3 = new FixedLengthString("UTF-8", "12345678", 1);
        assertEquals("1", fls3.getTruncated());
//
//    FixedLengthString fls4=new FixedLengthString("UTF-8","会社概要半導体電子デバイス",8);
//    assertEquals("会社".getBytes("UTF-8"), fls4.getTruncated().getBytes("UTF-8"));
//
//    FixedLengthString fls5=new FixedLengthString("UTF-8","会社概要半導体電子デバイス",9);
//    assertEquals("会社概".getBytes("UTF-8"), fls5.getTruncated().getBytes("UTF-8"));
//
//    FixedLengthString fls6=new FixedLengthString("UTF-8","会社概要半導体電子デバイス",10);
//    assertEquals("会社概".getBytes("UTF-8"), fls6.getTruncated().getBytes("UTF-8"));
//
//    FixedLengthString fls7=new FixedLengthString("UTF-8","会社概要半導体電子デバイス",11);
//    assertEquals("会社概".getBytes("UTF-8"), fls7.getTruncated().getBytes("UTF-8"));
//
//    FixedLengthString fls8=new FixedLengthString("UTF-8","会社概要半導体電子デバイス",12);
//    assertEquals("会社概要", fls8.getTruncated());
    }
}
