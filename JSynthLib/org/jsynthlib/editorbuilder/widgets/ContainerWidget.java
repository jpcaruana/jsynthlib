package org.jsynthlib.editorbuilder.widgets;

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.editorbuilder.Anchor;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class ContainerWidget extends LabeledWidget {
    private Collection kids = new LinkedList();
    private Widget[] kidArray;
    private Spring width = Spring.constant(0);
    private Spring height = Spring.constant(0);
    private Anchor wAnchor;
    private Anchor hAnchor;
    
    public ContainerWidget(String name, int w, int h) {
        super(name);
        setLayout(new SpringLayout());

        hAnchor = new Anchor(this, Anchor.SOUTH);
        hAnchor.setSideEditable(false);
        hAnchor.setTargetSide(Anchor.NORTH);
        hAnchor.setTarget(this);
        hAnchor.setPadding(h);
        wAnchor = new Anchor(this, Anchor.EAST);
        wAnchor.setSideEditable(false);
        wAnchor.setTargetSide(Anchor.WEST);
        wAnchor.setTarget(this);
        wAnchor.setPadding(w);
    }

    public void addWidget(Widget w) {
        addWidget(w, null);
    }
    public void addWidget(Widget w, Constraints constraints) {
        if (w == this) {
            System.out.println("Huh?");
        }
        if (constraints == null) {
            add(w);
        } else {
            add(w, constraints);
        }
        kids.add(w);
        kidArray = null;
    }    
    public Widget[] getWidgets() {
        if (kidArray == null) {
            kidArray = new Widget[kids.size()];
            kidArray = (Widget[]) kids.toArray(kidArray);
        }
        return kidArray;
    }
    
    public Collection getAllWidgets() {
        LinkedList result = new LinkedList();
        Iterator it = kids.iterator();
        while (it.hasNext()) {
            Object w = it.next();
            result.add(w);
            if (w instanceof ContainerWidget)
                result.addAll(0, ((ContainerWidget)w).getAllWidgets());
        }
        return result;
    }
    
    public void remove(Component comp) {
        kids.remove(comp);
        kidArray = null;
        super.remove(comp);
    }
    
    public Anchor getEWAnchor(Container parent) {
        if (parent != this)
            return super.getEWAnchor(parent);
        return wAnchor;
        
    }
    public Anchor getNSAnchor(Container parent) {
        if (parent != this)
            return super.getNSAnchor(parent);
        return hAnchor;
    }
    public Anchor getWidthAnchor() {
        return wAnchor;
    }
    public void setWidthAnchor(Anchor w) {
        wAnchor = w;
    }
    public Anchor getHeightAnchor() {
        return hAnchor;
    }
    public void setHeightAnchor(Anchor h) {
        hAnchor = h;
    }
    protected void writeContent(XMLWriter xml) throws SAXException {
        super.writeContent(xml);
        xml.startElement("size");
        writeAnchor(xml, wAnchor);
        writeAnchor(xml, hAnchor);
        xml.endElement("size");
        
        if (kids.size() > 0) {
            xml.startElement("widgets");
            Iterator it = kids.iterator();
            while (it.hasNext()) {
                ((Widget)it.next()).write(xml);
            }
            xml.endElement("widgets");
        }
    }

}
