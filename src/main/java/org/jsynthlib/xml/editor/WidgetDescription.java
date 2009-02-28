package org.jsynthlib.xml.editor;

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Map;

import javax.swing.SpringLayout;

import org.jsynthlib.xml.XMLPatch;


public abstract class WidgetDescription {
	private String id;
	private String label;
	private WidgetPosition position;
	private WidgetDescription widgets[];
	private String element;
	private String type;
	
	public WidgetDescription(String element) {
		this.element = element;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return label;
	}
	public void setTitle(String label) {
		this.label = label;
	}
	public WidgetPosition getPosition() {
		return position;
	}
	public void setPosition(WidgetPosition position) {
		this.position = position;
	}
	public WidgetDescription[] getChildren() {
		return widgets;
	}
	protected void setWidgets(List widgets) {
		this.widgets = new WidgetDescription[widgets.size()];
		this.widgets = (WidgetDescription[]) widgets.toArray(this.widgets);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	abstract public Component create(Map widgets, XMLPatch patch) throws Exception;

	protected void createChildren(Map widgets, XMLPatch patch) throws Exception {
		for (int i = 0; i < this.widgets.length; i++)
			this.widgets[i].create(widgets, patch);
	}
	public void position(Container parent, Map widgetMap) {
		Component self = (Component)widgetMap.get(getId());
		if (parent != null)
			parent.add(self, position.getConstraints((SpringLayout)parent.getLayout(),widgetMap));
		if (self instanceof Container && widgets != null && widgets.length > 0) {
			for (int i = 0; i < widgets.length; i++)
				widgets[i].position((Container)self, widgetMap);
		}
	}
}
