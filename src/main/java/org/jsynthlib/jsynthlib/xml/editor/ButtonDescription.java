package org.jsynthlib.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.Map;

import javax.swing.JButton;

import org.jsynthlib.jsynthlib.xml.XMLPatch;


public class ButtonDescription extends WidgetDescription {
	public ButtonDescription() {
		super("button");
	}
	/* (non-Javadoc)
	 * @see org.jsynthlib.jsynthlib.xml.WidgetDescription#create(java.awt.Container, java.util.Map, org.jsynthlib.jsynthlib.xml.XMLPatch)
	 */
	public Component create(Map widgets, XMLPatch patch)
			throws Exception {
		JButton result = new JButton(getTitle());
		widgets.put(getId(), result);
		return result;
	}

}
