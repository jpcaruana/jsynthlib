package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class ScrollBarWidget extends SysexWidget {
    /**
     * Base value.  This value is added to the actual value for
     * display purposes.
     */
    protected int base;
    /** JTextField to display value. */
    protected JTextField text;
    /** JSlider widget */
    public JSlider slider;	// accessed by PatchEditorFrame and some drivers

    /** Constructor for setting up the ScrollBarWidget.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param b base value. This value is added to the actual value
     * for display purposes
     * @param ofs a <code>ParamModel</code> instance.
     * @param s sysexSender for transmitting the value at editing the parameter
     * @see SysexWidget
     */
    public ScrollBarWidget(String l, Patch p, int min, int max, int b,
			   ParamModel ofs, SysexSender s) {
        super(l, p, min, max, ofs, s);
        base = b;
        setup();
    }
    /**
     * Constructor for setting up the ScrollBarWidget with an
     * initial value which overrides the value generated from paramModel.
     * @param valueInit initial value, displayed at construction of the widget
     * @deprecated call setValue(int) if really required.
     */
    // only KawaiK4EffectEditor calls this.
    public ScrollBarWidget(String l, Patch p, int min, int max, int b,
			   ParamModel ofs, SysexSender s, int valueInit) {
	this(l, p, min, max, b, ofs, s);
        setValue(valueInit);
    }

    protected void setup() {
        setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);

        slider = new JSlider(JSlider.HORIZONTAL,
			     getValueMin(), getValueMax(), getValue());
        slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    int v = slider.getValue();
		    text.setText(new Integer(v + base).toString());
		    sendSysex(v);
		}
	    });
	slider.setMinimumSize(new Dimension(50, 25));
	slider.setMaximumSize(new Dimension(125, 25));
	add(slider, BorderLayout.CENTER);

	text = new JTextField(new Integer(getValue() + base).toString(), 4);
	text.setEditable(false);
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
}
