/* $Id$ */
package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class SpinnerWidget extends SysexWidget {
    private int base;
    private JTextField text;
    private JSpinner spinner;

    /**
     * Constructor for setting up the SpinnerWidget.
     * @param l Label for the Widget.
     * @param p The patch, which is edited.
     * @param min Minimum value.
     * @param max Maximum value.
     * @param b base value. This value is added to the actual value
     * for display purposes.
     * @param ofs a <code>ParamModel</code> instance.
     * @param s sysexSender for transmitting the value at editing the parameter
     * @see SysexWidget
     */
    public SpinnerWidget(String l, Patch p, int min, int max, int b,
			 ParamModel ofs, SysexSender s) {
        super(l, p, min, max, ofs, s);
        base = b;
        setup();
    }

    /*
    public SpinnerWidget(String l, Patch p, int min, int max, int b,
			 ParamModel ofs, SysexSender s, int valueInit) {
        this(l, p, min, max, b, ofs, s);
        setValue(valueInit);
    }
    */
    protected void setup() {
        setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);

        SpinnerNumberModel model = new SpinnerNumberModel
	    (getValue() + base, getValueMin() + base, getValueMax() + base, 1);
        spinner = new JSpinner(model);
        spinner.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    // Maybe the displayed value differ from sysex
		    // value for 'base'
		    sendSysex(((Integer) spinner.getValue()).intValue() - base);
		}
	    });
        add(spinner, BorderLayout.CENTER);
    }

    public void setValue(int v) {
	super.setValue(v);
	// Maybe the displayed value differ from sysex value for 'base'
	spinner.setValue(new Integer(v + base));
    }

    public void setMinMax(int min, int max) {
	super.setMinMax(min, max);
        ((SpinnerNumberModel) (spinner.getModel())).setMinimum(new Integer(min));
        ((SpinnerNumberModel) (spinner.getModel())).setMaximum(new Integer(max));
        spinner.setValue(new Integer(getValue()));
    }

    public void setEnabled(boolean e) {
        spinner.setEnabled(e);
    }
}
