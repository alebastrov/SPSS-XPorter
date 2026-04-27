package com.nikondsl.spss.record;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 5:37:07 To change this template use
 * File | Settings | File Templates.
 */
class VariableLengthString extends VariableLengthStringWithoutChecking {

    VariableLengthString(String charset, String text) throws UnsupportedEncodingException {
        super(charset, text);
        if (text == null || text.length() == 0) text = " ";
        if (text.getBytes(charset).length > 255)
            throw new IllegalArgumentException("Label length is limited by 255 characters!");
    }

}