package core;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ScrollBarLookupWidget extends SysexWidget {
    /**
     * Base value.  This value is added to the actual value for
     * display purposes.
     */
    protected int base;
    protected String[] options;
    /** JTextField to display value. */
    protected JTextField text;
    /** JSlider widget */
    public JSlider slider;	// accessed by some drivers !!!FIXIT!!!

    /** Constructor for setting up the ScrollBarWidgetLookup.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param ofs a <code>ParamModel</code> instance.
     * @param s sysexSender for transmitting the value at editing the parameter
     * @param o array of label string for each value.
     * @see SysexWidget
     */
    public ScrollBarLookupWidget(String l, Patch p, int min, int max,
				 ParamModel ofs, SysexSender s, String[] o) {
	super(l, p, min, max, ofs, s);
	options = o;

	createWidgets();
	layoutWidgets();
    }

    protected void createWidgets() {
        slider = new JSlider(JSlider.HORIZONTAL,
			     getValueMin(), getValueMax(), getValue());
	slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    eventListener(e);
		}
	    });
	text = new JTextField(options[getValue()], 4);
	text.setEditable(false);
    }

    /** invoked when the slider is moved. */
    protected void eventListener(ChangeEvent e) {
	int v = slider.getValue();
	text.setText(options[v]);
	sendSysex(v);
    }

    protected void layoutWidgets() {
	setLayout(new BorderLayout());

	slider.setMinimumSize(new Dimension(75, 25));
	slider.setMaximumSize(new Dimension(125, 25));

	add(getJLabel(), BorderLayout.WEST);
	add(slider, BorderLayout.CENTER);
	add(text, BorderLayout.EAST);
    }

    public void setValue(int v) {
	super.setValue(v);
	slider.setValue(v);
    }

    public void setEnabled(boolean e) {
        slider.setEnabled(e);
    }

    public void changeOptions(String[] o) {
	if (o != options) {
	    options = o;
	    text.setText(options[getValue()]);
	}
    }
}
