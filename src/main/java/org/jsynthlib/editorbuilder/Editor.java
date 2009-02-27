package org.jsynthlib.editorbuilder;

import org.jsynthlib.editorbuilder.widgets.Widget;
import org.jsynthlib.utils.Writable;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class Editor implements Writable {
	private Widget root;
	
	public Editor(Widget root) {
		this.root = root;
	}
	/* (non-Javadoc)
	 * @see org.jsynthlib.util.Writable#write(org.jsynthlib.util.XMLWriter)
	 */
	public void write(XMLWriter xml) throws SAXException {
		xml.startElement("editor");
		xml.startElement("widgets");
		root.write(xml);
		xml.endElement("widgets");
		xml.endElement("editor");
	}

}
