package org.jsynthlib.editorbuilder.widgets;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class ContainerWidget extends LabeledWidget {
    private Collection kids = new LinkedList();
    private Widget[] kidArray;
    
    public ContainerWidget(String name) {
        super(name);
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
    protected void writeContent(XMLWriter xml) throws SAXException {
        super.writeContent(xml);
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
