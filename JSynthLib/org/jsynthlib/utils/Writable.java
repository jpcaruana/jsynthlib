package org.jsynthlib.utils;

import org.xml.sax.SAXException;


public interface Writable {
	public void write(XMLWriter xml) throws SAXException;
}
