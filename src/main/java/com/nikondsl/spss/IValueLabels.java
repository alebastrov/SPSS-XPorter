package com.nikondsl.spss;

import java.util.List;
import java.util.Map;

public interface IValueLabels {
    Map<Double, String> getValueLabels();

    List<Double> getValues();
}
