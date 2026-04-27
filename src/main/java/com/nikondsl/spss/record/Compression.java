package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA. User: User Date: 05.03.2008 Time: 3:10:33 To change this template use
 * File | Settings | File Templates.
 */
enum Compression {
    OFF, ON;

    int getDefinition() {
        return ordinal();
    }
}
