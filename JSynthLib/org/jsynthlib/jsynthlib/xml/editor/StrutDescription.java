package org.jsynthlib.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.Map;

import javax.swing.JLabel;

import org.jsynthlib.jsynthlib.xml.XMLPatch;


public class StrutDescription extends WidgetDescription {
	WidgetSize size;
	public StrutDescription() {
		super("strut");
	}
	
	/* (non-Javadoc)
	 * @see org.jsynthlib.jsynthlib.xml.WidgetDescription#create(java.awt.Container, java.util.Map, org.jsynthlib.jsynthlib.xml.XMLPatch)
	 */
	public Component create(Map widgets, XMLPatch patch)
			throws Exception {
		((AbsolutePosition) getPosition()).setSize(size);
		JLabel result = new JLabel();
		//result.setBorder(BorderFactory.createLineBorder(Color.RED));
		widgets.put(getId(), result);
		return result;
	}

	public WidgetSize getSize() {
		return size;
	}
	public void setSize(WidgetSize size) {
		this.size = size;
	}
}
