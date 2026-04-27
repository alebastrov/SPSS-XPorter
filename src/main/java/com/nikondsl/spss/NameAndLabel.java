package com.nikondsl.spss;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: igor.nikonov
 * Date: 13.07.11
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
public class NameAndLabel implements Serializable {
    private static final long serialVersionUID = -7808786236314844998L;
    private final String name;
    private final String label;

    public NameAndLabel(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof com.nikondsl.spss.NameAndLabel that)) return false;

        if (!java.util.Objects.equals(name, that.name)) return false;
        return java.util.Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NameAndLabel{" +
                "name='" + name + '\'' +
                ", value='" + label + '\'' +
                '}';
    }
}
