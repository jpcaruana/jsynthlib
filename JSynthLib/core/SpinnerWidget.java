/* $Id$ */
package core;
import java.awt.BorderLayout;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Spinner SysexWidget.
 * @version $Id$
 */
public class SpinnerWidget extends SysexWidget {
    protected int base;
    protected JTextField text;
    protected JSpinner spinner;

    public SpinnerWidget(IPatch patch, IParameter param) {
        super(patch, param);

        base = 0;
        createWidgets();
        layoutWidgets();
    }
    /**
     * Constructor for setting up the SpinnerWidget.
     * @param label Label for the Widget.
     * @param patch The patch, which is edited.
     * @param min Minimum value.
     * @param max Maximum value.
     * @param base base value. This value is added to the actual value
     * for display purposes.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     */
    public SpinnerWidget(String label, IPatch patch, int min, int max, int base,
			 IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, pmodel, sender);
        this.base = base;

	createWidgets();
        layoutWidgets();
    }

    /*
    public SpinnerWidget(String l, Patch p, int min, int max, int b,
			 ParamModel ofs, SysexSender s, int valueInit) {
        this(l, p, min, max, b, ofs, s);
        setValue(valueInit);
    }
    */

    protected void createWidgets() {
        SpinnerNumberModel model = new SpinnerNumberModel
	    (getValue() + base, getValueMin() + base, getValueMax() + base, 1);
        spinner = new JSpinner(model);
        spinner.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    eventListener(e);
		}
	    });
    }

    /** invoked when the value is changed. */
    protected void eventListener(ChangeEvent e) {
	// Maybe the displayed value differ from sysex value for 'base'.
	sendSysex(((Integer) spinner.getValue()).intValue() - base);
    }

    /** Adds a <code>ChangeListener</code> to the spinner. */
    public void addEventListener(ChangeListener l) {
	spinner.addChangeListener(l);
    }

    protected void layoutWidgets() {
        setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);
        add(spinner, BorderLayout.CENTER);
    }

    public void setValue(int v) {
	super.setValue(v);
	// Maybe the displayed value differ from sysex value for 'base'.
	spinner.setValue(new Integer(v + base));
    }

    public void setMin(int min) {
        super.setMin(min);
        ((SpinnerNumberModel) (spinner.getModel())).setMinimum(new Integer(min));
        spinner.setValue(new Integer(getValue()));
    }
    public void setMax(int max) {
        super.setMax(max);
        ((SpinnerNumberModel) (spinner.getModel())).setMaximum(new Integer(max));
        spinner.setValue(new Integer(getValue()));
    }
    
    public void setEnabled(boolean e) {
        spinner.setEnabled(e);
    }
}
