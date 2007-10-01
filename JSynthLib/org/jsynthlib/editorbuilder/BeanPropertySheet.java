package org.jsynthlib.editorbuilder;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;


public class BeanPropertySheet extends JPanel {

    public class TextWrapper extends PropertyEditorWrapper implements FocusListener, ActionListener {

        private JTextField field;

        public TextWrapper(Object bean, PropertyDescriptor desc,
                PropertyEditor editor) {
            super(bean, desc, editor);
            field = new JTextField();
            field.addActionListener(this);
            field.addFocusListener(this);
        }

        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(FocusEvent arg0) {
            // Do nothing
        }

        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost(FocusEvent arg0) {
            actionPerformed(null);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0) {
            editor.setAsText(field.getText());
        }
        public void refresh() {
            super.refresh();
            Object v = getValue();
            if (v == null)
                field.setText("");
            else
                field.setText(v.toString());
        }

        /* (non-Javadoc)
         * @see org.jsynthlib.editorbuilder.BeanPropertySheet.PropertyEditorWrapper#getComponent()
         */
        public Component getComponent() {
            return field;
        }
    }
    public class TagWrapper extends PropertyEditorWrapper implements ActionListener {

        private JComboBox combo;

        public TagWrapper(Object bean, PropertyDescriptor desc,
                PropertyEditor editor) {
            super(bean, desc, editor);
            combo = new JComboBox(editor.getTags());
            combo.addActionListener(this);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0) {
            editor.setAsText((String) combo.getSelectedItem());
        }
        
        
        public void refresh() {
            super.refresh();
            combo.removeActionListener(this);
            combo.setSelectedItem(getValue().toString());
            combo.addActionListener(this);
        }

        /* (non-Javadoc)
         * @see org.jsynthlib.editorbuilder.BeanPropertySheet.PropertyEditorWrapper#getComponent()
         */
        public Component getComponent() {
            return combo;
        }
        public Object getValue() {
            Object o = super.getValue();
            if (o == null)
                return null;
            
            String cur = o.toString();
            String backup = null;
            String[] tags = editor.getTags();
            for (int i = 0; i < tags.length; i++) {
                if (tags[i].equals(cur))
                    return cur;
                else if (tags[i].equalsIgnoreCase(cur))
                    backup = tags[i];
            }
            return backup;
        }
    }
    public class CustomWrapper extends PropertyEditorWrapper implements ActionListener {

        JButton b;
        JDialog d;
        public CustomWrapper(Object bean, PropertyDescriptor desc,
                PropertyEditor editor) {
            super(bean, desc, editor);
            b = new JButton("Edit");
            b.addActionListener(this);
            d = new JDialog(getFrame(), true);
            //d.getContentPane().add(editor.getCustomEditor());
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Container c = d.getContentPane();
            c.removeAll();
            Component ed = editor.getCustomEditor();
            if (ed instanceof JComponent)
                ((JComponent) ed).setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            c.add(ed);
            d.pack();
            d.setVisible(true);
        }

        /* (non-Javadoc)
         * @see org.jsynthlib.editorbuilder.BeanPropertySheet.PropertyEditorWrapper#getComponent()
         */
        public Component getComponent() {
            return b;
        }
        

    }
    protected abstract class PropertyEditorWrapper implements PropertyChangeListener {
        protected Object bean;
        protected PropertyDescriptor desc;
        protected PropertyEditor editor;
        protected Object value;
        
        public PropertyEditorWrapper(Object bean, PropertyDescriptor desc, PropertyEditor editor) {
            this.bean = bean;
            this.desc = desc;
            this.editor = editor;
            editor.addPropertyChangeListener(this);
        }

        public void refresh() {
            Object newVal = getValue();
            if (newVal == value || (newVal != null && newVal.equals(value)))
                return;
            value = newVal;
            editor.removePropertyChangeListener(this);
            editor.setValue(value);
            editor.addPropertyChangeListener(this);
        }

        public Object getValue() {
            Method m = desc.getReadMethod();
            try {
                return m.invoke(bean, new Object[0]);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean setValue(Object value) {
            if (value == null || value != this.value) {
                Method m = desc.getWriteMethod();
                try {
                    m.invoke(bean, new Object[] { value });
                } catch (Exception ex) {
                    return false;
                }
                refreshEditors();
            }
            return true;
        }
        
        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent e) {
            setValue(editor.getValue());
        }
        
        public abstract Component getComponent();
    }
    protected static final int spacing = 6;
    
    private List editors = new LinkedList();
    private List labels = new LinkedList();
    private Spring height;
    private Spring nameSpring;
    private Spring nameWidthSpring;
    private Spring valueWidthSpring;

    private SpringLayout layout;
    
    private LinkedList listeners = new LinkedList();

    public BeanPropertySheet() {
        layout = new SpringLayout();
        setLayout(layout);
        //setBorder(BorderFactory.createLineBorder(Color.red));
    }
    
    public void setBean(Object bean) {
        reset();
        if (bean == null)
            return;
        BeanInfo bi;
        try {
            bi = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return;
        }
        LinkedHashMap props = new LinkedHashMap();
        findProperties(bi, props);
        Iterator it = props.values().iterator();
        editors = new ArrayList(props.size());
        while (it.hasNext()) {
            addProperty((PropertyDescriptor) it.next(), bean);
        }
        positionEditors();
        //SpringUtilities.makeCompactGrid(this,editors.size(), 2, 6, 6, 6, 3);
        refreshEditors();
        invalidate();
        setVisible(true);
    }

    private void findProperties(BeanInfo bi, LinkedHashMap props) {
        BeanInfo others[] = bi.getAdditionalBeanInfo();
        if (others != null) {
            for (int i = 0; i < others.length; i++) {
                findProperties(others[i], props);
            }
        }
        PropertyDescriptor descs[] = bi.getPropertyDescriptors();
        if (descs == null)
            return;
        for (int i = 0; i < descs.length; i++)
            props.put(descs[i].getName(), descs[i]);
    }

    private void positionEditors() {
        Spring width = Spring.sum(Spring.sum(nameWidthSpring, valueWidthSpring), spacer(6));
        Spring valueSpring = Spring.sum(nameWidthSpring, spacer(4));

        Constraints myc = layout.getConstraints(this);
        myc.setConstraint(SpringLayout.SOUTH,Spring.sum(height, Spring.constant(6,6,Integer.MAX_VALUE)));
        myc.setConstraint(SpringLayout.EAST, width);

        Iterator it = editors.iterator();
        while (it.hasNext()) {
            PropertyEditorWrapper w = (PropertyEditorWrapper) it.next();
            Constraints c = layout.getConstraints(w.getComponent());
            c.setX(valueSpring);
            c.setWidth(valueWidthSpring);
        }
        it = labels.iterator();
        while (it.hasNext()) {
            Constraints c = layout.getConstraints((Component) it.next());
            c.setWidth(nameWidthSpring);
        }
    }

    private void reset() {
        setVisible(false);
        removeAll();
        editors.clear();
        height = spacer(2);
        nameSpring = spacer(2);
        nameWidthSpring = Spring.constant(0);
        valueWidthSpring = Spring.constant(0);
    }

    protected Spring spacer() {
        return spacer(1);
    }
    protected Spring spacer(int multiplier) {
        return Spring.constant(multiplier*spacing);
        //return Spring.constant(0, multiplier*spacing, Integer.MAX_VALUE);
    }
    
    protected void addProperty(PropertyDescriptor descriptor, Object bean) {
        if (descriptor.isHidden())
            return;
        if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null)
            return;
        PropertyEditor editor = getEditor(descriptor);
        if (editor == null)
            return;
        PropertyEditorWrapper wrapper = addWrapper(bean, descriptor, editor);
        if (wrapper == null)
            return;
        
        JLabel name = addLabel(descriptor);
        height = Spring.sum(Spring.max(layout.getConstraint(SpringLayout.SOUTH, name),
                            layout.getConstraint(SpringLayout.SOUTH, wrapper.getComponent())), spacer());
        editors.add(wrapper);
        labels.add(name);
    }

    protected PropertyEditorWrapper addWrapper(Object bean, PropertyDescriptor descriptor, PropertyEditor editor) {
        PropertyEditorWrapper w;
        if (editor.supportsCustomEditor())
            w = new CustomWrapper(bean, descriptor, editor);
        else if (editor.getTags() != null)
            w = new TagWrapper(bean, descriptor, editor);
        else {
            try {
                w = new TextWrapper(bean, descriptor, editor);
            } catch (Exception ex) {
                return null;
            }
        }
        add(w.getComponent());
        Constraints c = layout.getConstraints(w.getComponent());
        c.setY(height);
        valueWidthSpring = Spring.max(valueWidthSpring, layout.getConstraints(w.getComponent()).getWidth());
        return w;
    }

    protected JLabel addLabel(PropertyDescriptor descriptor) {
        JLabel name = new JLabel(descriptor.getDisplayName());
        add(name);
        Constraints c = layout.getConstraints(name);
        c.setX(nameSpring);
        c.setY(height);
        nameWidthSpring = Spring.max(nameWidthSpring, layout.getConstraints(name).getWidth());
        return name;
    }

    protected PropertyEditor getEditor(PropertyDescriptor descriptor) {
        Class c = descriptor.getPropertyEditorClass();
        PropertyEditor e = null;
        if (c != null)
            try {
                e = (PropertyEditor) c.newInstance();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        if (e == null)
            e = PropertyEditorManager.findEditor(descriptor.getPropertyType());
        return e;
    }
    
    protected void refreshEditors() {
        Iterator it = editors.iterator();
        while (it.hasNext()) {
            PropertyEditorWrapper w = (PropertyEditorWrapper) it.next();
            w.refresh();
        }
        notifyListeners();
    }
    
    public Frame getFrame() {
        Container c = getParent();
        while (!(c == null || c instanceof Frame))
            c = c.getParent();
        return (Frame)c;
    }
    
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }
    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }
    private void notifyListeners() {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            ActionListener l = (ActionListener)it.next();
            l.actionPerformed(new ActionEvent(this, 0, ""));
        }
    }
}
