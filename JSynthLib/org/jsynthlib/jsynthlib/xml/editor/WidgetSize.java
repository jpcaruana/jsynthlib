package org.jsynthlib.jsynthlib.xml.editor;


public class WidgetSize {
	private int width;
	private int height;
	
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}
	public void setHeight(String height) {
		this.height= Integer.parseInt(height);
	}
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
}
