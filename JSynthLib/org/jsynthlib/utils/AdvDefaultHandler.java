package org.jsynthlib.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import core.ErrorMsg;

/**
 * @author ribrdb
 */
public class AdvDefaultHandler extends DefaultHandler {

    private boolean found_start = false;
    protected Locator locator;
    protected HashMap generators = new HashMap();
    private LinkedHashMap defaults = new LinkedHashMap();
    private LinkedList objects = new LinkedList();
    private LinkedList elements = new LinkedList();
    private StringBuffer characters = new StringBuffer();
    protected Object output = null;
    private SAXEventRecorder recorder = new SAXEventRecorder();
    private HashMap recordings = new HashMap();

    // Support for delegate parser/handler
    private String start_tag;
    private String delegate_tag;
    private AdvDefaultHandler delegate;
    private int depth = 0;
    protected File base_path;
	protected boolean ignoreExtraDefaults = false;
    
    /**
     * Constructor with no delegate handler.
     * 
     * @param s Start element
     */
    public AdvDefaultHandler(String s) {
        this(s, "", null);
    }
    
    public AdvDefaultHandler(String s, String ds, AdvDefaultHandler d) {
        if (s == null || ds == null)
            throw new NullPointerException(); // Don't postpone the ineveitable
        start_tag = s;
        delegate_tag = ds;
        delegate = d;
    }
    
    
    public void setDocumentLocator(Locator l) {
        locator = l;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(ch,start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        depth--;
        String el = (String)elements.removeFirst();
        if (generators.containsKey(el) || delegate_tag.equals(el)) {
            storeObject(el);
        } else {
            storeText(el);
        }
        // Characters are stored in storeText to avoid unnecessary events,
        // so can't store the element until the characters are stored.
        recorder.recordEndElement(uri, localName, qName);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        recorder.recordStartElement(uri,localName, qName, attributes);
        characters.setLength(0);
        depth++;
        String el = (localName == "") ? qName : localName;
        if (!found_start) {
            if (!start_tag.equals(el))
                throw new SAXException("This is not a " + start_tag);
            found_start = true;
        } else if (!generators.containsKey(elements.getFirst())) {
            throw new SAXParseException("Element " + elements.getFirst() +
                    " can only contain text", locator);
        }
        String fn = attributes.getValue("file");
        if (delegate_tag.equals(el)) {
            if (fn == null)
                throw new SAXParseException("Element " + el
                        + " must be in a separate file", locator);
            passToDelegate(fn);
        } else if (fn != null) {
            throw new SAXParseException("Only " + delegate_tag 
                    + " may be in a separate file", locator);
        }
        Generator o = (Generator)generators.get(el);
        if (o != null) {
            String type = attributes.getValue("type");
            Object g = o.generate(el, type);
            if (g == null)
                throw new SAXParseException("Error creating " + el + " object", locator);
            startObject(g);
        }
        elements.addFirst(el);
        String id = attributes.getValue("id");
        if (id != null) {
            String key = el + '\u0000' + id; // use \0 as separator since 
                                            // it can't be in an XML file
            if (recordings.containsKey(key))
                ((SAXEventRecorder.Recording)recordings.get(key)).replay(this);
            else
                recordings.put(key, recorder.startRecording());
        }
    
    }

    public void endDocument() throws SAXException {
        if (!objects.isEmpty())
            throw new SAXException("Extra objects after parsing");
        // Make sure there aren't any extra defaults
        objects.addFirst(new Object());
        applyDefaults();
    }
    
    public Object getOutput() {
        return output;
    }

    public void setBasePath(File f) {
        base_path = f;
    }
    
    private void passToDelegate(String fn) throws SAXException {
        try {
            // create parser
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            // set defaults
            delegate.defaults = defaults;
            delegate.depth = depth;
            File file = new File(base_path, fn);
            delegate.setBasePath(file.getParentFile());
            // parse
            parser.parse(file, delegate);
            startObject(delegate.getOutput());        
        } catch (SAXException e) {
            throw e;
        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
            throw new SAXParseException("Unable to parse " + fn, locator, e);
        }
    }
    
    private void startObject(Object o) throws SAXException {
        objects.addFirst(o);
        applyDefaults();
    }

    private void applyDefaults() throws SAXException {
        boolean cur_is_list = (objects.getFirst() instanceof List);
        Iterator it = defaults.entrySet().iterator();
        int curdepth = depth;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String el = (String)entry.getKey();
            DefaultEntry de = (DefaultEntry)entry.getValue();
            if (de.depth > curdepth && !de.used) {
                if (ignoreExtraDefaults) {
                	return;
                }
                String msg= "Unrecognized element " + el.toLowerCase()
                    + " closed at line " + de.line + ", column " + de.col;
                throw new SAXException(msg);
            } else if (!cur_is_list && store(de.value, el, false)) {
                de.used = true;
            }
        }
    }

    private void setDefault(Object o, String property) {
        DefaultEntry e = new DefaultEntry();
        e.depth = this.depth;
        e.value = o;
        e.line = locator.getLineNumber();
        e.col = locator.getColumnNumber();
        defaults.put(property, e);
    }

    private void storeObject(String element) throws SAXParseException {
        Object o = objects.removeFirst();
        element = element.substring(0,1).toUpperCase() + element.substring(1);
        try {
            o = o.getClass().getMethod("get" + element, null).invoke(o, null);
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            throw new SAXParseException("Error storing " + element, locator, e);
        }
        if (start_tag.equalsIgnoreCase(element)) {
            output = o;
            return;
        } 
        store(o, element, true);
    }

    private void storeText(String element) throws SAXParseException {
        element = element.substring(0,1).toUpperCase() + element.substring(1);
        String text = characters.toString();
        text = text.replaceFirst("^\\s*(.*?)\\s*$","$1");
        recorder.recordCharacters(text);
        store(text, element, true);
    }

    private boolean store(Object o, String element, boolean setDefault) throws SAXParseException {
            Object parent = objects.getFirst();
            if (parent instanceof List) {
                ((List)parent).add(o);
                return true;
            }
            Method m = getSetter(element, parent.getClass(), o.getClass());
            if (m == null) {
                if (setDefault)
                    setDefault(o, element);
                return false;
            }
            try {
                m.invoke(parent, new Object[] {o});
            } catch (Exception e) {
                throw new SAXParseException("Error processing element " + element, locator, e);
            }
            return true;
       }

    private Method getSetter(String prop, Class parent, Class object) {
        Method m = null;
        while (m == null && object != null) {
            Class[] ca = new Class[] { object };
            try {
                m = parent.getMethod("set" + prop, ca);
            } catch (NoSuchMethodException e) {
                try {
                    m = parent.getMethod("add" + prop, ca);
                } catch (NoSuchMethodException ex) { }
            }
            object = object.getSuperclass();
        }
        return m;
    }

    public static String[] list2sa(LinkedList l) {
        if (l.size() == 1 && l.getFirst() instanceof String[]) {
            return (String[])l.getFirst();
        }
        return (String[])l.toArray(new String[0]);
    }

    private class DefaultEntry {
        int depth;
        int line;
        int col;
        Object value;
        boolean used = false;
    }
}
