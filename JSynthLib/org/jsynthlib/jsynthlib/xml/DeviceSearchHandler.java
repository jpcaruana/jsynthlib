package org.jsynthlib.jsynthlib.xml;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


class DeviceSearchHandler extends DefaultHandler {
		private boolean found_start = false;
		private boolean name_done = false;
		private boolean manuf_done = false;
		private boolean id_done = false;
		private LinkedList tags = new LinkedList();
		private StringBuffer name = new StringBuffer();
		private StringBuffer id = new StringBuffer();
		private StringBuffer manuf = new StringBuffer();
		private Locator locator;
		
		public void reset() {
			found_start = name_done = id_done = false;
			tags = new LinkedList();
			name = new StringBuffer();
			id = new StringBuffer();
		}
		
		
		public void setDocumentLocator(Locator l) {
			locator = l;
		}
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			String lasttage = (String)tags.getFirst();
			if (!name_done && "name".equals(lasttage)) {
				name.append(ch,start,length);
			} else if (!id_done && "inquiryid".equals(lasttage)) {
				id.append(ch,start,length);
			} else if (!manuf_done && "manufacturer".equals(lasttage)){
				manuf.append(ch,start,length);
			}
		}
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String el = (localName == "") ? qName : localName;
			if (el.equals("name")) {
				name_done = true;
			} else if (el.equals("inquiryid")) {
				id_done = true;
			} else if (el.equals("manufacturer")) {
				manuf_done = true;
			}
			tags.removeFirst();
			if (manuf_done && id_done && name_done)
				throw new FinishedParsingException();
		}
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String el = (localName == "") ? qName : localName;
			if (!found_start) {
				if (!"device".equals(el)) {
					throw new NotDeviceException();
				}
				found_start = true;
			}
			String lasttag = tags.size() == 0 ? "" : (String)tags.getFirst();
			if (lasttag.equals("xdevice")) {
				tags.addFirst(el);
			} else  if (lasttag.equals("name")
						|| lasttag.equals("inquiryid")
						|| lasttag.equals("manufacturer")) {
				throw new SAXParseException(lasttag + "can only contain text",locator);
			} else {
//				these should come first, but don't want to get the name for something other than a device
				tags.addFirst("x"+el);
			}
		}
		public String getManufacturer() { return manuf.toString(); }
		public String getName() { return name.toString(); }
		public String getId() { return id.toString(); }
	}