package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class ScrollBarWidget extends SysexWidget {
    /** base value. This value is added to the actual value for
	display purposes
    */
    int base;
    JTextField text;
    public JSlider slider;

    /**
     * @param l
     * @param p
     * @param min
     * @param max
     * @param b
     * @param ofs
     * @param s  */
    public ScrollBarWidget(String l, Patch p, int min, int max, int b,
			   ParamModel ofs, SysexSender s) {
        valueMin = min;
        valueMax = max;
        paramModel = ofs;
        sysexString = s;
        setValue(p);
        base = b;
        label = l;
        patch = p;
        setup();
    }

    /** Constructor for setting up the ScrollBarWidget including an
     * initial value.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param b base value. This value is added to the actual value
     * for display purposes
     * @param ofs Offset of the parameter in the patch
     * @param s ysexSender for transmitting the value at editing the parameter
     * @param valueInit initial value, displayed at construction of the widget
     */
    public ScrollBarWidget(String l, Patch p, int min, int max, int b,
			   ParamModel ofs, SysexSender s, int valueInit) {
        this(l, p, min, max, b, ofs, s);
        setValue(valueInit);
    }

    public void setup() {
        super.setup();
        setLayout(new BorderLayout());
        jlabel = new JLabel(label);
        add(jlabel, BorderLayout.WEST);

        if (valueCurr > valueMax)
	    valueCurr = valueMax;
        slider = new JSlider(JSlider.HORIZONTAL, valueMin, valueMax, valueCurr);
        slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    setValue(slider.getValue());
		    sendSysex();
		    text.setText(new Integer(valueCurr + base).toString());
		}
	    });
	text = new JTextField(new Integer(valueCurr + base).toString(), 4);
	slider.setMinimumSize(new Dimension(50, 25));
	slider.setMaximumSize(new Dimension(125, 25));

	add(slider, BorderLayout.CENTER);
	add(text, BorderLayout.EAST);
	text.setEditable(false);
    }

    public void setValue(int v) {
	super.setValue(v);
	slider.setValue(v);
    }

    public void setMinMax(int min, int max) {
        valueMin = min;
	valueMax = max;
        if (valueCurr > max)
	    valueCurr = max;
        if (valueCurr < min)
	    valueCurr = min;
        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setValue(valueCurr);
    }

    public void setEnabled(boolean e) {
        slider.setEnabled(e);
    }
}
