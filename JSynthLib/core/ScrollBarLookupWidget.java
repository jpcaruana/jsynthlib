package core;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Horizontal scrollbar SysexWidget with value label field.
 * @version $Id$
 * @see ScrollBarWidget
 * @see VertScrollBarWidget
 */
public class ScrollBarLookupWidget extends SysexWidget {
    /**
     * Base value.  This value is added to the actual value for
     * display purposes.
     */
    protected int base;
    /** An array of label string for each value */
    protected String[] options;
    /** JTextField to display value. */
    protected JTextField text;
    /** JSlider widget */
    protected JSlider slider;
    /** width of label widget */
    protected int labelWidth;

    /** Constructor for setting up the ScrollBarLookupWidget.
     * @param label Label for the Widget
     * @param patch The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param labelWidth width of label. If negative value, the width
     * will be determined by label strings.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing
     * the parameter
     * @param options array of label string for each value.
     */
    public ScrollBarLookupWidget(String label, IPatch patch, int min, int max,
				 int labelWidth,
				 ParamModel pmodel, SysexSender sender,
				 String[] options) {
	super(label, patch, min, max, pmodel, sender);
	this.options = options;
	this.labelWidth = labelWidth;

	createWidgets();
	layoutWidgets();
    }

    public ScrollBarLookupWidget(String label, IPatch patch, int min, int max,
				 ParamModel pmodel, SysexSender sender,
				 String[] options) {
	this(label, patch, min, max, -1, pmodel, sender, options);
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

	if (labelWidth > 0) {
	    Dimension d = getJLabel().getPreferredSize();
	    d.setSize(labelWidth, d.getHeight() + 3);
	    getJLabel().setPreferredSize(d);
	    d = getJLabel().getMinimumSize();
	    d.setSize(labelWidth, d.getHeight() + 1);
	    getJLabel().setMinimumSize(d);
	}
    }

    /** invoked when the slider is moved. */
    protected void eventListener(ChangeEvent e) {
	int v = slider.getValue();
	text.setText(options[v]);
	sendSysex(v);
    }

    /** Adds a <code>ChangeListener</code> to the slider. */
    public void addEventListener(ChangeListener l) {
	slider.addChangeListener(l);
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
