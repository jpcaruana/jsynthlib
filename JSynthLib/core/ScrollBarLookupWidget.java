package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class ScrollBarLookupWidget extends SysexWidget {
    int base;
    JTextField text;
    public JSlider slider;
    String[] options;

    public ScrollBarLookupWidget(String l, Patch p, int min, int max,
				 ParamModel ofs, SysexSender s, String[] o) {
	valueMin = min;
	valueMax = max;
	paramModel = ofs;
	sysexString = s;
	setValue(p);
	label = l;
	patch = p;
	options = o;
	setup();
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
		    text.setText(options[valueCurr]);
		}
	    });
	text = new JTextField(options[valueCurr], 4);
	slider.setMinimumSize(new Dimension(75, 25));
	slider.setMaximumSize(new Dimension(125, 25));

	add(slider, BorderLayout.CENTER);
	add(text, BorderLayout.EAST);
	text.setEditable(false);
    }

    public void setValue(int v) {
	super.setValue(v);
	slider.setValue(v);
    }
}
