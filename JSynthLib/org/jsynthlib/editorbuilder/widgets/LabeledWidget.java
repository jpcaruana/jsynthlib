package org.jsynthlib.editorbuilder.widgets;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class LabeledWidget extends Widget {
	
	public LabeledWidget(String name) {
		super(name);
	}
	public LabeledWidget(String name, String id) {
		super(name, id);
	}
	
	private String label;
	
	public final String getText() {
		return label;
	}
	public void setText( String text) {
		label = text;
		validateParents();
	}
	protected void startElement(XMLWriter xml) throws SAXException {
		super.startElement(xml);
		if (label != null) {
			xml.writeProperty("title", label);
		}
	}
}
