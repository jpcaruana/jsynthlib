package org.jsynthlib.editorbuilder.widgets;

import java.awt.Container;

import org.jsynthlib.editorbuilder.Anchor;
import org.jsynthlib.editorbuilder.AnchorChangeEvent;
import org.jsynthlib.editorbuilder.AnchorListener;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class AnchoredWidget extends Widget implements AnchorListener {
    protected void startElement(XMLWriter xml) throws SAXException {
        super.startElement(xml);
        writePosition(xml);
    }
    
    protected void writePosition(XMLWriter xml) throws SAXException {
        xml.setAttribute("type","relative");
        xml.startElement("position");
        writeAnchor(xml,getNSAnchor());
        writeAnchor(xml, getEWAnchor());
        xml.endElement("position");		
    }
    
    protected void writeAnchor(XMLWriter xml, Anchor anchor) throws SAXException {
        String element = sideName(anchor.getSide());
        xml.startElement(element);
        xml.writeProperty("widget",((Widget) anchor.getTarget()).getId());
        xml.writeProperty("side",sideName(anchor.getTargetSide()));
        xml.writeProperty("padding", anchor.getPadding());
        xml.endElement(element);
    }
    protected String sideName(int side) {
        switch (side) {
            case Anchor.NORTH:
                return "top";
            case Anchor.SOUTH:
                return "bottom";
            case Anchor.WEST:
                return "left";
            case Anchor.EAST:
                return "right";
            default:
                throw new IllegalArgumentException("Invalid side "+side);
        }
    }
    
    public AnchoredWidget(String name) {
        super(name);
        hAnchor = new Anchor(this, Anchor.WEST);
        hAnchor.addAnchorListener(this);
        vAnchor = new Anchor(this, Anchor.NORTH);
        vAnchor.addAnchorListener(this);
    }
    public AnchoredWidget(String name, String id) {
        super(name, id);
    }
    
    private Anchor hAnchor, vAnchor;
    
    public final Anchor getNSAnchor() {
        return getNSAnchor(getParent());
    }
    public Anchor getNSAnchor(Container parent) {
        return vAnchor;
    }
    public void setNSAnchor(Anchor a) {
        vAnchor = a;
    }
    public final Anchor getEWAnchor() {
        return getEWAnchor(getParent());
    }
    public Anchor getEWAnchor(Container parent) {
        return hAnchor;
    }
    public void setEWAnchor(Anchor a) {
        hAnchor = a;
    }
    public void anchorChanged(AnchorChangeEvent e) {
        /*
        validateParents();
        DesignerFrame f = EditorBuilder.getDesignerFrame();
        f.validate();
        f.repaint();
        */
    }
}
