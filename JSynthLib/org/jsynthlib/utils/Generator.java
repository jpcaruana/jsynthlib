package org.jsynthlib.utils;

import org.xml.sax.SAXException;

/**
 * Helper class for parsing XML documents.
 * @author ribrdb
 */
public class Generator {
    private Class c;
    public Generator() {
    }
    public Generator(Class cls) {
        c = cls;
    }
    public Object generate(String element, String type) throws SAXException {
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new SAXException("Unable to create " + element, e);
        }
    }
}