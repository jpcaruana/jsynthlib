package org.jsynthlib.editorbuilder;
import java.awt.Component;
import java.beans.PropertyEditorSupport;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AnchorEditor extends PropertyEditorSupport implements ChangeListener {
    private Anchor editorValue;
    
    public Component getCustomEditor() {
        OldAnchorEditor ae = new OldAnchorEditor(editorValue);
        ae.addChangeListener(this);
        return ae;
    }
    public boolean supportsCustomEditor() {
        return true;
    }

    public void setValue(Object value) {
        try {
            editorValue = (Anchor)((Anchor)value).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        super.setValue(value);
    }
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        try {
            super.setValue(editorValue.clone());
        } catch (CloneNotSupportedException e1) {
            e1.printStackTrace();
        }
    }
}
