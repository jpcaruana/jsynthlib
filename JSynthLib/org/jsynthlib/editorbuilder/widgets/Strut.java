package org.jsynthlib.editorbuilder.widgets;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Spring;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class Strut extends Widget{
    protected Constraints constraints;
    
    public Strut(int x, int y) { this(x, y, 8, 100); }
    public Strut(int x, int y, int width, int height) {
        super("strut");
        setBorder(BorderFactory.createLineBorder(Color.red, 2));
        constraints = new Constraints(Spring.constant( x ),
                Spring.constant( y ),
                Spring.constant( width ),
                Spring.constant( height ));
    }
    public Constraints getConstraints() { return constraints; }
    public void setX( int x ) {
        constraints.setX(Spring.constant( x ));
        setBounds(x,getY(),getWidth(),getHeight());
        validateParents();
    }
    public void setY( int y ) {
        constraints.setY(Spring.constant( y ));
        setBounds(getX(),y, getWidth(), getHeight());
        validateParents();
    }
    public void setWidth( int width ) {
        constraints.setWidth(Spring.constant( width ));
        setBounds(getX(),getY(),width, getHeight());
        validateParents();
    }
    public void setHeight( int height ) {
        constraints.setHeight(Spring.constant( height ));
        setBounds(getX(),getY(),getWidth(),height);
        validateParents();
    }
    private void updateBounds() {
        setBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
    }
    
    /*
     public String construct() { return "new JLabel()"; }
     public String postinitialize() {
     String id = GlassPane.getWidgetIdentifier(this);
     return
     "((SpringLayout)"+id+".getParent().getLayout()).addLayoutComponent(\n"+
     "            " + id + ",\n" +
     "            new Constraints(Spring.constant( " + getX() + " ),\n" +
     "                            Spring.constant( " + getY() + " ),\n" +
     "                            Spring.constant( " + getWidth()+ " ),\n" +
     "                            Spring.constant( " + getHeight() + " )));";
     }
     public String preinitialize() { return null; }
     */
    public int getCX() {
        return constraints.getX().getValue();
    }
    public int getCY() {
        return constraints.getY().getValue();
    }
    protected void writeContent(XMLWriter xml) throws SAXException {
        xml.setAttribute("type", "absolute");
        xml.startElement("position");
        xml.writeProperty("x", getCX());
        xml.writeProperty("y", getCY());
        xml.endElement("position");
        
        xml.startElement("size");
        xml.writeProperty("width", getWidth());
        xml.writeProperty("height", getHeight());
        xml.endElement("size");
    }
}
