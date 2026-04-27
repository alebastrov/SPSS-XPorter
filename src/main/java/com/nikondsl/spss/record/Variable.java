package com.nikondsl.spss.record;

import com.nikondsl.spss.IMissingValue;
import com.nikondsl.spss.IVariable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: User Date: 21.03.2008 Time: 9:24:50 To change this template use
 * File | Settings | File Templates.
 */
class Variable implements IVariable {

    FixedLengthString name;
    FixedLengthString oldName = null;
    String oldNameAsString = null;
    private final VariableType type;
    private String label = null;
    private String fullName = null;
    private Map<Double, String> numericValueLabels = Collections.emptyMap();
    private Map<String, String> stringValueLabels = Collections.emptyMap();
    private int width = 0;
    private int realWidth = 0;
    private int decimals = 2;
    private int columnWidth = -1;
    private Alignment align = Alignment.LEFT_ALIGNED;
    private Measure measure = Measure.NOMINAL;
    private FormatCode formatCode;
    private IMissingValue missingValue;
    private SPSSBuffer emptyBuffer;
    private int formatWidth;
    private final String charset;
    private final String[] indexedSpaces = new String[] {
            "        ",
            "       ",
            "      ",
            "     ",
            "    ",
            "   ",
            "  ",
            " ",
            "" };

    Variable(String charset, VariableType type, String name, String label, Map<Double, String> labels) throws UnsupportedEncodingException {
        this.charset = charset;
        if (type == VariableType.STRING)
            throw new IllegalArgumentException("Use another constructor for string variables!");
        measure = Measure.SCALE;
        setAlign(Alignment.RIGHT_ALIGNED);
        this.type = type;
        setNames(name);
        if (label != null && label.trim().length() > 0) this.label = label;
        if (labels != null && !labels.isEmpty()) setNumericValueLabels(labels);
        width = 1;
        realWidth = 1;
        formatWidth = 8;
    }

    //field width will be set automatically
    public Variable(String charset, String name, String label) throws UnsupportedEncodingException {
        this(charset, name, label, 0, null);
    }

    //field width will be set automatically
    public Variable(String charset, String name, String label, Map<String, String> labels) throws UnsupportedEncodingException {
        this(charset, name, label, 0, labels);
    }

    public Variable(String charset, String name, String label, int width) throws UnsupportedEncodingException {
        this(charset, name, label, width, null);
    }

    public Variable(String charset, String name, String label, int width, Map<String, String> labels) throws UnsupportedEncodingException {
        this.charset = charset;
        this.type = VariableType.STRING;
        setNames(name);
        if (label != null && label.trim().length() > 0) this.label = label;
        this.width = width < 0 ? 0 : width > 255 ? 255 : width;
        this.realWidth = width;
        if (labels != null && !labels.isEmpty()) setStringValueLabels(labels);
    }

    private void setNames(String name) throws UnsupportedEncodingException {
        this.fullName = name;
        name = name.replaceAll("\\s++", "");
        name = name.replaceAll("_", " ");
        if (name.length() < 8) name += indexedSpaces[ name.length() ];
        this.name = new FixedLengthString(charset, name, 8);
        oldName = new FixedLengthString(charset, this.name.toString().toUpperCase().replaceAll("\\s+$", "").replaceAll("\\s+", ""), 8);
        oldNameAsString = oldName.toString();
    }

    public FormatCode getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(FormatCode formatCode) {
        this.formatCode = formatCode;
    }

    public String getLabel() {
        return label;
    }

    public void setDateFormatStyle(DateTimeFormat formatType) {
        if (type != VariableType.DATE)
            throw new IllegalStateException("That variable type " + type + " does not allow to set date style");
        this.setFormatCode(FormatCode.decode(DateTimeFormat.create(formatType.getFormat()).getCodeType()));
    }

    public Integer guessInternalTypeCode() {
        //return variable.getType()== SPSSFacade.VariableType.STRING?variable.getWidth():0;
        // (0) -numeric, (1-255) -fixed length string, (-1) -continuations
        if (type == VariableType.STRING) {
            if (width == 0)
                throw new IllegalArgumentException("Width of " + this + " does not set! For automatically set of variable width you have to add at least one case.");
            return width <= 255 ? width : 255;
        }
        return 0;//1 character length for types NUMERIC, DOT, COMMA, DOLLAR
    }

    public Map<Double, String> getNumericValueLabels() {
        return Collections.unmodifiableMap(numericValueLabels);
    }

    public void setNumericValueLabels(Map<Double, String> numericValueLabels) {
        if (this.getType() == VariableType.STRING)
            throw new IllegalArgumentException("You can set only string value labels for " + this);
        if (numericValueLabels == null || numericValueLabels.isEmpty()) return;
        this.numericValueLabels = new HashMap<Double, String>();
        //check for length > 255
        try {
            for (Map.Entry<Double, String> entry : numericValueLabels.entrySet()) {
                if (!SPSSWriter.ignoreLimitations && entry.getValue().getBytes(charset).length > 255) {
                    throw new IllegalArgumentException("Value label length is limited by 255 bytes, " +
                            "but was " + entry.getValue().getBytes(charset).length + " for [" +
                            entry.getValue() + "]");
                }
                if (SPSSWriter.ignoreLimitations && entry.getValue().getBytes(charset).length > 255) {
                    this.numericValueLabels.put(entry.getKey(), new String(SPSSUtil.substringTillLastValidCharacter(charset, entry.getValue(), 0, 255), charset));
                } else {
                    this.numericValueLabels.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public Map<String, String> getStringValueLabels() {
        return Collections.unmodifiableMap(stringValueLabels);
    }

    public void setStringValueLabels(Map<String, String> stringValueLabels) {
        if (this.getType() != VariableType.STRING)
            throw new IllegalArgumentException("You can set only numeric value labels for " + this);
        if (stringValueLabels == null || stringValueLabels.isEmpty()) return;
        this.stringValueLabels = new HashMap<String, String>();
        //check for length > 255
        try {
            for (Map.Entry<String, String> entry : stringValueLabels.entrySet()) {
                if (!SPSSWriter.ignoreLimitations && entry.getValue().getBytes(charset).length > 255) {
                    throw new IllegalArgumentException("Value label length is limited by 255 bytes, " +
                            "but was " + entry.getValue().getBytes(charset).length + " for [" +
                            entry.getValue() + "]");
                }
                if (SPSSWriter.ignoreLimitations && entry.getValue().getBytes(charset).length > 255) {
                    this.stringValueLabels.put(entry.getKey(), new String(SPSSUtil.substringTillLastValidCharacter(charset, entry.getValue(), 0, 255), charset));
                } else {
                    this.stringValueLabels.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public VariableType getType() {
        return type;
    }

    public int getWidth() {
        if (type == VariableType.NUMERIC ||
                type == VariableType.DOLLAR) return 8;
        if (type == VariableType.DATE) return getFormatCode().getWidth();
        return width;
    }

    public void setWidth(int width) {
        if (width > 255) {
            this.realWidth = width;
            this.width = 255;
            formatWidth = 255;
        } else {
            this.width = width;
            this.realWidth = width;
            formatWidth = width;
        }
    }

    public int getRealWidth() {
        return realWidth;
    }

    public IMissingValue getMissingValue() {
        return missingValue;
    }

    public void setMissingValue(IMissingValue missingValue) {
        this.missingValue = missingValue;
    }

    public void setName(String name) throws UnsupportedEncodingException {
        setNames(name);
    }

    public String getOldNameAsString() {
        return oldNameAsString;
    }

    public FixedLengthString getOldName() {
        return oldName;
    }

    public String getFullName() {
        return fullName.replaceAll("\\t+|\\s+", "");
    }

    Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    Alignment getAlign() {
        return align;
    }

    public void setAlign(Alignment align) {
        this.align = align;
    }

    int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int width) {
        columnWidth = width;
    }

    int getDateFormatCode() {
        return formatCode.getCode();
    }

    SPSSBuffer getEmptyBuffer() {
        return emptyBuffer;//for caching purposes
    }

    void setEmptyBuffer(SPSSBuffer emptyBuffer) {
        this.emptyBuffer = emptyBuffer;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;
        if (type != variable.type) return false;
        if (!java.util.Objects.equals(oldName, variable.oldName)) return false;
        if (!java.util.Objects.equals(oldNameAsString, variable.oldNameAsString))
            return false;
        if (!java.util.Objects.equals(name, variable.name)) return false;
        if (!java.util.Objects.equals(missingValue, variable.missingValue))
            return false;
        if (!java.util.Objects.equals(numericValueLabels, variable.numericValueLabels))
            return false;
        if (!java.util.Objects.equals(stringValueLabels, variable.stringValueLabels))
            return false;
        if (!java.util.Objects.equals(fullName, variable.fullName)) return false;
        if (!java.util.Objects.equals(label, variable.label)) return false;
        if (columnWidth != variable.columnWidth) return false;
        if (decimals != variable.decimals) return false;
        if (formatWidth != variable.formatWidth) return false;
        if (realWidth != variable.realWidth) return false;
        if (width != variable.width) return false;
        if (align != variable.align) return false;
        if (!java.util.Objects.equals(charset, variable.charset)) return false;
        if (!java.util.Objects.equals(formatCode, variable.formatCode)) return false;

        return measure == variable.measure;
    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (numericValueLabels != null ? numericValueLabels.hashCode() : 0);
        result = 31 * result + (stringValueLabels != null ? stringValueLabels.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + realWidth;
        result = 31 * result + decimals;
        result = 31 * result + columnWidth;
        result = 31 * result + (align != null ? align.hashCode() : 0);
        result = 31 * result + (measure != null ? measure.hashCode() : 0);
        result = 31 * result + (formatCode != null ? formatCode.hashCode() : 0);
        result = 31 * result + (missingValue != null ? missingValue.hashCode() : 0);
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        result = 31 * result + formatWidth;
        result = 31 * result + (oldName != null ? oldName.hashCode() : 0);
        result = 31 * result + (oldNameAsString != null ? oldNameAsString.hashCode() : 0);
        return result;
    }

    public String toString() {
        return type + " variable [" + name.toString() + "] {" +
                '\'' + (label == null ? "" : label) + '\'' +
                (!stringValueLabels.isEmpty()
                        ? " {" + (type == VariableType.STRING
                        ? stringValueLabels
                        : numericValueLabels) + "} "
                        : " ") +
                "width:" + width +
                (columnWidth > 0 ? "." + columnWidth : "") +
                " " + align +
                '}';
    }

    int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    int getFormatWidth() {
        return formatWidth;
    }

    public void setFormatWidth(int width) {
        this.formatWidth = width;
    }
}
