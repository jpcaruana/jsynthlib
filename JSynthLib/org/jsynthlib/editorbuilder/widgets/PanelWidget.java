package org.jsynthlib.editorbuilder.widgets;
import javax.swing.BorderFactory;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class PanelWidget extends ContainerWidget {
    private boolean bordered = true;
    
    public PanelWidget() {
        this(100,100);   
    }
    public PanelWidget(int w, int h) {
        this("Panel", w, h);
    }
    
    public PanelWidget(String title, int w, int h) {
        this("panel",title, w, h);
    }
    protected PanelWidget(String name, String title, int w, int h) {
        super(name, w, h);
        setText(title);
    }
    public void setText(String title) {
        if (bordered)
            setBorder(BorderFactory.createTitledBorder(title));
        super.setText(title);
    }
    
    public void setBordered(boolean bordered) {
        this.bordered = bordered;
        if (bordered)
            setText(getText());
        else
            setBorder(null);
    }
    
    public boolean isBordered() {
        return bordered;
    }
    
    protected void writeContent(XMLWriter xml) throws SAXException {
        xml.writeProperty("bordered", Boolean.toString(bordered));
        super.writeContent(xml);
    }
    
    
}
