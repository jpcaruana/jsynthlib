package org.jsynthlib.editorbuilder;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.editorbuilder.widgets.AnchoredWidget;
import org.jsynthlib.editorbuilder.widgets.ButtonWidget;
import org.jsynthlib.editorbuilder.widgets.LabeledWidget;
import org.jsynthlib.editorbuilder.widgets.PanelWidget;
import org.jsynthlib.editorbuilder.widgets.ParameterWidget;
import org.jsynthlib.editorbuilder.widgets.Strut;
import org.jsynthlib.editorbuilder.widgets.Widget;

public class PropertiesFrame extends JFrame implements ActionListener,
ChangeListener,
java.io.Serializable, WidgetListListener {
    
    protected JComboBox selection_cb;
    protected DesignerFrame designer = EditorBuilder.getDesignerFrame();
    protected JScrollPane sp;
    protected Widget selection;
    
    protected JTextField id;
    protected JTextField label;
    protected JTextField cn;
    protected JTextField x;
    protected JTextField y;
    protected JTextField width;
    protected JTextField height;
    protected AnchorEditor vanchor;
    protected AnchorEditor hanchor;
    protected JComboBox type;
    private WidgetComboBoxModel model;
    
    public PropertiesFrame() {
        
        super("Properties");
        
        GlassPane.addWidgetListener(this);
        
        getContentPane().setLayout(new BoxLayout(getContentPane(),
                BoxLayout.Y_AXIS));
        setSize(300,300);
        
        designer.addChangeListener(this);
        
        selection = designer.getSelectedWidget();
        
        if (selection == null)
            selection = designer.getRootWidget();
        
        model = new WidgetComboBoxModel(GlassPane.getCommonWidgets());
        selection_cb = new JComboBox(model);
        setMaxSize(selection_cb);
        selection_cb.addActionListener(this);
        getContentPane().add(selection_cb);
        
        createBox();
    }
    
    protected void createBox() {
        if (sp != null)
            remove(sp);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        
        id = label = x = y = width = height = null;
        vanchor = hanchor = null;
        type = null;
        
        Box row = Box.createHorizontalBox();
        if (selection instanceof ParameterWidget)
            row.add(new JLabel("Address"));
        else
            row.add(new JLabel("ID"));
        
        row.add(Box.createHorizontalGlue());
        
        id = new JTextField();
        setMaxSize(id);
        id.setText(selection.getId());
        id.addActionListener(this);
        row.add(id);
        
        box.add(row);
        
        if (selection instanceof LabeledWidget) {
            row = Box.createHorizontalBox();
            row.add(new JLabel("Label"));
            row.add(Box.createHorizontalGlue());
            label = new JTextField();
            setMaxSize(label);
            label.setText( ((LabeledWidget)selection).getText() );
            label.addActionListener(this);
            row.add(label);
            
            box.add(row);
        }
        
        if (selection instanceof ButtonWidget) {
            box.add(new JLabel("Class to show:"));
            cn = new JTextField();
            setMaxSize(cn);
            cn.setText(((ButtonWidget)selection).getClassName());
            cn.addActionListener(this);
            box.add(cn);
        }
        
        if (selection instanceof ParameterWidget) {
            type = new JComboBox(((ParameterWidget)selection).getTypes());
            type.addActionListener(this);
            box.add(type);
        }
        
        if (selection instanceof Strut) {
            row = Box.createHorizontalBox();
            row.add(new JLabel("X"));
            row.add(Box.createHorizontalGlue());
            
            x = new JTextField();
            setMaxSize(x);
            x.setText("" + ((Strut)selection).getCX());
            x.addActionListener(this);
            row.add(x);
            
            row.add(Box.createHorizontalGlue());
            row.add(Box.createHorizontalGlue());
            
            row.add(new JLabel("Y"));
            row.add(Box.createHorizontalGlue());
            
            y = new JTextField();
            setMaxSize(y);
            y.setText("" + ((Strut)selection).getCY());
            y.addActionListener(this);
            row.add(y);
            
            box.add(row);
            
            row = Box.createHorizontalBox();
            row.add(new JLabel("Width"));
            row.add(Box.createHorizontalGlue());
            
            width = new JTextField();
            setMaxSize(width);
            width.setText("" + ((Strut)selection).getWidth());
            width.addActionListener(this);
            row.add(width);
            
            row.add(Box.createHorizontalGlue());
            row.add(Box.createHorizontalGlue());
            
            row.add(new JLabel("Height"));
            row.add(Box.createHorizontalGlue());
            
            height = new JTextField();
            setMaxSize(height);
            height.setText("" + ((Strut)selection).getHeight());
            height.addActionListener(this);
            row.add(height);
            
            box.add(row);
        } else if (selection instanceof PanelWidget && ((PanelWidget)selection).isRoot()){ 
        }else if (selection instanceof AnchoredWidget) {
            vanchor = new AnchorEditor(((AnchoredWidget)selection).getNSAnchor());
            vanchor.addChangeListener(this);
            
            hanchor = new AnchorEditor(((AnchoredWidget)selection).getEWAnchor());
            hanchor.addChangeListener(this);
            
            box.add(vanchor);
            box.add(hanchor);
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
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof AnchorEditor) {
            designer.validate();
            designer.repaint();
        } else {
            Widget new_selection = designer.getSelectedWidget();
            if (new_selection == null)
                new_selection = designer.getRootWidget();
            if (new_selection != selection) {
                selection = new_selection;
                selection_cb.setSelectedItem(selection.getId());
                createBox();
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source  = e.getSource();
        if (source == null)
            return;
        
        if (source == selection_cb) {
            if (model.isNotifying()) {
                if (!selection.getId().equals(selection_cb.getSelectedItem()))
                    selection_cb.setSelectedItem(selection.getId());
                return;
            }
            Widget new_selection = ((GlassPane)designer.getGlassPane())
            .getWidget((String)selection_cb.getSelectedItem());
            if (new_selection != designer.getRootWidget())
                designer.setSelectedWidget(new_selection);
            return;
        }
        if (source == type) {
            ((ParameterWidget)selection).setType((String)type.getSelectedItem());
        } else if (source == id) {
            selection.setId(id.getText());
            GlassPane.updateWidgetLists();
        } else if (source == label) {
            ((LabeledWidget)selection).setText(label.getText());
        } else if (source == cn) {
            ((ButtonWidget)selection).setClassName(cn.getText());
        } else if (source == x) {
            try {
                ((Strut)selection).setX(Integer.parseInt(x.getText()));
            } catch (Exception ex) {}
            x.setText("" + ((Strut)selection).getCX());
        } else if (source == y) {
            try {
                ((Strut)selection).setY(Integer.parseInt(y.getText()));
            } catch (Exception ex) {}
            y.setText("" + ((Strut)selection).getCY());
        } else if (source == width) {
            try {
                ((Strut)selection).setWidth(Integer.parseInt(width.getText()));
            } catch (Exception ex) {}
            width.setText("" + ((Strut)selection).getWidth());
        } else if (source == height) {
            try {
                ((Strut)selection).setHeight(Integer.parseInt(height.getText()));
            } catch (Exception ex) {}
            height.setText("" + ((Strut)selection).getHeight());
        }
        designer.validate();
        designer.repaint();
    }
    
    /* (non-Javadoc)
     * @see org.jsynthlib.editorbuilder.WidgetListListener#listChanged()
     */
    public void listChanged() {
        model.update(GlassPane.getCommonWidgets());
        if (selection != null)
            selection_cb.setSelectedItem(selection.getId());
    }
    
}
