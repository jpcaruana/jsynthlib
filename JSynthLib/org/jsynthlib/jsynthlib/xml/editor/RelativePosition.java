package org.jsynthlib.jsynthlib.xml.editor;

import java.util.Map;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;


public class RelativePosition implements WidgetPosition {

	private Anchor vAnchor, hAnchor;
	private WidgetSize size;

	public void setTop(Anchor a) {
		vAnchor = a;
	}
	public void setBottom(Anchor a) {
		vAnchor = a;
	}
	public void setLeft(Anchor a) {
		hAnchor = a;
	}
	public void setRigt(Anchor a) {
		hAnchor = a;
	}
	
	/* (non-Javadoc)
	 * @see org.jsynthlib.jsynthlib.xml.WidgetPosition#getConstraints(javax.swing.SpringLayout, java.util.Map)
	 */
	public Constraints getConstraints(SpringLayout layout, Map widgetMap) {
		Constraints c;
		if (size == null) {
		    c = new Constraints();
		} else {
		    c = new Constraints(
		            Spring.constant(0),
		            Spring.constant(0),
		            Spring.constant(size.getWidth()),
		            Spring.constant(size.getHeight())
		    );
		}
		c.setConstraint(vAnchor.getSide(), vAnchor.getSpring(layout, widgetMap));
		c.setConstraint(hAnchor.getSide(), hAnchor.getSpring(layout, widgetMap));
		return c;
	}
	public void setSize(WidgetSize size) {
	    this.size = size;
	}
}
