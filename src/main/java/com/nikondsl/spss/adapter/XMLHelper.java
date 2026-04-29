package com.nikondsl.spss.adapter;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 7/2/2008
 * Time: 8:47:30
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
class XMLHelper {

    static void parseXML(InputSource inputsource, DefaultHandler defaulthandler) {
        try {
            XMLReader xmlreader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            setFeatures(xmlreader);
            xmlreader.setContentHandler(defaulthandler);
            xmlreader.setErrorHandler(defaulthandler);
            xmlreader.parse(inputsource);
        } catch (SAXException exception) {
            throw new IllegalStateException("Could not load library (it might be xerces.jar)\n" + exception.getMessage());
        } catch (Exception exception) {
            log.error("", exception);
        }
    }

    private static void setFeatures(XMLReader xmlreader) {
        boolean nameSpaces = true;
        boolean nameSpacePrefixes = false;
        boolean validation = false;
        boolean schema = false;
        boolean schemaFullCheck = false;
        boolean dynamic = false;
        try {
            xmlreader.setFeature("http://xml.org/sax/features/namespaces", nameSpaces);
        } catch (SAXException saxexception) {
            log.warn("warning: Parser does not support feature (http://xml.org/sax/features/namespaces)");
        }
        try {
            xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", nameSpacePrefixes);
        } catch (SAXException saxexception1) {
            log.warn("warning: Parser does not support feature (http://xml.org/sax/features/namespace-prefixes)");
        }
        try {
            xmlreader.setFeature("http://xml.org/sax/features/validation", validation);
        } catch (SAXException saxexception2) {
            log.warn("warning: Parser does not support feature (http://xml.org/sax/features/validation)");
        }
        try {
            xmlreader.setFeature("http://apache.org/xml/features/validation/schema", schema);
        } catch (SAXNotRecognizedException saxnotrecognizedexception) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
        } catch (SAXNotSupportedException saxnotsupportedexception) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/schema)");
        }
        try {
            xmlreader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", schemaFullCheck);
        } catch (SAXNotRecognizedException saxnotrecognizedexception1) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/schema-full-checking)");
        } catch (SAXNotSupportedException saxnotsupportedexception1) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/schema-full-checking)");
        }
        try {
            xmlreader.setFeature("http://apache.org/xml/features/validation/dynamic", dynamic);
        } catch (SAXNotRecognizedException saxnotrecognizedexception2) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/dynamic)");
        } catch (SAXNotSupportedException saxnotsupportedexception2) {
            log.warn("warning: Parser does not support feature (http://apache.org/xml/features/validation/dynamic)");
        }
    }
}
