package org.jsynthlib.editorbuilder.widgets;

import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jsynthlib.editorbuilder.DesignerFrame;
import org.jsynthlib.editorbuilder.EditorBuilder;
import org.jsynthlib.utils.Writable;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class Widget extends JPanel implements Writable {
    private static int count;
    
    
    private String name;
    private String id;
    private String type;
    private WidgetPosition position;
    
    public Widget(String name) {
        this(name, name + " " + (count++));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }
    
    public Widget(String name, String id) {
        this.name = name;
        this.id = id;
        setName(id);
    }
    public final String getId() {
        return id;
    }
    public final void setId(String _id) {
        this.id = _id;
        setName(_id);
    }
    
    public String getWidgetName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
        validateParents();
    }
    
    public void write(XMLWriter xml) throws SAXException {
        startElement(xml);
        writeContent(xml);
        endElement(xml);
    }
    
    
    protected void startElement(XMLWriter xml) throws SAXException {
        String type = getType();
        if (type != null)
            xml.setAttribute("type", type);
        
        xml.startElement(getWidgetName());
        xml.writeProperty("id", getId());
    }
    
    protected void writeContent(XMLWriter xml) throws SAXException {
        
    }
    
    protected void endElement(XMLWriter xml) throws SAXException {
        xml.endElement(getWidgetName());
    }
    public void validateParents() {
        Iterator it = getParents().iterator();
        while (it.hasNext()) {
            ((Component)it.next()).validate();
        }        
    }
    public void validate() {
        super.validate();
        getParent().invalidate();
    }
    
    protected List getParents() {
        List result = new LinkedList();
        DesignerFrame f = EditorBuilder.getDesignerFrame();
        if (f == null)
            return result;
        Widget root = f.getRootWidget();
        if (this == root)
            return result;
        for (Component c = getParent();
        c != null && c != root;
        c = c.getParent()) {
            result.add(c);
        }
        return result;
    }
    public void poof() {
        Container c = getParent();
        c.remove(this);
        c.validate();
        if (c instanceof Widget)
            ((Widget)c).validateParents();
    }

}
