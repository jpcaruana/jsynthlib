package core;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

/**
 * @version $Id$
 */
public class ComboBoxWidget extends SysexWidget {
    public JComboBox cb;	// should be protected !!!FIXIT!!!
    protected Object[] options;

    /**
     * Constructor for setting up the ComboBoxWidget.
     * @param l Label for the Widget.
     * @param p The patch, which is edited.
     * @param min The minimum value (default 0).
     * @param ofs Offset of the parameter in the patch.
     * @param s SysexSender for transmitting the value at editing the
     * parameter.
     * @param o Array, which contains the list of the options in the combobox.
     * @see SysexWidget
     */
    public ComboBoxWidget(String l, Patch p, int min,
			  ParamModel ofs, SysexSender s, Object[] o) {
	super(l, p, min, min + o.length - 1, ofs, s);
        options = o;

	createWidgets();
        layoutWidgets();
    }

    public ComboBoxWidget(String l, Patch p,
			  ParamModel ofs, SysexSender s, Object [] o) {
        this(l, p, 0, ofs, s, o);
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
