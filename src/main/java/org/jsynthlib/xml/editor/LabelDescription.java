package org.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.Map;

import javax.swing.JLabel;

import org.jsynthlib.xml.XMLPatch;


public class LabelDescription extends WidgetDescription {

	public LabelDescription() {
		super("label");
	}

	public Component create(Map widgets, XMLPatch patch) throws Exception {
		Component result = new JLabel(getTitle());
		widgets.put(getId(), result);
		return result;
	}

}
