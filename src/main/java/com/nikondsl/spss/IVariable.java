package com.nikondsl.spss;

import com.nikondsl.spss.record.Alignment;
import com.nikondsl.spss.record.DateTimeFormat;
import com.nikondsl.spss.record.Measure;
import com.nikondsl.spss.record.VariableType;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 28/5/2008
 * Time: 8:03:59
 * To change this template use File | Settings | File Templates.
 */
public interface IVariable {
    void setWidth(int width);

    void setMissingValue(IMissingValue missingValue);

    void setAlign(Alignment align);

    void setMeasure(Measure measure);

    void setDecimals(int decimals);

    void setNumericValueLabels(Map<Double, String> valueLabels);

    void setStringValueLabels(Map<String, String> valueLabels);

    void setDateFormatStyle(DateTimeFormat formatType);

    void setFormatWidth(int width);

    void setColumnWidth(int width);

    VariableType getType();
}
