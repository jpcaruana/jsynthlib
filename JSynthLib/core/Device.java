package core;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Device class defines some informations for your synthsizer, such as
 * a name of manufacturer, the model name, author(s) of drivers, etc.
 * It also manages a list of Driver classes which provides actual
 * functions.<p>
 *
 * Subclass must define two constructor, with no argument and with one
 * argument, <code>Preferences</code>.  Here is an example;
 * <pre>
 *    import java.util.prefs.Preferences;
 *    ...
 *    // Constructor for DeviceListWriter.
 *    public KawaiK4Device() {
 *	super("Kawai", "K4/K4R", "F07E..0602400000040000000000f7",
 *	      INFO_TEXT, "Brian Klock & Gerrit Gehnen");
 *    }
 *
 *    // Constructor for for actual work.
 *    public KawaiK4Device(Preferences prefs) {
 *	this();
 *	this.prefs = prefs;
 *
 *      addDriver(new KawaiK4BulkConverter());
 *      ...
 *    }
 * </pre>
 * Compatibility Note: The following fields are now
 * <code>private</code>. Use setter/getter method to access them.
 * <pre>
 *	manufacturerName, modelName, inquiryID, infoText, authors,
 *	synthName, channel, inPort, port
 * </pre>
 * Created on 5. Oktober 2001, 21:59
 * @author Gerrit Gehnen
 * @version $Id$
 * @see IDriver
 */
public abstract class Device /*implements Serializable, Storable*/ {

    /** Preferences node for storing configuration options. */
    protected Preferences prefs = null;

    /** The company which made the Synthesizer. */
    private final String manufacturerName;
    /**
     * The fixed name of the model supported by this driver, as stated
     * on the type plate of the engine. eg TG33/SY22
     */
    private final String modelName;
    /**
     * The response to the Universal Inquiry Message.  It is a
     * regular expression. It can be up to 16 bytes.<p>
     * Ex. <code>"F07E..0602413F01000000020000f7"</code>
     */
    private final String inquiryID;
    /**
     * Information about Device.
     * @see DeviceDetailsDialog
     */
    private final String infoText;
    /** Authors of the device driver. */
    private final String authors;
    /** The synthName is your personal naming of the device. */
    //private String synthName;
    /** The channel the user assigns to this driver. */
    //private int channel = 1;
    /** The device ID. */
    //private int deviceID = -1;

    /** The MIDI output port number. */
    //private int port = -1;
    /** set to true when initialization of MIDI output is done. */
    private boolean initPort = false;
    /** MIDI output Receiver */
    private Receiver rcvr;
    /** The MIDI input port number. */
    //private int inPort = -1;
    /** set to true when initialization of MIDI input is done. */
    private boolean initInPort = false;

    /** The List for all available drivers of this device. */
    private ArrayList driverList = new ArrayList ();

    /*
     * Creates new Device.
     * @deprecated Use Device(String, String, String, String, String).
     */
    /*
    public Device () {
    }
    */

    /**
     * Creates a new <code>Device</code> instance.
     *
     * @param manufacturerName The company which made the Synthesizer.
     * @param modelName The fixed name of the model supported by
     * this driver, as stated on the type plate of the engine. eg
     * TG33/SY22
     * @param inquiryID The response to the Universal Inquiry Message.
     * It can have wildcards (*). It can be up to 16 bytes.
     * Ex. <code>"F07E**0602413F01000000020000f7"</code>
     * @param infoText Information about Device.
     * @param authors Authors of the device driver.
     */
    public Device (String manufacturerName, String modelName,
		   String inquiryID, String infoText, String authors) {
	this.manufacturerName = manufacturerName;
	this.modelName = modelName;
	this.inquiryID = (inquiryID == null) ? "NONE" : inquiryID;
	this.infoText = (infoText == null)
	    ? "There is no information about this Device." : infoText;
	this.authors = authors;
	//this.synthName = modelName;
    }

    /** Called after Device(Preferences prefs) is called. */
    protected void setup() {
	// set default MIDI in/out port
	setInPort(prefs.getInt("inPort", AppConfig.getInitPortIn()));
	setPort(prefs.getInt("port", AppConfig.getInitPortOut()));
    }

    public final Preferences getPreferences() {
	return prefs;
    }

    /**
     * Create a configration panel.  Override this if your device
     * supports a configration panel.
     */
    protected JPanel config() {
	JPanel panel = new JPanel();
	panel.add(new JLabel("This Device has no configuration options."));
	return panel;
    }

    /**
     * Getter for property getManufacturerName.
     * @return Value of property getManufacturerName.
     */
    public final String getManufacturerName () {
        return manufacturerName;
    }

    /**
     * Getter for property modelName.
     * @return Value of property modelName.
     */
    public final String getModelName () {
        return modelName;
    }

    /**
     * Getter for property inquiryID.
     * @return Value of property inquiryID.
     */
    public final String getInquiryID() {
	return inquiryID;
    }

    /**
     * Getter for property infoText.
     * @return Value of property infoText.
     */
    public final String getInfoText() {
	return infoText;
    }

    /**
     * Getter for property authors.
     * @return Value of property authors.
     */
    public final String getAuthors() {
	return authors;
    }

    /**
     * Getter for property synthName.
     * @return Value of property synthName.
     */
    public /*final*/ String getSynthName () { // overridden by XMLDevice
	return prefs.get("synthName", modelName);
    }

    /**
     * Setter for property synthName.  The synthName is your personal
     * naming of the device.  A user can change it in the first column
     * of the Synth-Configuration dialog.  modelName is used as
     * default value. A synth driver should not use this.
     * @param synthName New value of property synthName.
     */
    protected final void setSynthName (String synthName) {
	prefs.put("synthName", synthName);
    }

    /**
     * Getter for property channel.
     * @return Value of property channel.
     */
    public final int getChannel () {
	return prefs.getInt("channel", 1);
    }

    /**
     * Setter for property channel which is used for playPatch, etc.
     * The value must be 1 or greater than 1, and 16 or less than 16.
     * A synth driver may use this method to set default value.<p>
     * Some old drivers use this for device ID.  Use setDeviceID
     * method to set device ID.
     * @param channel The value must be 1 or greater than 1, and 16 or
     * less than 16.
     */
    protected final void setChannel (int channel) {
	prefs.putInt("channel", channel);
    }

    /**
     * Getter for property deviceID.
     * @return Value of property deviceID.
     */
    public final int getDeviceID() {
	// For backward compatibility if this has the initial value
	// (-1), The value of <code>channel</code> is used as device
	// ID.
	int deviceID = prefs.getInt("deviceID", -1);
        return deviceID == -1 ? getChannel() : deviceID;
    }

    /**
     * Setter for property deviceID.  The value must be 1 or greater
     * than 1, and 256 or less than 256.  A synth driver may use this
     * to set default device ID.<p>
     * For backward compatibility if this has the initial value (-1),
     * The value of <code>channel</code> is used as device ID.
     * @param deviceID The value must be 1 or greater than 1, and 256
     * or less than 256.
     */
    protected final void setDeviceID(int deviceID) {
	prefs.putInt("deviceID", deviceID);
    }

    /**
     * Getter for property port (MIDI output port).
     * @return Value of property port.
     */
    public final int getPort () {
	return prefs.getInt("port", AppConfig.getInitPortOut());
    }

    /**
     * Setter for property port, the MIDI output port number, where
     * the cable <B>to</B> the device is connected.  A synth driver
     * should not use this.
     * @param port New value of property port.
     */
    protected final void setPort (int port) {
	if (!MidiUtil.isOutputAvailable())
	    return;

	if (!initPort || getPort() != port) {
	    // don't close
	    //if (rcvr != null) rcvr.close();
	    try {
		rcvr = MidiUtil.getReceiver(port);
	    } catch (MidiUnavailableException e) {
		ErrorMsg.reportStatus(e);
	    }
	}
	prefs.putInt("port", port);
	initPort = true;
    }

    /** send MidiMessage to MIDI output. Called by Driver.send(). */
    public final void send(MidiMessage message) {
	try {
	    MidiUtil.send(rcvr, message);
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /**
     * Getter for property inPort.
     * @return Value of property inPort.
     */
    public final int getInPort () {
	return prefs.getInt("inPort", AppConfig.getInitPortIn());
    }

    /**
     * Setter for property inPort, the MIDI input port number, where
     * the cable <B>to</B> the device is connected.  A synth driver
     * should not use this.
     * @param inPort New value of property inPort.
     */
    protected final void setInPort (int inPort) {
	if (!MidiUtil.isInputAvailable())
	    return;

	if (!initInPort || getInPort() != inPort)
	    MidiUtil.setSysexInputQueue(inPort);
	prefs.putInt("inPort", inPort);
	initInPort = true;
    }

    // Getters/Setters, etc for Drivers
    /**
     * Add Driver.  Usually a constructor of a subclass of
     * <code>Device</code> calls this.  Bulk converters must be added
     * before simple drivers!
     * @param driver IDriver to be added.
     * @see IConverter
     */
    public final void addDriver(IDriver driver) {
        driver.setDevice(this);
        driverList.add(driver);
    }

    /**
     * Add Driver at the <code>index</code>.
     * @param index The index, where the driver is added in the list.
     * Bulk converters must be added before simple drivers!
     * @param driver Driver to be added.
     * @deprecated Call <code>addDriver(Driver)</code> in order.
     */
    protected final void addDriver(int index, IDriver driver) {
	driver.setDevice(this);
        driverList.add(index, driver);
    }

    /** Size query for driverList. */
    final int driverCount() {
	return this.driverList.size();
    }

    /** Indexed getter for driverList elements. */
    protected final IDriver getDriver(int i) {
	return (IDriver) this.driverList.get(i);
    }

    /** Returns the index of a Driver */
    final int getDriverNum(IDriver drv) {
 	return driverList.indexOf(drv);
    }

    /** Remover for driverList elements. */
    final IDriver removeDriver(int i) {
	return (IDriver) this.driverList.remove(i);
    }

    /** getter for device number. */
    final int getDeviceNum() {
	return AppConfig.getDeviceIndex(this);
    }

    /**
     * Getter for DeviceName.
     * @return String of Device Name with inPort and Channel.
     */
    public String getDeviceName() {
	return getManufacturerName() + " " + getModelName()
	    + " <" + getSynthName() + ">  -  MIDI Out Port: "
	    + MidiUtil.getOutputMidiDeviceInfo(getPort()).getName()
	    + "  -  MIDI Channel: " + getChannel();
    }

    /**
     * Same as <code>getDeviceName()</code>.
     * See #getDeviceName
     */
    public String toString() {
	return getDeviceName();
    }

    /**
     * Show a dialog for the details of the device.
     */
    public void showDetails() {
        DeviceDetailsDialog ddd = new DeviceDetailsDialog(this);
	ddd.show();
    }

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     * @param patchString A sysex string like
     * <code>"F07E010602413F01000000020000f7"</code>.
     * @return true if the patchString matches the ID of the device
     */
    // commented out since not used.  If you need, change the argument
    // patchString String instead of StringBuffer.
//     public /*static*/ boolean checkInquiry (StringBuffer patchString) {
//         StringBuffer inquiryString = new StringBuffer (inquiryID);
// 	if (inquiryString.length () > patchString.length())
// 	    return false;
// 	for (int j = 0; j < inquiryString.length(); j++)
//             if (inquiryString.charAt(j) == '*')
// 		inquiryString.setCharAt(j, patchString.charAt(j));
//         return (inquiryString.toString().equalsIgnoreCase(patchString.toString().substring(0, inquiryString.length())));
//     }
}
