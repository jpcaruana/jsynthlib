package org.jsynthlib.jsynthlib.xml.editor;

import java.util.Map;

import javax.swing.SpringLayout;


public interface WidgetPosition {
	public SpringLayout.Constraints getConstraints(SpringLayout layout, Map widgetMap);
	public void setSize(WidgetSize size);
}
