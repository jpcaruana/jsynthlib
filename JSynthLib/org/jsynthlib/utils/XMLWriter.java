package org.jsynthlib.utils;

import java.io.File;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class XMLWriter {
	private AttributesImpl attributes = new AttributesImpl();
	private TransformerHandler xml;
	public XMLWriter(File f) throws TransformerConfigurationException {
		StreamResult output = new StreamResult(f);
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
	}

	public void writeProperty(String key, String value) throws SAXException {
		startElement(key);
		write(value);
		endElement(key);
	}
	
	public void endElement(String key) throws SAXException {
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
