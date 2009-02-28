package org.jsynthlib.utils;

import java.io.File;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class XMLWriter {
	private AttributesImpl attributes = new AttributesImpl();
	private TransformerHandler xml;
	private LinkedList<String> elements = new LinkedList<String>();
    
	public XMLWriter(OutputStream s) throws TransformerConfigurationException { 
	    this (new StreamResult(s));
        }
	public XMLWriter(File f) throws TransformerConfigurationException {
	    this(new StreamResult(f));
	}
	public XMLWriter(StreamResult output) throws TransformerConfigurationException {
		SAXTransformerFactory tf = (SAXTransformerFactory)  SAXTransformerFactory.newInstance();
		xml = tf.newTransformerHandler();
		xml.setResult(output);
	}

	public void write(Writable w) throws SAXException {
		xml.startDocument();
		w.write(this);
		xml.endDocument();
	}
	
	public void setAttribute(String name, String value) {
		attributes.addAttribute("","",name,"CDATA",value);		
	}

	public void startElement(String element) throws SAXException {
		xml.startElement("","",element, attributes);
		attributes.clear();
		elements.addFirst(element);
	}

	public void writeProperty(String key, String value) throws SAXException {
		startElement(key);
		write(value);
		endElement(key);
	}
	
	public void endElement(String key) throws SAXException {
	    String expected = (String) elements.removeFirst();
	    if (!expected.equals(key))
	        throw new SAXException("Closing tag " + key + " doesn't match opening tag " + expected);
	    xml.endElement("","",key); 
	}

	public void write(String text) throws SAXException {
		xml.characters(text.toCharArray(), 0, text.length());
	}
	public void clearAttributes() {
		attributes.clear();
	}

	public void writeProperty(String key, int value) throws SAXException {
		writeProperty(key, Integer.toString(value));
	}
}
