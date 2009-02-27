package org.jsynthlib.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.jsynthlib.jsynthlib.xml.XMLPatch;


public class EditorDescription {
	private HashMap rootWidgets = new HashMap();
	
	public void setWidgets(LinkedList roots) {
		Iterator it = roots.iterator();
		while (it.hasNext() ) {
		    WidgetDescription d = (WidgetDescription)it.next();
		    rootWidgets.put(d.getId(), d);
		}
	}

	public Component loadRoot(XMLPatch patch) throws Exception {
		return loadRoot("root 0", patch);
	}
	
	public Component loadRoot(String id, XMLPatch patch) throws Exception {
		Map widgets = new HashMap();
		WidgetDescription w = (WidgetDescription)rootWidgets.get(id);
		Component root = w.create(widgets, patch);
		w.position(null, widgets);
		return root;
	}
}
