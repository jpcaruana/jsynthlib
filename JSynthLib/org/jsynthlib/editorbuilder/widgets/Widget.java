package org.jsynthlib.editorbuilder.widgets;

import java.awt.Component;
import java.awt.Container;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jsynthlib.editorbuilder.DesignerFrame;
import org.jsynthlib.editorbuilder.EditorBuilder;
import org.jsynthlib.utils.Writable;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class Widget extends JPanel implements Writable {
	private static int count;

	
	private String name;
	private String id;
	private String type;
	private WidgetPosition position;
	
	public Widget(String name) {
		this(name, name + " " + (count++));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	public Widget(String name, String id) {
		this.name = name;
		this.id = id;
	}
	public final String getId() {
		return id;
	}
	public final void setId(String _id) {
		this.id = _id;
	}
	
	public String getWidgetName() {
		return name;
	}
	
	protected String getType() {
		return type;
	}
	
	protected void setType(String type) {
		this.type = type;
		validateParents();
	}
	
	public void write(XMLWriter xml) throws SAXException {
		startElement(xml);
		writeContent(xml);
		endElement(xml);
	}


	protected void startElement(XMLWriter xml) throws SAXException {
		String type = getType();
		if (type != null)
			xml.setAttribute("type", type);
		
		xml.startElement(getWidgetName());
		xml.writeProperty("id", getId());
	}
	
	protected void writeContent(XMLWriter xml) throws SAXException {
		Component kids[] = getComponents();
		boolean started = false;
		if (kids != null && kids.length > 0) {
			for (int i = 0; i < kids.length; i++) {
				if (kids[i] instanceof Widget) {
					if (!started) {
					    xml.startElement("widgets");
					    started = true;
					}
					((Widget)kids[i]).write(xml);
				}
			}
			if (started)
				xml.endElement("widgets");
		}
	}
	
	protected void endElement(XMLWriter xml) throws SAXException {
		xml.endElement(getWidgetName());
	}
	public void validateParents() {
	    DesignerFrame f = EditorBuilder.getDesignerFrame();
	    if (f == null)
	        return;
	    Widget root = f.getRootWidget();
	    if (this == root)
	        return;
	    for (Component c = getParent();
	            c != null && c != root;
	            c = c.getParent()) {
	        c.validate();
	    }        
	}
	public void poof() {
	    Container c = getParent();
	    c.remove(this);
	    c.validate();
	    if (c instanceof Widget)
	        ((Widget)c).validateParents();
	}
	public Set getAnchorTargets() {
	    Set result = new HashSet();
	    Container c = getParent();
	    result.add(c);
	    if (c instanceof ContainerWidget) {
	        Widget[] siblings = ((ContainerWidget)c).getWidgets();
	        for (int i = 0; i < siblings.length; i++)
	            if (siblings[i] != this)
	                result.add(siblings[i]);
	    }
	    return result;
	}
}
