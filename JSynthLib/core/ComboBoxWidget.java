package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * @version $Id$
 */
public class ComboBoxWidget extends SysexWidget {
    /** JComboBox widget */
    public JComboBox cb;	// access by some drivers
    private Object[] options;

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
        setup();
    }

    public ComboBoxWidget(String l, Patch p,
			  ParamModel ofs, SysexSender s, Object [] o) {
        this(l, p, 0, ofs, s, o);
    }
    /*
    public ComboBoxWidget(String l, Patch p,
			  ParamModel ofs, SysexSender s, String [] o,
			  int valueInit) {
	this(l, p, 0, ofs, s, o);
	setValue(valueInit);
    }
    */
    protected void setup() {
        setLayout(new FlowLayout());
        cb = new JComboBox(options);
        cb.setSelectedIndex(getValue() - getValueMin());
        cb.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				sendSysex(cb.getSelectedIndex() + getValueMin());
			}
		}
	    });
        add(getJLabel());
        cb.setMaximumSize(new Dimension(125, 25));
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
