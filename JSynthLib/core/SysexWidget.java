package core;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Base class of SysexWidgets. There are many kinds of extended class.<p>
 *
 * They can be used as is, and can be used by being extended.  Here is
 * an example of extended {@link ScrollBarWidget} class.
 * <pre>
 *    class MyScrollBarWidget extends ScrollBarWidget {
 *        MyScrollBarWidget(String label, IPatch patch, int min, int max, int base,
 *                          IParamModel pmodel, ISender sender) {
 *            super(lable, patch, min, max, base, pmodel, sender);
 *        }
 *
 *        protected void layoutWidgets() {
 *            setLayout(new BorderLayout());
 *            JLabel label = getJLabel();
 *            label.setHorizontalAlignment(SwingConstants.CENTER);
 *            slider.setMinimumSize(new Dimension(50, 25));
 *            slider.setMaximumSize(new Dimension(125, 25));
 *
 *            add(label, BorderLayout.NORTH);
 *            add(slider, BorderLayout.CENTER);
 *            add(text, BorderLayout.EAST);
 *        }
 *    }
 * </pre>
 * @version $Id$
 */
public abstract class SysexWidget extends JPanel {
    /** The minimum value. Used by <code>setValue</code> */
    private int valueMin;
    /** The maximum value. Used by <code>setValue</code> */
    private int valueMax;
    /** The current value. */
    // This variable is used as cache.  Can be removed by using
    // paramModel.get() and paramModel.set() every time.
    private int valueCurr;

    /** Label text for the sysexWidget */
    private String label;
    /**
     * JLabel widget for the label text
     *
     * @deprecated Use 'setLabel(String)' instead of
     * 'jlabel.setText(String)'.  Use 'getLabel()' instead of
     * 'jlabel.getText()'.
     */
    private JLabel jlabel;

    /** <code>Patch</code> associated with the widget. */
    private IPatch patch;

    /** MIDI message sender. */
    private ISender sender;

    /**
     * Parameter Model for this widget.
     * 
     * @deprecated This was changed to 'private'. Driver does not have to use
     *             this as far as I see. (Hiroo)
     */
    private IParamModel paramModel;

    /**
     * New flyweight parameter model. Used with XML drivers.
     */
    private Parameter parameter = null;
    
    /**
     * slider number.<p>
     * Posivite for slider.<p>
     * Negative for button.
     */
    private int sliderNum;

    /**
     * Number of faders the widget uses.<p>
     * ex. EnvelopWidget uses multiple faders.
     */
    private int numFaders = 1;

    /** <code>Device</code> associated with the <code>patch</code>. */
    private Device device;
    /** <code>Driver</code> associated with the <code>patch</code>. */
    private IPatchDriver driver;

    protected SysexWidget(IPatch patch, Parameter param) {
        this(param.getName(), patch, param.getMin(), param.getMax(), null, null);
        parameter = param;
    }
    
    /**
     * Creates a new <code>SysexWidget</code> instance.
     *
     * @param label a label text for the sysexWidget.
     * @param patch a <code>Patch</code>, which is edited.
     * @param min mininum value.
     * @param max maxinum value.
     * @param pmodel a <code>ParamModel</code> instance.
     * @param sender ISender for transmitting the value at editing
     * the parameter.
     */
    protected SysexWidget(String label, IPatch patch, int min, int max,
			  IParamModel pmodel, ISender sender) {
        super();
	this.label = label;
	this.patch = patch;
	valueMin = min;
	valueMax = max;
	paramModel = pmodel;
	this.sender = sender;

	// If patch.deviceNum or patch.driverNum can be changed after
	// the constructor is called, the following code have to be
	// moved to methods which use them.
	if (patch != null) {
	    device = patch.getDevice();
	    driver = patch.getDriver();
	}
	if (paramModel != null)
	    _setValue(paramModel.get());
	jlabel = new JLabel(label);
    }

    /**
     * <code>min</code> is set to <code>Integer.MIN_VALUE</code> and
     * <code>max</code> is set to <code>Integer.MAX_VALUE</code>.
     */
    protected SysexWidget(String label, IPatch patch, IParamModel pmodel, ISender sender) {
	this(label, patch, Integer.MIN_VALUE, Integer.MAX_VALUE, pmodel, sender);
    }

    /**
     * @deprecated use <code>SysexWidget(l, p, null, null)</code>
     */
    protected SysexWidget(String label, IPatch patch) {
	this(label, patch, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null);
    }

    /**
     * @deprecated use <code>SysexWidget(l, null, null, null)</code>
     */
    protected SysexWidget(String label) {
	this(label, null, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null);
    }

    /*
    // now unnecessary.
    protected SysexWidget() {
	super();
	patch = null;		// verbose?
	setup();
    }

    // Comment out since not used (and may be useless).
    public SysexWidget(String l, Patch p, int min, int max,
		       ParamModel ofs) {
	this(l, p, min, max, ofs, null);
    }

    // Comment out since not used (and may be useless).  Note that the
    // current implementation of setValue(Patch p) and sendSysex()
    // requires ParamModel.
    public SysexWidget(String l, Patch p, int min, int max,
		       ISender s) {
	this(l, p, min, max, null, s);
    }

    public SysexWidget(String l, Patch p, int min, int max) {
	this(l, p, min, max, null, null);
    }
    */

    /** create Widgets. */
    abstract protected void createWidgets();
    /**
     * layout Widgets. By overriding this method, a patch editor can
     * change the layout as he wants.
     */
    abstract protected void layoutWidgets();

    /** Enable/disable the widget. */
    public abstract void setEnabled(boolean e);

    /** Return the current value. */
    public int getValue() {
        // Don't cache with new model, so listeners will work
        if (parameter == null) {
            return valueCurr;
        } else {
            return parameter.get(patch);
        }
    }

    /** Set value. This does not update widget state. */
    private void _setValue(int v) {
	if (v < valueMin)
	    v = valueMin;
	if (v > valueMax)
	    v = valueMax;
	if (parameter == null) {
	    valueCurr = v;
	} else {
	    parameter.set(patch, v);
	}
    }

    /**
     * Set value, and update widget state.  An extended class has to
     * override this method to let update the widget state (i.e. move
     * a slider to the value set, etc.).
     */
    protected void setValue(int v) {
	_setValue(v);
    }

    /**
     * Set value by using <code>paramModel.get</code>, and update
     * the widget state.
     */
    public void setValue() {
	if (parameter == null && paramModel != null)
	    setValue(paramModel.get());
    }

    /**
     * Set value and send MIDI messages for the value to the MIDI port of
     * <code>driver</code>. An extended class calls this when widget state is
     * chagned.
     * <p>
     * This method does not update the widget state.
     */
    protected void sendSysex(int value) {
        _setValue(value);
        if (parameter == null) {
            // Don't use 'value' instead of 'valueCurr'.
            if (paramModel != null)
                paramModel.set(valueCurr);
            if (sender != null) {
                // do it only if there is a sysex-sender available
                sender.send(driver, value);
            }
        } else {
            driver.sendParameter(patch, parameter);
        }
    }

    /**
     * Set value and send MIDI messages for the value to the MIDI port of
     * <code>driver</code>. An extended class calls this when widget state is
     * chagned.
     * <p>
     * This method does not update the widget state nor do min/max range check.
     * It is caller's responsibility to do that.
     * XXX: Should be private once EnvelopeWidget is updated.
     */
    // for ExnvelopeWidget
    protected void sendSysex(ISender sender, int value) {
        if (sender != null) { // do it only if there is a sysex-sender available
            sender.send(driver, value);
        }
    }

    /** Return min value. */
    protected int getValueMin() {
	return valueMin;
    }

    /** Return max value. */
    protected int getValueMax() {
	return valueMax;
    }

    public void setMin(int min) {
        valueMin = min;
        if (getValue() < min)
            setValue(min);
    }
    public void setMax(int max) {
        valueMax = max;
        if (getValue() > max)
            setValue(max);
    }
    
    /** Return <code>Patch</code> value. */
    protected IPatch getPatch() {
	return patch;
    }

    /** Return <code>driver</code> value. */
    protected IPatchDriver getDriver() {
	return driver;
    }

    /** Getter of label. */
    public String getLabel() {
	return label;
    }

    /** Set label string. */
    public void setLabel(String l) {
	label = l;
	jlabel.setText(l);
    }

    /**
     * Setter of label.  This does not change the text in
     * <code>jlabel</code> widget.
     */
    protected void _setLabel(String l) {
	label = l;
    }

    /** Getter of jlabel. */
    protected JLabel getJLabel() {
	return jlabel;
    }

    /** Setter of jlabel. */
    protected void setJLabel(JLabel l) {
	jlabel = l;
    }

    /* Getter of <code>paramModel</code>.
    public ParamModel getModel() {
	return paramModel;
    }
    */
    /**
     * Set a fader slider number. Called by PatchEditorFrame.addWidget.<p>
     *
     * For a slider <code>num</core> is positive.<p>
     * For a button <code>num</core> is negative.<p>
     *
     * The tool-tip text is also set.<p>
     *
     * bank number : 1, 2,..., 8<p>
     * slider/button  number : 1, 2,..., 16
     *
     * @param num ((bank number) - 1) * 16 + ((slider/button number) - 1)
     * @see PatchEditorFrame
     */
    // Used to extend PatchEdit.addWidget() method.  See
    // YamahaMotifNormalVoiceEditor.java.
    public void setSliderNum(int num) {
        _setSliderNum(num);
        if (num > 0)
	    setToolTipText("Bank " + ((num - 1) / 16)
			   + "  Slider " + (((num - 1) % 16) + 1));
        if (num < 0) {
	    num = 0 - num;
	    setToolTipText("Bank " + ((num - 1) / 16)
			   + "  Button " + (((num - 1) % 16) + 1));
        }
    }

    /** Setter of fader slider number. */
    protected void _setSliderNum(int num) {
	sliderNum = num;
    }

    /** Getter of fader slider number. */
    protected int getSliderNum() {
	return sliderNum;
    }

    // numFader is only used by EnvelopWidget.  The following 3
    // mothods can be moved the EnvelopWidget class.
    /** Get number of faders. */
    protected int getNumFaders() {
	return numFaders;
    }

    /** Set number of faders. */
    protected void setNumFaders(int v) {
	numFaders = v;
    }

    /**
     * Set the value specified by <code>fader</code> and send System
     * Exclusive message to a MIDI port.<p>
     * Called by PatchEditorFrame.faderMoved(byte, byte).
     * This method is used and must be extended by a SysexWidget with
     * multiple prameters (i.e. numFaders != 1, only EnvelopeWidget now).
     *
     * @param fader fader number.
     * @param value value to be set. [0-127]
     */
    protected void setFaderValue(int fader, int value) {
    }

    /**
     * Return Insets(0,0,0,0).
     *
     * @return an <code>Insets</code> value
     */
    // What's this?  This Overrides javax.swing.JComponent.getInsets.
    // Used by Envelopwidget only.
    public Insets getInsets() {
	return new Insets(0, 0, 0, 0);
    }

    /**
     * Interface for Paramer Model.
     * 
     * Paramter Model keeps track of the changes to the patch so that when we
     * next call up this patch the changes are there. This is also used to set
     * the widgets to the correct values for a particular patch when the Single
     * Editor is opened.
     *
     * @see SysexWidget
     * @see ParamModel
     */
    public interface IParamModel {
        /** Set a parameter value <code>value</code>. */
        void set(int value);

        /** Get a parameter value. */
        int get();
    }

    /**
     * Interface for Sender.
     * 
     * Sender sends MidiMessage[s] by using <code>send(IPatchDriver, int)</code>
     * method. Every time a widget moves, its Sender gets told. The MidiMessage
     * will be sent to the synth informing it of the change. Usually a Single
     * Editor will have one or more Sender. Sometimes more than one is used
     * because a synth uses more than one method to transfer the data.
     */
    public interface ISender {
        /**
         * Send MIDI message[s] for <code>value</code>.
         *
         * @param value an <code>int</code> value
         */
        void send(IPatchDriver driver, int value);
    }
}
