package org.jsynthlib.editorbuilder.widgets;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Spring;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class Strut extends AnchoredWidget {
    protected Constraints constraints;
    
    public Strut() { this(8,100); }
    public Strut(int width, int height) {
        super("strut");
        setBorder(BorderFactory.createLineBorder(Color.red, 2));
        constraints = new Constraints(Spring.constant( 0 ),
                Spring.constant( 0 ),
                Spring.constant( width ),
                Spring.constant( height ));
    }
    public Constraints getConstraints() { return constraints; }
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

    protected void writeContent(XMLWriter xml) throws SAXException {
        super.writeContent(xml);
        xml.startElement("size");
        xml.writeProperty("width", getWidth());
        xml.writeProperty("height", getHeight());
        xml.endElement("size");
    }
}
