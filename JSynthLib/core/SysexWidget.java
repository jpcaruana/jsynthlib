package core;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

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
     * @deprecated Use 'setLabel(String)' instead of
     * 'jlabel.setText(String)'.  Use 'getLabel()' instead of
     * 'jlabel.getText()'.
     */
    private JLabel jlabel;

    /** <code>Patch</code> associated with the widget. */
    private Patch patch;

    /** System exclusive message sender. */
    private SysexSender sysexSender;

    /**
     * System exclusive modeler.
     * @deprecated This will be 'private'.  Driver does not have to
     * use this as far as I see. (Hiroo)
     */
    private ParamModel paramModel; // accessed by some drivers unnecessarily.

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

    /**
     * <code>min</code> is set to <code>Integer.MIN_VALUE</code> and
     * <code>max</code> is set to <code>Integer.MAX_VALUE</code>.
     */
    protected SysexWidget(String l, Patch p, ParamModel ofs, SysexSender s) {
	this(l, p, Integer.MIN_VALUE, Integer.MAX_VALUE, ofs, s);
    }

    /**
     * @deprecated use <code>SysexWidget(l, p, null, null)</code>
     */
    protected SysexWidget(String l, Patch p) {
	this(l, p, Integer.MIN_VALUE, Integer.MAX_VALUE, null, null);
    }

    /**
     * @deprecated use <code>SysexWidget(l, null, null, null)</code>
     */
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
		s.channel = (byte) device.getDeviceID();
		if (PatchEdit.newMidiAPI)
		    driver.send(s.generate(v));
		else
		    PatchEdit.MidiOut.writeLongMessage(device.getPort(),
						       s.generate(v));
            } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	    }
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

    /** Change mix/max value. */
    protected void setMinMax(int min, int max) {
	valueMin = min;
	valueMax = max;
        if (valueCurr > max)
	    valueCurr = max;
        if (valueCurr < min)
	    valueCurr = min;
    }

    /** Return <code>Patch</code> value. */
    protected Patch getPatch() {
	return patch;
    }

    /** Return <code>driver</code> value. */
    protected Driver getDriver() {
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
}
