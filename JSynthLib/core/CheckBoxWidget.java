package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class CheckBoxWidget extends SysexWidget {
    public JCheckBox cb;	// will be private

    /**
     * Creates a new <code>CheckBoxWidget</code> instance.
     *
     * @param l a label text for the sysexWidget.
     * @param p a <code>Patch</code>, which is edited.
     * @param ofs a <code>ParamModel</code> instance.
     * @param s a <code>SysexSender</code> instance.
     * @see SysexWidget
     */
    public CheckBoxWidget(String l, Patch p, ParamModel ofs, SysexSender s) {
	super(l, p, 0, 1, ofs, s);
	setup();
    }

    // comment out since exactly same as one in SysexWidget.java
    /*
    public void setSliderNum(int num) {
	sliderNum = num;
	if (num > 0)
	    cb.setToolTipText("Bank " + ((num - 1) / 16)
			      + "  Slider " + (((num - 1) % 16) + 1));
	if (num < 0) {
	    num = 0 - num;
	    cb.setToolTipText("Bank " + ((num - 1) / 16)
			      + "  Button " + (((num - 1) % 16) + 1));
	}
    }
    */

    protected void setup() {
	setLayout(new BorderLayout());
	if (getValue() == 0)
	    cb = new JCheckBox(getLabel(), false);
	else
	    cb = new JCheckBox(getLabel(), true);
	cb.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED)
			sendSysex(1);
		    else
			sendSysex(0);
		}
	    });
	add(cb);
    }

    public void setValue(int v) {
	super.setValue(v);
	//cb.doClick();
	cb.setSelected(v != 0);
    }

    public void setLabel(String l) {
	_setLabel(l);
	cb.setText(l);
    }

    public void setEnabled(boolean e) {
        cb.setEnabled(e);
    }
}
