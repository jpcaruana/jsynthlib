package org.jsynthlib.editorbuilder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.editorbuilder.widgets.Widget;

public class PropertiesFrame extends JFrame implements java.io.Serializable, WidgetListListener, ActionListener, ChangeListener {
    
    protected JComboBox selection_cb;
    protected DesignerFrame designer = EditorBuilder.getDesignerFrame();
    protected JScrollPane sp;
    private Widget selection;
    
    private BeanPropertySheet s = new BeanPropertySheet();
    
    /*
    protected JTextField id;
    protected JTextField label;
    protected JTextField cn;
    protected JTextField width;
    protected JTextField height;
    protected OldAnchorEditor vanchor;
    protected OldAnchorEditor hanchor;
    protected JComboBox type;
    */
    private WidgetComboBoxModel model;
    
    public PropertiesFrame() {
        
        super("Properties");
        
        GlassPane.addWidgetListener(this);
        
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS));
        setSize(300,300);
        
        designer.addChangeListener(this);
        
        setSelection(designer.getSelectedWidget());
        
        if (getSelection() == null)
            setSelection(designer.getRootWidget());
        
        model = new WidgetComboBoxModel(GlassPane.getCommonWidgets());
        selection_cb = new JComboBox(model);
        //setMaxSize(selection_cb);
        selection_cb.addActionListener(this);
        getContentPane().add(selection_cb);
        JScrollPane sp = new JScrollPane(s,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setBorder(null);
        getContentPane().add(sp);
        s.addActionListener(this);
        //createBox();
    }
    /*
    protected void createBox() {
        if (sp != null)
            remove(sp);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        
        id = label = width = height = null;
        vanchor = hanchor = null;
        type = null;
        
        Box row = Box.createHorizontalBox();
        row.add(new JLabel("ID"));
        
        row.add(Box.createHorizontalGlue());
        
        id = new JTextField();
        setMaxSize(id);
        id.setText(getSelection().getId());
        id.addActionListener(this);
        row.add(id);
        
        box.add(row);
        
        if (getSelection() instanceof LabeledWidget) {
            row = Box.createHorizontalBox();
            row.add(new JLabel("Label"));
            row.add(Box.createHorizontalGlue());
            label = new JTextField();
            setMaxSize(label);
            label.setText( ((LabeledWidget)getSelection()).getText() );
            label.addActionListener(this);
            row.add(label);
            
            box.add(row);
        }
        
        if (getSelection() instanceof ButtonWidget) {
            box.add(new JLabel("Class to show:"));
            cn = new JTextField();
            setMaxSize(cn);
            cn.setText(((ButtonWidget)getSelection()).getClassName());
            cn.addActionListener(this);
            box.add(cn);
        }
        
        if (getSelection() instanceof ParameterWidget) {
            type = new JComboBox(((ParameterWidget)getSelection()).getTypes());
            type.addActionListener(this);
            box.add(type);
        }
        
        if (getSelection() instanceof RootPanelWidget) {
        }else if (getSelection() instanceof AnchoredWidget) {
            vanchor = new OldAnchorEditor(((AnchoredWidget)getSelection()).getNSAnchor());
            vanchor.addChangeListener(this);
            
            hanchor = new OldAnchorEditor(((AnchoredWidget)getSelection()).getEWAnchor());
            hanchor.addChangeListener(this);
            
            box.add(vanchor);
            box.add(hanchor);
        }

        if (getSelection() instanceof ContainerWidget) {
            OldAnchorEditor ae = new OldAnchorEditor(((AnchoredWidget) getSelection()).getNSAnchor(getSelection()));
            ae.addChangeListener(this);
            box.add(ae);
            ae = new OldAnchorEditor(((AnchoredWidget) getSelection()).getEWAnchor(getSelection()));
            ae.addChangeListener(this);
            box.add(ae);
        }
        
        if (getSelection() instanceof Strut) {
            row = Box.createHorizontalBox();
            row.add(new JLabel("Width"));
            row.add(Box.createHorizontalGlue());
            
            width = new JTextField();
            setMaxSize(width);
            width.setText("" + ((Strut)getSelection()).getWidth());
            width.addActionListener(this);
            row.add(width);
            
            row.add(Box.createHorizontalGlue());
            row.add(Box.createHorizontalGlue());
            
            row.add(new JLabel("Height"));
            row.add(Box.createHorizontalGlue());
            
            height = new JTextField();
            setMaxSize(height);
            height.setText("" + ((Strut)getSelection()).getHeight());
            height.addActionListener(this);
            row.add(height);
            
            box.add(row);
        }        
        
        box.add(Box.createVerticalGlue());
        sp = new JScrollPane(box);
        getContentPane().add(sp);
        validate();
        repaint();
    }
    
    protected void setMaxSize(JComponent tf) {
        tf.setMaximumSize(new Dimension((int)tf.getMaximumSize().getWidth(),
                (int)tf.getPreferredSize().getHeight()));
        
    }
    */
    
    public void stateChanged(ChangeEvent e) {
        /*
        if (e.getSource() instanceof OldAnchorEditor) {
            designer.validate();
            designer.repaint();
        } else {
        */
            Widget new_selection = designer.getSelectedWidget();
            if (new_selection == null)
                new_selection = designer.getRootWidget();
            if (new_selection != getSelection()) {
                setSelection(new_selection);
                selection_cb.setSelectedItem(getSelection().getId());
                //createBox();
            }
        //}
    }
    public void actionPerformed(ActionEvent e) {
        Object source  = e.getSource();
        if (source == null)
            return;
        
        if (source == selection_cb) {
            if (model.isNotifying()) {
                if (!getSelection().getId().equals(selection_cb.getSelectedItem()))
                    selection_cb.setSelectedItem(getSelection().getId());
                return;
            }
            Widget new_selection = ((GlassPane)designer.getGlassPane())
            .getWidget((String)selection_cb.getSelectedItem());
            if (new_selection != designer.getRootWidget())
                designer.setSelectedWidget(new_selection);
            return;
        } else if (source == s) {
            GlassPane.updateWidgetLists();
            designer.validate();
            designer.repaint();
        }
        /*
        if (source == type) {
            ((ParameterWidget)getSelection()).setType((String)type.getSelectedItem());
        } else if (source == id) {
            getSelection().setId(id.getText());
            GlassPane.updateWidgetLists();
        } else if (source == label) {
            ((LabeledWidget)getSelection()).setText(label.getText());
        } else if (source == cn) {
            ((ButtonWidget)getSelection()).setClassName(cn.getText());
        } else if (source == width) {
            try {
                ((Strut)getSelection()).setWidth(Integer.parseInt(width.getText()));
            } catch (Exception ex) {}
            width.setText("" + ((Strut)getSelection()).getWidth());
        } else if (source == height) {
            try {
                ((Strut)getSelection()).setHeight(Integer.parseInt(height.getText()));
            } catch (Exception ex) {}
            height.setText("" + ((Strut)getSelection()).getHeight());
        }
        */
        designer.validate();
        designer.repaint();
    }
    /* (non-Javadoc)
     * @see org.jsynthlib.editorbuilder.WidgetListListener#listChanged()
     */
    public void listChanged() {
        model.update(GlassPane.getCommonWidgets());
        if (getSelection() != null)
            selection_cb.setSelectedItem(getSelection().getId());
    }

    protected void setSelection(Widget selection) {
        this.selection = selection;
        s.setBean(selection);
    }

    protected Widget getSelection() {
        return selection;
    }
    
}
