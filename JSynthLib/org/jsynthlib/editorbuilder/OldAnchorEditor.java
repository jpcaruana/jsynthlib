package org.jsynthlib.editorbuilder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.editorbuilder.widgets.Widget;

public class OldAnchorEditor extends Box implements ActionListener,
java.io.Serializable, WidgetListListener {
    protected Anchor anchor;
    protected boolean vertical;
    protected LinkedList listeners = new LinkedList();
    protected JRadioButton rb0;
    protected JRadioButton rb1;
    protected JComboBox side;
    protected JComboBox widget;
    private WidgetComboBoxModel wModel;
    
    
    public OldAnchorEditor(Anchor a) {
        super(BoxLayout.Y_AXIS);
        
        GlassPane.addWidgetListener(this);
        if (a == null)
            return;
        anchor = a;
        if (anchor.getSide() < 2)
            vertical = true;
        else
            vertical = false;
        Box b = createHorizontalBox();
        add(b);
        String sideLabel = vertical ? "Height " : "Width ";
        if (anchor.isSideEditable()) {
            rb0 = new JRadioButton(vertical ? "North"
                    : "East" );
            rb0.addActionListener(this);
            ButtonGroup bg = new ButtonGroup();
            bg.add(rb0);
            b.add(rb0);
        
            rb1 = new JRadioButton(vertical ? "South"
                    : "West" );
            rb1.addActionListener(this);
            bg.add(rb1);
            b.add(rb1);
        
            if (anchor.getSide() % 2 == 0)
                bg.setSelected(rb0.getModel(), true);
            else
                bg.setSelected(rb1.getModel(), true);
        
            b = createHorizontalBox();
            add(b);
            sideLabel = "";
        }
        
        b.add(new JLabel(sideLabel + "connected to"));
        b.add(Box.createHorizontalGlue());
        
        side = new JComboBox(new String[] {
                "North","South","East","West"
        });
        side.setSelectedIndex(anchor.getTargetSide());
        side.addActionListener(this);
        b.add(side);
        
        b = createHorizontalBox();
        add(b);
        
        b.add(new JLabel("of"));
        b.add(Box.createHorizontalGlue());
        
        Widget t = getCurrentTarget();
        
        wModel = new WidgetComboBoxModel(anchor.getTargets());
        widget = new JComboBox(wModel);
        widget.setSelectedItem(t.getId());
        widget.addActionListener(this);
        b.add(widget);
        
        b = createHorizontalBox();
        add(b);
        
        b.add(new JLabel("with padding of"));
        b.add(Box.createHorizontalGlue());
        
        JTextField tf = new JTextField();
        tf.setText(""+anchor.getPadding());
        tf.addActionListener(this);
        b.add(tf);
    }
    
    private Widget getCurrentTarget() {
        Widget t = EditorBuilder.getDesignerFrame().getRootWidget();
        if (anchor.getTarget() instanceof Widget)
            t = (Widget)anchor.getTarget();
        return t;
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JRadioButton) {
            int newside = vertical ? 0 : 2;
            if (source == rb1)
                newside += 1;
            if (anchor.getSide() != newside) {
                anchor.setSide(newside);
            }
        } else if (source instanceof JComboBox) {
            if (source == side) {
                anchor.setTargetSide(((JComboBox)side).getSelectedIndex());
            } else if (source == widget) {
                if (wModel.isNotifying())
                    return;
                String id = (String)widget.getSelectedItem();
                Widget w = ((GlassPane)EditorBuilder.getDesignerFrame()
                        .getGlassPane()).getWidget(id);
                anchor.setTarget(w);
            }
        } else if (source instanceof JTextField) {
            try {
                anchor.setPadding(
                        Integer.parseInt(
                                ((JTextField)source).getText()));
            } catch (Exception ex) {}
            ((JTextField)source).setText("" + anchor.getPadding());
        } else
            throw new Error("Unrecognized source " + source);
        
        notifyListeners();
    }
    protected void notifyListeners() {
        Iterator it = listeners.iterator();
        while (it.hasNext())
            ((ChangeListener)it.next()).stateChanged(new ChangeEvent(this));
    }
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    
    /* (non-Javadoc)
     * @see org.jsynthlib.editorbuilder.WidgetListListener#listChanged()
     */
    public void listChanged() {
        if (anchor == null)
            return;
        wModel.update(anchor.getTargets());
        widget.setSelectedItem(getCurrentTarget().getId());
    }
    
}
