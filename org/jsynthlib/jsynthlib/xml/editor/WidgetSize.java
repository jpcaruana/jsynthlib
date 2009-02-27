package org.jsynthlib.jsynthlib.xml.editor;


public class WidgetSize {
    private int width;
    private int height;
    
    private Anchor wAnchor, hAnchor;
    
    public void setWidth(String width) {
        this.width = Integer.parseInt(width);
    }
    public void setHeight(String height) {
        this.height= Integer.parseInt(height);
    }
    public void setBottom(Anchor h) {
        hAnchor = h;
    }
    public Anchor getBottom() {
        return hAnchor;
    }
    public void setRight(Anchor w) {
        wAnchor = w;
    }
    public Anchor getRight() {
        return wAnchor;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
