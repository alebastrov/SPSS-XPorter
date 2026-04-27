package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 14/1/2008
 * Time: 1:13:36
 * To change this template use File | Settings | File Templates.
 */
class VariableNameGenerator {
    private int value = 1;

    public synchronized String getNext() {
        return Long.toHexString(value++);
    }
}
