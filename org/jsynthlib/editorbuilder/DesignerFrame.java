package org.jsynthlib.editorbuilder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.editorbuilder.widgets.PanelWidget;
import org.jsynthlib.editorbuilder.widgets.RootPanelWidget;
import org.jsynthlib.editorbuilder.widgets.Widget;
import org.jsynthlib.utils.AutoSpringLayout;
import org.jsynthlib.utils.XMLWriter;

import core.ErrorMsg;

public class DesignerFrame extends JFrame implements ContainerListener {
    
    protected JScrollPane sp;
    protected AutoSpringLayout layout;
    protected Widget selected;
    protected java.util.List selection_listeners = new LinkedList();
    protected RootPanelWidget widget;
    
    public DesignerFrame(boolean dummy) {
        super("Untitled.xml");
        setSize(300,300);
        
        GlassPane glass = new GlassPane(this);
        setGlassPane(glass);
        glass.setVisible(true);
        
        widget = new RootPanelWidget(600, 400);
        glass.addWidget(widget);
        
        sp = new JScrollPane(widget,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setBorder(null);
        getContentPane().add(sp, BorderLayout.CENTER);
        
        Container c = widget;
        
        c.removeContainerListener(this);
        /*
        Strut strut = new Strut(8,400);
        widget.addWidget(strut, strut.getConstraints());
        SpringLayout l = (SpringLayout) c.getLayout();
        l.putConstraint(SpringLayout.EAST, widget, 0, SpringLayout.EAST, strut);
        l.putConstraint(SpringLayout.SOUTH, widget, 0, SpringLayout.SOUTH, strut);
        strut.getEWAnchor().setTarget(widget);
        strut.getEWAnchor().setPadding(592);
        strut.getNSAnchor().setTarget(widget);
        glass.addWidget(strut);
        */
    }
    
    public JScrollPane getScrollPane() { return sp; }
    public PanelWidget getRootWidget() { return widget; }
    
    public Widget getSelectedWidget() { return selected; }
    public void setSelectedWidget(Widget _selected) {
        selected = _selected;
        Iterator it = selection_listeners.iterator();
        while (it.hasNext())
            ((ChangeListener)it.next()).stateChanged(new ChangeEvent(this));
        validate();
        repaint();
    }
    public void addChangeListener(ChangeListener listener) {
        selection_listeners.add(listener);
    }
    
    public void componentAdded(ContainerEvent e) {
        Component c = e.getChild();
        if (c instanceof Widget)
            ((GlassPane)getGlassPane()).addWidget((Widget)c);
        if (c instanceof PanelWidget)
            ((Container)c).addContainerListener(this);
    }
    public void componentRemoved(ContainerEvent e) {}
    
    public void export(File file) {
        try {
            XMLWriter xml = new XMLWriter(file);
            xml.write(new Editor(widget));
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Error saving Editor", ex);
        }
    }
    public void validateTree() {
        super.validateTree();
    }
    
}
