package org.jsynthlib.editorbuilder.widgets;

import org.jsynthlib.editorbuilder.Anchor;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class AnchoredWidget extends LabeledWidget {
	protected void startElement(XMLWriter xml) throws SAXException {
		super.startElement(xml);
		writePosition(xml);
	}

	protected void writePosition(XMLWriter xml) throws SAXException {
		xml.setAttribute("type","relative");
		xml.startElement("position");
		writeAnchor(xml,getNSAnchor());
		writeAnchor(xml, getEWAnchor());
		xml.endElement("position");		
	}
	
	private void writeAnchor(XMLWriter xml, Anchor anchor) throws SAXException {
		String element = sideName(anchor.getConstrainedSide());
		xml.startElement(element);
		xml.writeProperty("widget",((Widget) anchor.getTargetComponent()).getId());
		xml.writeProperty("side",sideName(anchor.getTargetSide()));
		xml.writeProperty("padding", anchor.getPadding());
		xml.endElement(element);
	}
	private String sideName(int side) {
		switch (side) {
		case Anchor.NORTH:
			return "top";
		case Anchor.SOUTH:
			return "bottom";
		case Anchor.WEST:
			return "left";
		case Anchor.EAST:
			return "right";
		default:
			throw new IllegalArgumentException("Invalid side "+side);
		}
	}

	public AnchoredWidget(String name) {
		super(name);
	}
	public AnchoredWidget(String name, String id) {
		super(name, id);
	}
    public final Anchor getNSAnchor() {
			return Anchor.getNSAnchor(this);
	}
	public final void setNSAnchor(Anchor dummy) {}
    public final Anchor getEWAnchor() {
    		return Anchor.getEWAnchor(this);
    }
    public final void setEWAnchor(Anchor dummy) {}
}
