package org.jsynthlib.editorbuilder.widgets;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class ContainerWidget extends AnchoredWidget {
    private Collection kids = new LinkedList();
    private Widget[] kidArray;
    
    public ContainerWidget(String name) {
        super(name);
    }

    public void addWidget(Widget w) {
        add(w);
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

}
