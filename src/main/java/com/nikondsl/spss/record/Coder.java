package com.nikondsl.spss.record;


import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.util.Calendar;

/*********************************************************************
 *                                                                   *
 * Copyright (c) 2002-2006 by Survey Software Services, Inc.         *
 * All rights reserved.                                              *
 *                                                                   *
 * This computer program is protected by copyright law and           *
 * international treaties. Unauthorized reproduction or distribution *
 * of this program, or any portion of it, may result in severe civil *
 * and criminal penalties, and will be prosecuted to the maximum     *
 * extent possible under the law.                                    *
 *                                                                   *
 *********************************************************************/


/**
 * Title:        Survey Company Site
 * Description:
 * Copyright:    Copyright (c) 2001-2002
 * Company:      KeySurvey.com
 *
 * @author NIkon DSL
 * @version 1.0
 *
 */
@Slf4j
class Coder implements Serializable {
    private static final long APP_RUN_RANDOM_SECURE_ID = Double.doubleToLongBits(Math.random());
    private static final long serialVersionUID = -2431630616834337918L;
    private static final ThreadLocal<MessageDigest> mdVar = new ThreadLocal<MessageDigest>();

    Coder() {
    }

    public static byte[] getMD5(byte[] input) {
        MessageDigest md = mdVar.get();
        if (md == null) {
            try {
                md = MessageDigest.getInstance("MD5");
                mdVar.set(md);
            } catch (java.security.NoSuchAlgorithmException ex) {
                log.error("MD5 doesn't exist", ex);
                throw new RuntimeException(ex);
            }
        }
        md.reset();
        md.update(input);
        return md.digest();
    }

    /**
     * input is encoded as UTF-8, MD5 calculated, and returned as
     * hex String link 'FFFFFFF..FF'
     *
     * @param input String
     * @return String
     */
    public static String getMD5AsHexString(String input) {
        byte[] md5 = getMD5(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return getHexString(md5);
    }

    public static String getHexString(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2 + 1);
        for (byte aByte : bytes) ret.append(getHexString(aByte));
        return ret.toString();
    }

    private static String getHexString(byte b) {
        String res = Integer.toHexString(b & 0xff);
        if (res.length() == 2) return res;
        return "0" + res;
    }

    /**
     * This is to sign requests so no one could put arbitrary values
     * in request parameters and submit that to server.
     * Hidden fields must contain valid signature to be accepted for
     * processing.
     *
     * @param input String
     * @return String
     */
    public static String generateAppSignature(String input) {
        return getMD5AsHexString(APP_RUN_RANDOM_SECURE_ID + "-" + input);
    }

    static byte[] unhash(int keySize, byte[] src) {
        if (src.length - keySize <= 0) return EmptyArrays.emptyBytes[ 0 ];
        byte[] dest = new byte[ src.length - keySize ];
        int i = 0;
        int j = 0;
        while (i < src.length) {
            dest[ i ] = (byte) (src[ i + keySize ] ^ src[ j ]);
            i++;
            j++;
            if (i + keySize >= src.length) break;
            if (j >= keySize) j = 0;
        }
        return dest;
    }

    static String getMD5(String src) {
        byte[] md = null;
        try {
            md = Coder.getMD5(src.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ex) {
            log.error("", ex);
        }
        return md == null
                ? ""
                : getHexString(md);
    }

    static byte[] hash(int keySize,
                       byte[] src) {
        if (keySize == 0) return src;
        byte[] dest = new byte[ src.length + keySize ];
        long[] sample = new long[] { 12L, 32L, -23L, 83459031457L, -345737L, 273980L, 347865786L - 47856783467L,
                12342342L, 36792L, -27897893L, 83459057L, -347L, 2738780L, 3478686L - 4767L,
                1435342L, 36782L, -2784563L, 837L, -345737L, 270980L, 347886L - 46783467L,
                145672L, 32567L, -2345L, 834591457L, -3457L, 2738780L, 35786L - 4785467L, 535L, 64667L, 245763L, -24789L };
//    RandomSampler rs = new RandomSampler(keySize, 256L, 0L, engine);
//    rs.nextBlock(keySize, sample, 0);
        int i = 0;
        int j = 0;
        while (i < keySize) {
            dest[ i ] = (byte) sample[ i ];
            i++;
        }
        i = 0;
        while (i < src.length) {
            byte src_b = src[ i ];
            byte sample_b = (byte) sample[ j ];
            j++;
            if (j >= keySize) j = 0;
            dest[ i + keySize ] = (byte) (src_b ^ sample_b);
            i++;
        }
        return dest;
    }

    static byte[] hexStringToBytes(String hash) {
        byte[] src = new byte[ hash.length() / 2 ];
        int i = 0;
        try {
            while (i + 1 < hash.length()) {
                String hex = hash.substring(i, i + 2);
                int b = Integer.parseInt(hex, 16);
                src[ i / 2 ] = (byte) (b & 0xFF);
                i += 2;
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        return src;
    }

    /**
     * Returns alphanumeric code with given length.
     *
     * @param length int - the length of the code
     * @return String - the code
     */
    static String generateCode(int length) {
        String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(symbols.charAt((int) (Math.random() * symbols.length())));
        }
        return code.toString();
    }

    //, vogzirkzfe=EVMVI => , expiration=NEVER
    static String convertation(String text) {
        StringBuilder stringbuffer = new StringBuilder(text);
        for (int i1 = 0; i1 < stringbuffer.length(); i1++) {
            int j1 = stringbuffer.charAt(i1);
            int k1 = j1 & 0x20;
            j1 &= ~k1;
            j1 = (j1 < 65 || j1 > 90 ? j1 : ((j1 - 65) + 9) % 26 + 65) | k1;
            stringbuffer.setCharAt(i1, (char) j1);
        }
        return stringbuffer.toString();
    }

    //0c20e9a1773c4f46b8dbf1a55cffceaecd457784030837d7e17f5c8f179b032b3d11d8970b6b2034d4bfb0f50cd3eee7a32659f8373907e5d2466dbc21ae341a3eafa373df8a070a0f0c0a643dec2548f2=>897|WorldApp, Inc.|4102391290337
    static void main(String[] args) throws UnsupportedEncodingException {
        Coder coder = new Coder();
        long keyNumber = (long) Math.abs(Math.random() * 1000L) * 3L;
        Calendar calendar = Calendar.getInstance();
        calendar.set(2099, 11, 31);

        System.err.println(coder.encode(keyNumber + "|" + "WorldAPP, Inc.|" + calendar.getTime().getTime()));
        System.err.println(coder.decode("0c20e9a1773c4f46b8dbf1a55cffceaecd457784030837d7e17f5c8f179b032b3d11d8970b6b2034d4bfb0f50cd3eee7a32659f8373907e5d2466dbc21ae341a3eafa373df8a070a0f0c0a643dec2548f2"));
    }

    protected int getHash() {
        return (int) 22345091L;
    }

    String encode(String key) throws UnsupportedEncodingException {
        String hash = "";
        boolean encoded = true;
        try {
            tryDecode(key);
        } catch (CertificateException cex) {
            encoded = false;
        }
        if (encoded) return key;
        try {
            hash = getHexString(hash(32, key.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception ex) { /**/
        }
        return hash + getMD5(hash);
    }

    String tryDecode(String key) throws IllegalArgumentException, CertificateException, UnsupportedEncodingException {
        if (key == null || key.length() < 32) throw new CertificateException("non-encoded string: " + key);
        if (!key.toLowerCase().matches("^([0-9a-f][0-9a-f])+$"))
            throw new CertificateException("non-encoded string: " + key);
        String md5 = key.substring(key.length() - 32);
        String hash = key.substring(0, key.length() - 32);
        if (hash.length() > 0 &&
                md5.length() == 32 &&
                !md5.equals(getMD5(hash))) {
            throw new IllegalArgumentException("MD5 is broken");
        }
        if (hash.length() == 0) throw new CertificateException("non-encoded string: " + key);
        return new String(unhash(32, hexStringToBytes(hash)), java.nio.charset.StandardCharsets.UTF_8);
    }

    String decode(String key) throws UnsupportedEncodingException {
        //verify md5 and get unhash key
        try {
            return tryDecode(key);
        } catch (CertificateException cex) { //log.error("not-encrypted "+key,cex);// non-encoded string
            return key;
        }
    }
}
