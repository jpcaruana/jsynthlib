package org.jsynthlib.xml.editor;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Spring;
import javax.swing.SpringLayout;


public class Anchor {
	private String myside, otherside;
	private String key;
	private int padding;
	private static final Map SIDES;
	static {
		SIDES = new HashMap();
		SIDES.put("top", SpringLayout.NORTH);
		SIDES.put("bottom", SpringLayout.SOUTH);
		SIDES.put("left", SpringLayout.WEST);
		SIDES.put("right", SpringLayout.EAST);
	}
	public Anchor(String type) {
		myside = (String) SIDES.get(type);
	}
	public void setWidget(String widget) {
		key = widget;
	}
	public String getKey() {
		return key;
	}
	public void setSide(String side) {
		otherside = (String) SIDES.get(side);
	}
	public String getSide() {
		return myside;
	}
	public String getOtherSide(){
		return otherside;
	}
	public int getPadding() {
		return padding;
	}
	public void setPadding(String padding) {
		this.padding = Integer.parseInt(padding);
	}
	public Spring padding() {
		return Spring.constant(padding);
	}
	public Spring getSpring(SpringLayout l, Map widgetMap) {
		Component c = (Component)widgetMap.get(key);
		Spring base = l.getConstraint(otherside, c);
		return Spring.sum(base, padding());
	}
}
