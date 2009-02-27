package org.jsynthlib.jsynthlib.xml.editor;

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.jsynthlib.xml.XMLPatch;

public class PanelDescription extends WidgetDescription {
    
    private boolean bordered;
    private JPanel panel;
    private WidgetSize size;
    
    public PanelDescription() {
        super("panel");
    }
    
    /* (non-Javadoc)
     * @see org.jsynthlib.jsynthlib.xml.WidgetDescription#create(java.awt.Container, java.util.Map, org.jsynthlib.jsynthlib.xml.XMLPatch)
     */
    public Component create(Map widgets, XMLPatch patch)
    throws Exception {
        panel = new JPanel();
        if (bordered)
            panel.setBorder(BorderFactory.createTitledBorder(getTitle()));
        panel.setLayout(new SpringLayout());
        widgets.put(getId(), panel);
        createChildren(widgets, patch);
        
        return panel;
    }
    public void setBordered(String val) {
        bordered = Boolean.valueOf(val).booleanValue();
    }
    public void setWidgets(List widgets) {
        super.setWidgets(widgets);
    }
    public void setSize(WidgetSize s) {
        size = s;
    }
    public void position(Container parent, Map widgetMap) {
        super.position(parent, widgetMap);
        SpringLayout l = (SpringLayout) panel.getLayout();
        Constraints c = l.getConstraints(panel);
        Anchor a = size.getRight();
        c.setConstraint(a.getSide(), a.getSpring(l, widgetMap));
        a = size.getBottom();
        c.setConstraint(a.getSide(), a.getSpring(l, widgetMap));
    }
}
