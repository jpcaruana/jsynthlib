package core;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Horizontal scrollbar SysexWidget.
 * @version $Id$
 * @see VertScrollBarWidget
 * @see ScrollBarLookupWidget
 */
public class ScrollBarWidget extends SysexWidget {
    /**
     * Base value.  This value is added to the actual value for
     * display purposes.
     */
    protected int base;
    /** JTextField to display value. */
    protected JTextField text;
    /** JSlider widget */
    protected JSlider slider;
    /** width of label widget */
    protected int labelWidth;

    /**Set Label Width explicitly to? zero disables*/
    protected int forceLabelWidth = 0; // for setForceLabelWidth

    /** Constructor for setting up the ScrollBarWidget.<p>
     *
     * For <code>labelWidth</code> you can obtain a length of a string
     * as follows;
     * <pre>
     * int len = (int) (new JLabel("longest string")).getPreferredSize().getWidth();
     * </pre>

     * @param label Label for the Widget
     * @param patch The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param base base value. This value is added to the actual value
     * for display purposes
     * @param labelWidth width of label. If negative value, the width
     * will be determined by label strings.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     * @see SysexWidget
     */
    public ScrollBarWidget(String label, IPatch patch, int min, int max,
			   int base, int labelWidth,
			   ParamModel pmodel, SysexSender sender) {
        super(label, patch, min, max, pmodel, sender);
        this.base = base;
	this.labelWidth = labelWidth;

	createWidgets();
        layoutWidgets();
    }

    public ScrollBarWidget(String label, IPatch patch, int min, int max,
			   int base,
			   ParamModel pmodel, SysexSender sender) {
        this(label, patch, min, max, base, -1, pmodel, sender);
    }

    /**
     * Constructor for setting up the ScrollBarWidget with an
     * initial value which overrides the value generated from paramModel.
     * @param valueInit initial value, displayed at construction of the widget
     * @deprecated call setValue(int) if really required.
     */
    public ScrollBarWidget(String l, IPatch p, int min, int max, int b,
			   ParamModel ofs, SysexSender s, int valueInit) {
	this(l, p, min, max, b, ofs, s);
        setValue(valueInit);
    }

    protected void createWidgets() {
	slider = new JSlider(JSlider.HORIZONTAL,
			     getValueMin(), getValueMax(), getValue());
        slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    eventListener(e);
		}
	    });
	text = new JTextField(new Integer(getValue() + base).toString(), 4);
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
	text.setText(new Integer(v + base).toString());
	sendSysex(v);
    }

    /** Adds a <code>ChangeListener</code> to the slider. */
    public void addEventListener(ChangeListener l) {
	slider.addChangeListener(l);
    }

    protected void layoutWidgets() {
        setLayout(new BorderLayout());

	slider.setMinimumSize(new Dimension(50, 25));
	slider.setMaximumSize(new Dimension(125, 25));

        add(getJLabel(), BorderLayout.WEST);
	add(slider, BorderLayout.CENTER);
	add(text, BorderLayout.EAST);
    }

    public void setValue(int v) {
	super.setValue(v);
	slider.setValue(v);
    }

    public void setMinMax(int min, int max) {
	super.setMinMax(min, max);
        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setValue(getValue());
    }

    public void setEnabled(boolean e) {
        slider.setEnabled(e);
    }

    /** This may be deprecated. */
    public void setForceLabelWidth(int i) {
	forceLabelWidth = i;

	if (forceLabelWidth > 0) {
	    Dimension d = getJLabel().getPreferredSize();
	    d.setSize(forceLabelWidth, d.getHeight() + 3);
	    getJLabel().setPreferredSize(d);
	    d = getJLabel().getMinimumSize();
	    d.setSize(forceLabelWidth, d.getHeight() + 1);
	    getJLabel().setMinimumSize(d);
	}
    }
}
