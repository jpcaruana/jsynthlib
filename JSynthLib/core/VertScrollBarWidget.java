package core;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Vertical scrollbar SysexWidget.
 * @version $Id$
 * @see ScrollBarWidget
 * @see ScrollBarLookupWidget
 */
public class VertScrollBarWidget extends ScrollBarWidget {
    /** Constructor for setting up the VertScrollBarWidget.
     * @param l Label for the Widget
     * @param p The patch, which is edited
     * @param min Minimum value
     * @param max Maximum value
     * @param b base value. This value is added to the actual value
     * for display purposes
     * @param ofs a <code>ParamModel</code> instance.
     * @param s sysexSender for transmitting the value at editing the parameter
     */
    public VertScrollBarWidget(String l, IPatch p, int min, int max, int b,
			       ParamModel ofs, SysexSender s) {
	super(l, p, min, max, b, ofs, s);
    }

    protected void createWidgets() {
	slider = new JSlider(JSlider.VERTICAL,
			     getValueMin(), getValueMax(), getValue());
	slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    eventListener(e);
		}
	    });
	text = new JTextField(new Integer(getValue() + base).toString(), 4);
	text.setEditable(false);
    }

    protected void layoutWidgets() {
	setLayout(new BorderLayout());

	slider.setMinimumSize(new Dimension(25, 50));
	slider.setMaximumSize(new Dimension(25, 100));

	add(getJLabel(), BorderLayout.NORTH);
	add(slider, BorderLayout.CENTER);
	add(text, BorderLayout.SOUTH);
    }
}
