package org.jsynthlib.jsynthlib.xml.editor;

import java.util.Map;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;



public class AbsolutePosition implements WidgetPosition {
	private int width, height;
	private int x, y;
	public void setX(String x) {
		this.x = Integer.parseInt(x);
	}
	public void setY(String y) {
		this. y = Integer.parseInt(y);
	}
	public void setSize(WidgetSize size) {
		width = size.getWidth();
		height = size.getHeight();
	}

	public Constraints getConstraints(SpringLayout layout, Map widgetMap) {
		return new Constraints(
				Spring.constant(x),
				Spring.constant(y),
				Spring.constant(width),
				Spring.constant(height)
				);
				
	}
}
