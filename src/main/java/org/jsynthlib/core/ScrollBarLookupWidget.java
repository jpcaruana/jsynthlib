package org.jsynthlib.core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

    public ScrollBarLookupWidget(IPatch patch, IParameter param) {
        super(patch, param);

        options = param.getValues();
        labelWidth = -1;
        createWidgets();
        layoutWidgets();
    }
    /** Constructor for setting up the ScrollBarLookupWidget.
     * 
     * @param label Label for the Widget
     * @param patch The patch, which is edited
     * @param min minimum value
     * @param max maximum value
     * @param labelWidth width of the label. If the value is negative, the width
     * will be determined by the label strings.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     * @param options array of label string for each value.
     */
    public ScrollBarLookupWidget(String label, IPatch patch, int min, int max,
				 int labelWidth,
				 IParamModel pmodel, ISender sender,
				 String[] options) {
	super(label, patch, min, max, pmodel, sender);
	this.options = options;
	this.labelWidth = labelWidth;

	createWidgets();
	layoutWidgets();
    }

    /** Constructor for setting up the VertScrollBarLookupWidget.<br>
     * The width of the label will be determined by the label strings.
     * 
     * @param label label for the Widget
     * @param patch the patch, which is edited
     * @param min minimum value
     * @param max maximum value
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     * @param options array of label strings for each value.
     */
    public ScrollBarLookupWidget(String label, IPatch patch, int min, int max,
				 IParamModel pmodel, ISender sender,
				 String[] options) {
	this(label, patch, min, max, -1, pmodel, sender, options);
    }
    
    /** Constructor for setting up the ScrollBarLookupWidget.<br>
     * The width of the label will be determined by the label strings.<br>
     * The minimum value is set to 0 and the maximum value to the length
     * of the options array.
     * 
     * @param label label for the Widget
     * @param patch the patch, which is edited     
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender sysexSender for transmitting the value at editing the parameter
     * @param options array of label strings for each value.
     */
    public ScrollBarLookupWidget(String label, IPatch patch,
                 IParamModel pmodel, ISender sender,
                 String[] options) {
        this(label, patch, 0, options.length, -1, pmodel, sender, options);
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

	text = new JTextField(options[getValue()-super.getValueMin()], 4);
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
	text.setText(options[v-getValueMin()]);
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
	    text.setText(options[getValue()-getValueMin()]);
	}
    }
}
