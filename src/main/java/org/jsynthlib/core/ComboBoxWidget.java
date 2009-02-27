package org.jsynthlib.core;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * A SysexWidget class features JComboBox widget.
 * @version $Id$
 */
public class ComboBoxWidget extends SysexWidget {
    /**
     * JComboBox object.  This should be protected, but many patch
     * editors access this directly.
     */
    public JComboBox cb;	// should be protected !!!FIXIT!!!
    /** An array of the list of the options in the ComboBox. */
    protected Object[] options;

    public ComboBoxWidget(IPatch patch, IParameter param) {
        super(patch, param);
        options = param.getValues();
        
        createWidgets();
        layoutWidgets();
    }
    /**
     * Constructor for setting up the ComboBoxWidget.
     * @param label Label for the Widget.
     * @param patch The patch, which is edited.
     * @param min The minimum value (default 0).
     * @param pmodel sysex parameter model.
     * @param sender SysexSender for transmitting the value at editing the
     * parameter.
     * @param options Array, which contains the list of the options in the combobox.
     * @see SysexWidget
     */
    public ComboBoxWidget(String label, IPatch patch, int min,
			  IParamModel pmodel, ISender sender, Object[] options) {
	super(label, patch, min, min + options.length - 1, pmodel, sender);
        this.options = options;

	createWidgets();
        layoutWidgets();
    }

    /** <code>min</code> is set to 0. */
    public ComboBoxWidget(String label, IPatch patch,
			  IParamModel pmodel, ISender sender, Object [] options) {
        this(label, patch, 0, pmodel, sender, options);
    }

    protected void createWidgets() {
        cb = new JComboBox(options);
        cb.setSelectedIndex(getValue() - getValueMin());
        cb.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    eventListener(e);
		}
	    });
    }

    /** invoked when the an item is selected. */
    protected void eventListener(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
	    sendSysex(cb.getSelectedIndex() + getValueMin());
	}
    }

    /** Adds an <code>ActionListener</code> to the ComboBox. */
    public void addEventListener(ActionListener l) {
	cb.addActionListener(l);
    }

    /** Adds an <code>ItemListener</code> to the ComboBox. */
    public void addEventListener(ItemListener l) {
	cb.addItemListener(l);
    }

    protected void layoutWidgets() {
        setLayout(new FlowLayout());
        cb.setMaximumSize(new Dimension(125, 25));

        add(getJLabel());
        add(cb);
    }

    public void setValue(int v) {
	super.setValue(v);
	cb.setSelectedIndex(v - getValueMin());
    }

    public void setEnabled(boolean e) {
        cb.setEnabled(e);
    }
}
