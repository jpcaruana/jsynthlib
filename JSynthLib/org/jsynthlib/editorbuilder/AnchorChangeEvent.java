package org.jsynthlib.editorbuilder;

import javax.swing.Spring;

import org.jsynthlib.editorbuilder.widgets.Widget;


public class AnchorChangeEvent {
    public static final int CONSTRAINED_WIDGET = 0;
    public static final int CONSTRAINED_SIDE = 1;
    public static final int TARGET_WIDGET = 2;
    public static final int TARGET_SIDE = 3;
    public static final int PADDING = 4;
    
    private int type;
    private Widget w;
    private int side;
    private Anchor anchor;
    private Spring padding;
    
    public AnchorChangeEvent(Anchor a, int type, int side) {
        this.type = type;
        this.side = side;
        anchor = a;
    }
    public AnchorChangeEvent(Anchor a, int type, Widget w) {
        this.type = type;
        this.w = w;
        anchor = a;
    }
    public AnchorChangeEvent(Anchor a, Spring padding) {
        this.padding = padding;
        this.type = PADDING;
        this.anchor = a;
    }
    
    public Anchor getAnchor() {
        return anchor;
    }
    public int getOldSide() {
        return side;
    }
    public Widget getOldWidget() {
        return w;
    }
    public Spring getOldPadding() {
        return padding;
    }
    public int getType() {
        return type;
    }
}
