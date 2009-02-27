package core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

    public ScrollBarWidget(IPatch patch, IParameter param) {
        super(patch, param);

        base = 0;
        labelWidth = -1;
        createWidgets();
        layoutWidgets();
    }
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
			   IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, pmodel, sender);
        this.base = base;
	this.labelWidth = labelWidth;

	createWidgets();
        layoutWidgets();
    }

    public ScrollBarWidget(String label, IPatch patch, int min, int max,
			   int base,
			   IParamModel pmodel, ISender sender) {
        this(label, patch, min, max, base, -1, pmodel, sender);
    }

    protected void createWidgets() {
	slider = new JSlider(JSlider.HORIZONTAL,
			     getValueMin(), getValueMax(), 
			     constrain(getValueMin(),getValue(),getValueMax()));
        slider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    eventListener(e);
		}
	    });
        slider.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
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

    protected int constrain(int min, int val, int max) {
        return val < min ? min : val > max ? max : val;
    }
    
    /** invoked when the slider is moved. */
    protected void eventListener(ChangeEvent e) {
	int v = slider.getValue();
	text.setText(new Integer(v + base).toString());
	sendSysex(v);
    }
    /** invoked when mouse wheel is moved. */
    protected void eventListener(MouseWheelEvent e) {
        JSlider t = (JSlider) e.getSource();
        //ErrorMsg.reportStatus("wheel : " + e.getWheelRotation());
	if (t.hasFocus()) // to make consistent with other operation.
	    t.setValue(t.getValue() - e.getWheelRotation());
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

    public void setMin(int min) {
        super.setMin(min);
        slider.setMinimum(min);
        slider.setValue(getValue());
    }
    public void setMax(int max) {
        super.setMax(max);
        slider.setMaximum(max);
        slider.setValue(getValue());
    }

    public void setEnabled(boolean e) {
        slider.setEnabled(e);
    }
}
