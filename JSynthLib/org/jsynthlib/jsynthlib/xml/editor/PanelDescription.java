package org.jsynthlib.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jsynthlib.jsynthlib.xml.XMLPatch;
import org.jsynthlib.utils.AutoSpringLayout;


public class PanelDescription extends WidgetDescription {

	private boolean bordered;
	
	public PanelDescription() {
		super("panel");
	}
	
	/* (non-Javadoc)
	 * @see org.jsynthlib.jsynthlib.xml.WidgetDescription#create(java.awt.Container, java.util.Map, org.jsynthlib.jsynthlib.xml.XMLPatch)
	 */
	public Component create(Map widgets, XMLPatch patch)
			throws Exception {
		JPanel result = new JPanel();
		if (bordered)
			result.setBorder(BorderFactory.createTitledBorder(getTitle()));
		result.setLayout(new AutoSpringLayout());
		widgets.put(getId(), result);
		createChildren(widgets, patch);
		
		return result;
	}
	public void setBorder(String val) {
		bordered = Boolean.valueOf(val).booleanValue();
	}
	public void setWidgets(List widgets) {
		super.setWidgets(widgets);
	}
}
