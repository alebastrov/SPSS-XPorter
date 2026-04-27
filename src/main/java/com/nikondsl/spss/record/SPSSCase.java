package com.nikondsl.spss.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 18/5/2008
 * Time: 10:40:41
 * represents one row in SPSS data file
 */
public class SPSSCase {
    private final List<Object> values = Collections.synchronizedList(new ArrayList<Object>());

    public SPSSCase(Object... values) {
        if (values == null) {
            this.values.add(null);
            return;
        }
        java.util.Collections.addAll(this.values, values);
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    public void addValue(Object obj) {
        values.add(obj);
    }

    void addValueToStart(Object obj) {
        values.add(0, obj);
    }

    public Object getValue(int index) {
        if (values.isEmpty()) throw new IllegalStateException("Values was not added");
        return values.get(index);
    }

    public String toString() {
        return "SPSSCase{" +
                values +
                '}';
    }
}
