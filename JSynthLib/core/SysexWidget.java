package core;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Insets;

/**
 * Base class of SysexWidgets.
 *
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
     * @deprecated This will be "private".  Use 'setLabel(String)'
     * instead of 'jlabel.setText(String)'.  Use 'getLabel()' instead
     * of 'jlabel.getText()'.
     */
    public JLabel jlabel;	// accessed by some drivers

    /** <code>Patch</code> associated with the widget. */
    private Patch patch;

    /** System exclusive message sender. */
    private SysexSender sysexSender;

    /**
     * System exclusive modeler.
     * @deprecated This will be 'private'.  Driver does not have to
     * use this as far as I see. (Hiroo)
     */
    public ParamModel paramModel; // accessed by some drivers unnecessarily.

    /**
     * slider number.<p>
     * Posivite for slider.<p>
     * Negative for button.
     */
    private int sliderNum;

    /**
     * Number of faders the widget use.<p>
     * ex. Envelop Widget uses multiple faders.
     */
    private int numFaders = 1;

    /** <code>Device</code> associated with the <code>patch</code>. */
    private Device device;
    /** <code>Driver</code> associated with the <code>patch</code>. */
    private Driver driver;

    /**
     * Creates a new <code>SysexWidget</code> instance.
     *
     * @param l a label text for the sysexWidget.
     * @param p a <code>Patch</code>, which is edited.
     * @param min mininum value.
     * @param max maxinum value.
     * @param ofs a <code>ParamModel</code> instance.
     * @param s SysexSender for transmitting the value at editing
     * the parameter.
     */
    protected SysexWidget(String l, Patch p, int min, int max,
			  ParamModel ofs, SysexSender s) {
        super();
	label = l;
	patch = p;
	valueMin = min;
	valueMax = max;
	paramModel = ofs;
	sysexSender = s;

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

    protected SysexWidget(String l, Patch p, ParamModel ofs, SysexSender s) {
	this(l, p, Integer.MIN_VALUE, Integer.MAX_VALUE, ofs, s);
    }

    // for PatchNameWidget and EnvelopWidget
    protected SysexWidget(String l, Patch p) {
	this(l, p, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null);
    }

    // for LabelWidget
    protected SysexWidget(String l) {
	this(l, null, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null);
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
		       SysexSender s) {
	this(l, p, min, max, null, s);
    }

    public SysexWidget(String l, Patch p, int min, int max) {
	this(l, p, min, max, null, null);
    }
    */

    /** Return the current value. */
    public int getValue() {
	return valueCurr;
    }

    /** Set value. This does not update widget state. */
    private void _setValue(int v) {
	if (v < valueMin)
	    v = valueMin;
	if (v > valueMax)
	    v = valueMax;
	valueCurr = v;
    }

    /** Set value, and update widget state. Extended class have to
	override this if necessary. */
    protected void setValue(int v) {
	_setValue(v);
    }

    /** Set value by using <code>paramModel.get</code>, and update
	the widget state. */
    public void setValue() {
	if (paramModel != null)
	    setValue(paramModel.get());
    }

    /** @deprecated Use no arg <code>setValue()</code>. */
    public void setValue(Patch p) { // 'p' is not used!!!
	setValue();
    }

    /**
     * Set value and send System Exclusive message of the value to a
     * MIDI port.  An extended class calls this when widget state is
     * chagned.  This method does not update the widget state.
     */
    protected void sendSysex(int v) {
	_setValue(v);
	// Don't use 'v' instead of 'valueCurr'.
	if (paramModel != null)
	    paramModel.set(valueCurr);
	sendSysex(sysexSender, valueCurr);
    }

    /**
     * Send System Exclusive message of the value to a MIDI port using
     * SysexSender <code>s</code>.  An extended class calls this when
     * widget state is chagned.<p>
     * This method does not do min/max range check.  It is caller's
     * responsibility.
     */
    // for ExnvelopeWidget
    protected void sendSysex(SysexSender s, int v) {
        if (s != null) {
	    // do it only if there is a sysex-sender available
            try {
		s.channel = (byte) device.getChannel();
                PatchEdit.MidiOut.writeLongMessage(device.getPort(),
						   s.generate(v));
            } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	    }
        }
    }

    /**
     * Set value from fader and send System Exclusive message to a
     * MIDI port.<p>
     * Called by PatchEditorFrame.faderMoved(byte, byte).
     * This method is used and must be extended by a SysexWidget with
     * multiple prameters. (i.e. numFaders != 1, only EnvelopeWidget now)
     *
     * @param fader fader number.
     * @param value value to be set. [0-127]
     */
    protected void setFaderValue(int fader, int value) {
    }

    /** Return min value. */
    protected int getValueMin() {
	return valueMin;
    }

    /** Return max value. */
    protected int getValueMax() {
	return valueMax;
    }

    /** Change mix/max value. */
    protected void setMinMax(int min, int max) {
	valueMin = min;
	valueMax = max;
        if (valueCurr > max)
	    valueCurr = max;
        if (valueCurr < min)
	    valueCurr = min;
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

    /** Setter of label.  This does not change jlabel widget. */
    protected void _setLabel(String l) {
	label = l;
    }

    /** Getter of jlabel.<p> */
    protected JLabel getJLabel() {
	return jlabel;
    }

    /** Set label string. */
    protected void setJLabel(JLabel l) {
	jlabel = l;
    }

    /** Getter of Patch name. */
    public String getPatchName() {
	if (driver == null)
	    return "Patch Name";
	return driver.getPatchName(patch);
    }

    /** Setter of Patch name. */
    protected void setPatchName(String s) {
	if (driver != null)
	    driver.setPatchName(patch, s);
    }

    /** Getter of Patch name size. */
    public int getPatchNameSize() {
	if (driver == null)
	    return 0;
	return driver.patchNameSize;
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
     */
    public void setSliderNum(int num) {	// called by YamahaMotifNormalVoiceEditor.java.  Why?
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

    /** Setter of sliderNum. */
    protected void _setSliderNum(int num) {
	sliderNum = num;
    }

    /** Getter of sliderNum. */
    protected int getSliderNum() {
	return sliderNum;
    }

    /** Getter of numFaders. */
    protected int getNumFaders() {
	return numFaders;
    }

    /** Setter of numFaders. */
    protected void setNumFaders(int v) {
	numFaders = v;
    }

    /** Enable/disable the widget. */
    public abstract void setEnabled(boolean e);

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
}
