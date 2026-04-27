package com.nikondsl.spss.record;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 13.03.12
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
class VariableKey {
    String uniqueName;
    String label;
    int length;

    VariableKey(String uniqueName, String label, int length) {
        this.uniqueName = uniqueName;
        this.label = label;
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof com.nikondsl.spss.record.VariableKey that)) return false;

        if (length != that.length) return false;
        if (!java.util.Objects.equals(label, that.label)) return false;
        return java.util.Objects.equals(uniqueName, that.uniqueName);
    }

    @Override
    public int hashCode() {
        int result = uniqueName != null ? uniqueName.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + length;
        return result;
    }
}
