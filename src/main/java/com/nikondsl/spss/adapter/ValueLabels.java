package com.nikondsl.spss.adapter;

import com.nikondsl.spss.IValueLabels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueLabels implements IValueLabels {

    Map<Double, String> labels;
    List<Double> values;

    public ValueLabels() {
        labels = new HashMap<Double, String>();
        values = new ArrayList<Double>();
    }

    public void putLabel(double d, String s) {
        if (s == null) throw new IllegalArgumentException("label cannot have empty value");
        if (s.length() > 120) s = s.substring(0, 120);
        labels.put(d, s);
        values.add(d);
    }

    public Map<Double, String> getValueLabels() {
        return Collections.unmodifiableMap(labels);
    }

    public List<Double> getValues() {
        Collections.sort(values);
        return Collections.unmodifiableList(values);
    }
}