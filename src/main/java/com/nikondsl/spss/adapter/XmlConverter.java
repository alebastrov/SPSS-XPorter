// Decompiled by DJ v2.9.9.57 Copyright 2000 Atanas Neshkov  Date: 07.02.2008 8:15:10
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package com.nikondsl.spss.adapter;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@lombok.extern.slf4j.Slf4j
public class XmlConverter extends DefaultHandler {
    private final PMStationSPSSWriter writer;
    private StringBuilder stringBuilder = null;
    private final List<IndicatorType> typesOfVariables = new ArrayList<>();
    private int variableIndex = 0;
    private boolean dataSectionHasBeenAdded = false;
    private boolean rowFinished = false;
    private boolean variablesNotCorrespondentCases = false;
    private Locator locator;
    private ValueLabels valueLabels;
    private int variableIndexForLabel = -1;

    private XmlConverter(PMStationSPSSWriter spsswriter) {
        writer = spsswriter;
    }

    public static void convert(InputStream inputstream, OutputStream outputstream, String charset) throws IOException, SAXException {
        PMStationSPSSWriter spsswriter = new PMStationSPSSWriter(outputstream, charset);
        convert(inputstream, spsswriter);
    }

    public static void convert(File file, File file1, String charset) throws IOException, SAXException {
        PMStationSPSSWriter spsswriter = new PMStationSPSSWriter(new BufferedOutputStream(new FileOutputStream(file1)), charset);
        FileInputStream fileinputstream = new FileInputStream(file);
        convert(fileinputstream, spsswriter);
        fileinputstream.close();
    }

    public static void convert(InputStream inputstream, PMStationSPSSWriter spsswriter) throws IOException, SAXException {
        XmlConverter xmlConverter = new XmlConverter(spsswriter);
        spsswriter.addDictionarySection();
        XMLHelper.parseXML(new InputSource(inputstream), xmlConverter);
        spsswriter.addFinishSection();
    }

    static void main(String[] as) {
        try {
            convert(new File("test.xml"), new File("test.sav"), "utf-8");
        } catch (Exception exception) {
            log.error("", exception);
        }
    }

    public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
        String tag = s1.toLowerCase();
        if ("string".equals(tag)) {
            String name = attributes.getValue("name");
            String length = attributes.getValue("length");
            String label = attributes.getValue("label");
            addStringVariable(name, length, label);
        } else if ("numeric".equals(tag)) {
            String name = attributes.getValue("name");
            String width = attributes.getValue("width");
            String decimals = attributes.getValue("decimals");
            String label = attributes.getValue("label");
            addNumericVariable(name, width, decimals, label);
        } else if ("valuelabels".equals(tag)) {
            String s6 = attributes.getValue("varnum");
            valueLabels = new ValueLabels();
            variableIndexForLabel = Integer.parseInt(s6);
        } else if ("lable".equals(tag) || "label".equals(tag)) {
            String s7 = attributes.getValue("name");
            String s10 = attributes.getValue("value");
            valueLabels.putLabel(Double.parseDouble(s10), s7);
        } else if ("data".equals(tag)) {
            try {
                writer.addDataSection();
                dataSectionHasBeenAdded = true;
            } catch (Exception exception) {
                throw new SAXException("line number: " + locator.getLineNumber(), exception);
            }
        } else if ("row".equals(tag)) {
            variablesNotCorrespondentCases = true;
            if (variableIndex != 0 && variableIndex != typesOfVariables.size())
                writer.finishCurrentLine();
            variableIndex = 0;
        }
    }

    private void addStringVariable(String name, String length, String label) throws SAXException {
        if (name == null) name = "var" + (typesOfVariables.size() + 1);
        if (length == null) length = "255";
        if (label == null) label = name;
        try {
            writer.addStringVar(name, Integer.parseInt(length), label);
            typesOfVariables.add(IndicatorType.STRING);
        } catch (Exception exception) {
            throw new SAXException("line number: " + locator.getLineNumber(), exception);
        }
    }

    private void addNumericVariable(String name, String width, String decimals, String label) throws SAXException {
        if (name == null) name = "var" + (typesOfVariables.size() + 1);
        if (width == null) width = "8";
        if (decimals == null) decimals = "2";
        if (label == null) label = name;
        try {
            writer.addNumericVar(name, Integer.parseInt(width), Integer.parseInt(decimals), label);
            typesOfVariables.add(IndicatorType.DOUBLE);
        } catch (Exception exception) {
            throw new SAXException("line number: " + locator.getLineNumber(), exception);
        }
    }

    public void characters(char[] ac, int i1, int j1) throws SAXException {
        String s = (new String(ac, i1, j1)).trim();
        if (s.length() == 0) return;
        if (stringBuilder != null) stringBuilder.append(s);
        else stringBuilder = new StringBuilder(s);
    }

    public void endElement(String s, String s1, String s2) throws SAXException {
        String tag = s1.toLowerCase();
        try {
            if (tag.equals("valuelabels")) writer.addValueLabels(variableIndexForLabel, valueLabels);
            if (!dataSectionHasBeenAdded) return;
            if ("row".equals(tag)) {
                variablesNotCorrespondentCases = false;
                if (!rowFinished) {
                    writer.finishCurrentLine();
                    variableIndex = 0;
                    return;
                }
            }
            rowFinished = false;
            if (!"value".equals(tag)) return;
            if (variableIndex == typesOfVariables.size()) {
                if (variablesNotCorrespondentCases)
                    throw new SAXException("The quantity of values for line is exceeded  - line number :" + locator.getLineNumber());
                variableIndex = 0;
            }
            addData();
            stringBuilder = null;
            variableIndex++;
            if (variableIndex == typesOfVariables.size()) rowFinished = true;
        } catch (Exception exception) {
            throw new SAXException("Error " + exception.getMessage() + " - line number :" + locator.getLineNumber());
        }
    }

    private void addData() {
        IndicatorType type = typesOfVariables.get(variableIndex);
        if (type == IndicatorType.STRING) writer.addData(stringBuilder.toString());
        else writer.addData(new Double(stringBuilder.toString()));
    }

    public void warning(SAXParseException saxparseexception) {
        if (saxparseexception != null) log.error("", saxparseexception);
    }

    public void error(SAXParseException saxparseexception) throws SAXException {
        throw new SAXException(" line number " + locator.getLineNumber() + " : " + saxparseexception.getMessage(), saxparseexception);
    }

    public void fatalError(SAXParseException saxparseexception) throws SAXException {
        throw new SAXException(" line number " + locator.getLineNumber() + " : " + saxparseexception.getMessage(), saxparseexception);
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    enum IndicatorType {
        STRING, DOUBLE
    }

}