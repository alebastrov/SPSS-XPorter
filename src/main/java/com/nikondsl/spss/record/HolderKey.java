package com.nikondsl.spss.record;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 19.06.2008
 * Time: 10:03:59
 * To change this template use File | Settings | File Templates.
 */
class HolderKey {
    Variable variable;
    byte[] valueBytes;

    HolderKey() {
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HolderKey holderKey = (HolderKey) o;

        if (!Arrays.equals(valueBytes, holderKey.valueBytes)) return false;
        return variable.equals(holderKey.variable);
    }

    public int hashCode() {
        int result;
        result = variable.hashCode();
        result = 31 * result + Arrays.hashCode(valueBytes);
        return result;
    }
}
