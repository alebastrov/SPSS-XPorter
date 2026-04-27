package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 19.06.2008
 * Time: 9:59:02
 * To change this template use File | Settings | File Templates.
 */
class VariableHolderKey {
    private final int index;
    private final String variableName;

    VariableHolderKey(String variableName, int index) {
        this.variableName = variableName;
        this.index = index;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableHolderKey holderKey = (VariableHolderKey) o;

        if (index != holderKey.index) return false;
        return java.util.Objects.equals(variableName, holderKey.variableName);
    }

    public int hashCode() {
        int result;
        result = index;
        result = 31 * result + (variableName != null ? variableName.hashCode() : 0);
        return result;
    }
}
