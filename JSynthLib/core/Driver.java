package core;
import java.io.*;
import javax.swing.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * This is the base class for all Drivers.
 * @author Brian Klock
 * @version $Id$
 */
public class Driver extends Object implements Serializable, Storable {
    /** Which device does this driver go with? */
    // can be private
    protected Device device;
    /** Which deviceNum does the device of this driver goes with? */
    private int deviceNum;
    /** Which driverNum does the device of this driver goes with? */
    private int driverNum;

    /** The patch type. eg. "Single", "Bank", "Drumkit", etc. */
    // can be private final
    protected String patchType;
    /** The names of the authors of this driver. */
    // can be private final
    protected String authors;

    /*
     * The following fields are used by default methods defined in
     * this file.  If your extending driver can use a method, set the
     * corresponding fields.  Otherwise override the method.
     */
    // for default set/getPatchName methods
    /**
     * The offset in the patch where the patchname starts. '0' if
     * patch is not named -- remember all offsets are zero based.
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameStart;
    /**
     * Number of characters in the patch name. (0 if no name)
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameSize;

    // for default calculateCheckSum(Patch) method
    /**
     * Offset of checksum byte.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateCheckSum(Patch)
     */
    protected int checksumOffset;
    /**
     * Start of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateCheckSum(Patch)
     */
    protected int checksumStart;
    /**
     * End of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateCheckSum(Patch)
     */
    protected int checksumEnd;

    // for default trimSysex method
    /**
     * The size of the patch for trimming purposes.
     * @see #trimSysex
     */
    protected int trimSize = 0;

    // for default choosePatch methods
    /**
     * Array holding names/numbers/names for all banks.
     * @see #choosePatch(Patch, int, int)
     * @see #choosePatch()
     */
    protected String[] bankNumbers;
    /**
     * Array holding names/numbers for all patches.
     * @see #choosePatch(Patch, int, int)
     * @see #choosePatch()
     */
    protected String[] patchNumbers;

    // for default supportsPatch method
    /**
     * The size of the patch this Driver supports (or 0 for variable).
     * @see #supportsPatch
     */
    protected int patchSize;
    /**
     * The hex header that sysex files of the format this driver
     * supports will have. It can have wildcards (<code>*</code>). The
     * program will attempt to match loaded sysex drivers with the
     * sysexID of a loaded driver. It can be up to 16 bytes.
     * (ex. <code>"F041**003F12"</code>)
     * @see #supportsPatch
     */
    protected String sysexID;

    // for default sendPatchWorker method
    /**
     * Offset of channel (deviceID) in sysex.
     * @see sendPatchWorker
     */
    protected int deviceIDoffset;  //location of device id

    /**
     * SysexHandler object to request dump.
     * @see #requestPatchDump
     * @see SysexHandler
     */				// - phil@muqus.com
    protected SysexHandler sysexRequestDump = null;

    /** Number of sysex messages in patch dump.  Not used now. */
    protected int numSysexMsgs;

    /** The channel (device ID) the user assigns to this driver. */
    // This is used for both 'channel' for Channel Meesages and
    // 'Device ID' for System Exclusive Messages.  !!!FIXIT!!! but how?
    protected int channel = 1;

    /*
     * The following fields are obsoleted when the Device class was
     * introduced.  Use getter functions to access them.
     */
    /** The MIDI Out port the user assigns to this driver. */
    protected int port;
    /** The MIDI In port the user assigns to this driver. */  // phil@muqus.com
    protected int inPort;                                     // phil@muqus.com
    /**The company which made the Synthesizer
     * @deprecated Don't use this. */
    protected String manufacturer;
    /**The models supported by this driver eg TG33/SY22
     * @deprecated Don't use this. */
    protected String model;
    /**The response to the Universal Inquiry Message.
     * It can have wildcards (*). It can be up
     * to 16 bytes
     * @deprecated Don't use this. */
    // ADDED BY GERRIT GEHNEN
    protected String inquiryID;
    /** A Shorthand alias for the Synth this driver supports (eg TG33,K5k)
     * @deprecated Don't use this. */
    protected String id;

    /**
     * Creates a new <code>Driver</code> instance.
     *
     * @param patchType The patch type. eg. "Single", "Bank",
     * "Drumkit", etc.
     * @param authors The names of the authors of this driver.
     */
    public Driver(String patchType, String authors) {
	this.patchType = patchType;
	this.authors = authors;
    }

    /**
     * Constructs a generic Driver.
     */
    public Driver() {
        sysexID = "MATCHNONE";
	inquiryID = "NONE";
        authors = "Brian Klock";
	manufacturer = "Generic";
	model = "";
        patchType = "Sysex";
	id = "???";
        patchNameSize = 0;
        patchNumbers = new String[128];
        for (int i = 0; i < 128; i++)
	    patchNumbers[i] = String.valueOf(i);
        bankNumbers = new String[] {"0"};
    }

    //
    // Setters and Getters
    //
    /** Setter for property <code>device</code>. */
    void setDevice(Device d) {
	device = d;
    }
    /** Getter for property <code>device</code>. */
    protected Device getDevice() {
	return device;
    }
    /** Setter for property <code>deviceNum</code>. */
    public void setDeviceNum(int deviceNum) { // for storable interface
	this.deviceNum = deviceNum;
    }
    /** Getter for property <code>deviceNum</code>. */
    public int getDeviceNum() {	// for storable interface
	return this.deviceNum;
    }
    /** Setter for property <code>driverNum</code>. */
    public void setDriverNum(int driverNum) { // for storable interface
	this.driverNum = driverNum;
    }
    /** Getter for property <code>driverNum</code>. */
    public int getDriverNum() {	// for storable interface
	return this.driverNum;
    }
    /** Getter for property <code>patchType</code>. */
    protected String getPatchType() {
	return patchType;
    }
    /** Getter for property <code>getAuthors</code>. */
    protected String getAuthors() {
	return authors;
    }
    /** Setter for property <code>port</code>. */
    // remove when 'port' becomes 'private'.
    public void setPort(int port) { // for storable interface
        this.port = port;
	//device.setPort(port);
    }
    /** Getter for property <code>port</code>. */
    public int getPort() {	// called by bank driver
        return port;
	//return device.port;
    }
    /** Setter for property <code>inPort</code>. */
    // remove when 'inPort' becomes 'private'.
    public void setInPort(int inPort) { // for storable interface
        this.inPort = inPort;
	//device.setInPort(inPort);
    }
    /** Getter for property <code>inPort</code>. */
    public int getInPort() {	// for storable interface
        return inPort;
	//return device.inPort;
    }
    /** Getter for property <code>device.manufacturerName</code>. */
    protected String getManufacturerName() {
	return manufacturer;
	//return device.manufacturerName;
    }
    /** Getter for property <code>device.modelName</code>. */
    protected String getModelName() {
	return model;
	//return device.modelName;
    }
    /** Getter for property <code>device.synthName</code>. */
    protected String getSynthName() {
	return id;
	//return device.synthName;
    }
    /** Getter for property <code>device.channel</code>. */
    public int getChannel() {	// called by bank driver
	return channel;
        //return device.channel;
    }
    /** Setter for property <code>device.channel</code>. */
    public void setChannel(int channel) { // called by Device and for storable interface
        this.channel = channel;
    }
//     protected void setSynthName(String s) {
//  	id = s;
//     }

    /**
     * Gets the name of the patch from the sysex. If the patch uses
     * some weird format or encoding, this needs to be overidden in
     * the particular driver.
     */
    public String getPatchName(Patch p) { // called by bank driver
        if (patchNameSize == 0)
	    return ("-");
        try {
	    StringBuffer s = new StringBuffer(new String(p.sysex, patchNameStart,
							 patchNameSize, "US-ASCII"));
	    return s.toString();
	    // Why don't we simply do the following? Hiroo
// 	    return new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII");
	} catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

    /**
     * Set the name of the patch in the sysex. If the patch uses some
     * weird format or encoding, this needs to be overidden in the
     * particular driver.
     */
    public void setPatchName(Patch p, String name) { // called by bank driver
        if (patchNameSize == 0) {
	    ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Name Editing.");
	    return;
	}
        if (name.length() < patchNameSize)
	    name = name + "            ";
        byte[] namebytes = new byte[64];
        try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		p.sysex[patchNameStart + i] = namebytes[i];
	} catch (UnsupportedEncodingException ex) {
	    return;
	}
        calculateChecksum(p);	// Is this required here?
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.<p>
     *
     * This method is called by extended classes.
     * Override this for different checksum calculation method.
     *
     * @param p a <code>Patch</code> value
     * @param start start offset
     * @param end end offset
     * @param ofs offset of the checksum data
     */
    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
        ErrorMsg.reportStatus("Driver:calcChecksum:1st byte is " + p.sysex[start]);
        ErrorMsg.reportStatus("Last byte is " + p.sysex[end]);
        ErrorMsg.reportStatus("Checksum was " + p.sysex[ofs]);
        int sum = 0;
        for (int i = start; i <= end; i++)
            sum += p.sysex[i];
	// Here are examples of checksum caluculation
        //p.sysex[ofs] = (byte) (sum % 128);
        //p.sysex[ofs] = (byte) (p.sysex[ofs] ^ 127);
        //p.sysex[ofs] = (byte) (p.sysex[ofs] + 1);
        p.sysex[ofs] = (byte) (p.sysex[ofs] & 127);   //to ensure that checksum is in range 0-127;
        ErrorMsg.reportStatus("Checksum is now " + p.sysex[ofs]);
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.<p>
     *
     * This method is called by main program.  Need to be overridden
     * if a patch is consist from multiple SysEX messages.
     *
     * @param p a <code>Patch</code> value
     */
    public void calculateChecksum(Patch p) { // called by bank driver
	calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    /**
     * Create a new Patch.<p>
     * Need to be Overridden.
     */
    protected Patch createNewPatch() {
	return null;
    }

    /**
     * This method trims a patch, containing more than one real
     * patch to a correct size. Useful for files containg more than one
     * bank for example. Some drivers are incompatible with this method
     * so it reqires explicit activation with the trimSize variable.
     * @param p the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     */
    protected int trimSysex(Patch p) { // no driver overrides this now.
        if (trimSize > 0 && p.sysex.length > trimSize
	    && p.sysex[trimSize - 1] == (byte) 0xf7) {
	    byte[] sysex = new byte[trimSize];
	    System.arraycopy(p.sysex, 0, sysex, 0, trimSize);
	    p.sysex = sysex;
        }
	return p.sysex.length;	// == trimSize
    }

    /**
     * Returns an Editor Window for this Patch. Overwrite this to
     * invoke your Patch Editor if made it.
     */
    protected JInternalFrame editPatch(Patch p) {
	ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Editing.");
	return null;
    }

    /**
     * Prompts the user for the location to store the patch and stores
     * it.
     *
     * @param p a <code>Patch</code> value
     * @param bankNum initially selected bank number.
     * @param patchNum initially selected patch number.
     */
    void choosePatch(Patch p, int bankNum, int patchNum) {
	int bank = 0;
	int patch = 0;
	String bankstr;
	String patchstr;
	try {
	    // choose bank number
	    if (bankNumbers.length > 1) {
		bankstr = (String) JOptionPane.showInputDialog
		    (null, "Please Choose a Bank", "Storing Patch",
		     JOptionPane.QUESTION_MESSAGE, null,
		     bankNumbers, bankNumbers[bankNum]);
		if (bankstr == null) // canceled
		    return;
		for (int i = 0; i < bankNumbers.length; i++)
		    if (bankstr.equals(bankNumbers[i])) {
			bank = i;
			break;
		    }
	    }
	    // choose patch number
	    if (patchNumbers.length > 1) {
		patchstr = (String) JOptionPane.showInputDialog
		    (null, "Please Choose a Patch Location", "Storing Patch",
		     JOptionPane.QUESTION_MESSAGE, null,
		     patchNumbers, patchNumbers[patchNum]); // phil@muqus.com
		if (patchstr == null) // canceled
		    return;
		for (int i = 0; i < patchNumbers.length; i++)
		    if (patchstr.equals(patchNumbers[i])) {
			patch = i;
			break;
		    }
	    }
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
	ErrorMsg.reportStatus("Driver:ChoosePatch  Bank = " + bank
			      + "  Patch = " + patch);
	// send a Patch to selected bank & patch number
	storePatch(p, bank, patch);
    }

    /**
     * Prompts the user for the location to store the patch and stores
     * it.  Initially bank number '0' and patch number '0' are selected.
     *
     * @param p a <code>Patch</code> value
     */
    protected void choosePatch(Patch p) {
	choosePatch(p, 0, 0);
    }

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     */
    boolean supportsPatch(StringBuffer patchString, Patch p) {
// 	System.out.println("SupportsPatch:" + manufacturer + " " + model
// 			   + " " + patchType + " " + patchSize + " "
// 			   + p.sysex.length);
	// check the length of Patch
        if ((patchSize != p.sysex.length) && (patchSize != 0))
	    return false;
	// Why StringBuffer? !!!FIXIT!!!
        StringBuffer driverString = new StringBuffer(sysexID);
        StringBuffer compareString = new StringBuffer();
        if (patchString.length() < driverString.length())
	    return false;
        for (int j = 0, i = 0; j < driverString.length(); j++, i++) {
	    switch (driverString.charAt(j)) {
	    case '*':
		compareString.append(patchString.charAt(i));
		break;
	    default:
		compareString.append(driverString.charAt(j));
            }
        }
// 	System.out.println(getDriverName());
// 	System.out.println("Comp.String: " + compareString);
// 	System.out.println("DriverString:" + driverString);
// 	System.out.println("PatchString: " + patchString);
        return (compareString.toString().equalsIgnoreCase
		(patchString.toString().substring(0, driverString.length())));
    }

    //
    // MIDI methods
    //
    /** Send Program Change MIDI message. */
    protected void setPatchNum(int patchNum) {
        try {
	    PatchEdit.MidiOut.writeShortMessage
		(port,
		 (byte) (0xC0 + (channel - 1)),	// Program Change
		 (byte) patchNum); // Program Number
	} catch (Exception e) {
	}
    }

    /** Send Control Change (Bank Select) MIDI message. */
    protected void setBankNum(int bankNum) {
        try {
	    PatchEdit.MidiOut.writeShortMessage
		(port,
		 (byte) (0xB0 + (channel - 1)),	// Control Change
		 (byte) 0x00,	// Bank Select
		 (byte) (bankNum / 128)); // Bank Number (MSB)
	    PatchEdit.MidiOut.writeShortMessage
		(port,
		 (byte) (0xB0 + (channel - 1)),	// Control Change
		 (byte) 0x20,	// Bank Select (LSB)
		 (byte) (bankNum % 128)); // Bank Number (LSB)
	} catch (Exception e) {
	}
    }

    /**
     * Sends a patch to a set location on a synth.<p>
     * Override this if required.
     */
    // sendPatch(Patch) may be better name.
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);
    }

    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable.
     */
    protected void sendPatch(Patch p) {
	sendPatchWorker(p);
    }

    /** Does the actual work to send a patch to the synth. */
    // Why do we need both sendPatch(Patch) and sendPatchWorker(Patch)?
    protected void sendPatchWorker(Patch p) {
	// set channel (device ID)
        if (deviceIDoffset > 0)
	    p.sysex[deviceIDoffset] = (byte) (channel - 1);
        try {
	    PatchEdit.MidiOut.writeLongMessage(port, p.sysex);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    //----- Start phil@muqus.com
    /**
     * Request MIDI synth to send a patch dump.  If
     * <code>sysexRequestDump</code> is not <code>null</code>, a
     * request dump message is sent by calling
     * <code>sysexRequestDump.send</code> method.  Otherwise a dialog
     * window will prompt users.
     * @see SysexHandler
     */
    protected void requestPatchDump(int bankNum, int patchNum) {
	setBankNum(bankNum);
	setPatchNum(patchNum);
	if (sysexRequestDump == null) {
	    JOptionPane.showMessageDialog
		(PatchEdit.instance,
		 "The " + getDriverName()
		 + " driver does not support patch getting.\n\n"
		 + "Please start the patch dump manually...",
		 "Get Patch", JOptionPane.WARNING_MESSAGE);
	    byte[] buffer = new byte[256 * 1024];
	    try {
		while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
		    PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Error Clearing MIDI In buffer.", ex);
	    }
	} else
	    sysexRequestDump.send(port, (byte) channel,
				  new NameValue("bankNum", bankNum),
				  new NameValue("patchNum", patchNum));
    }
    //----- End phil@muqus.com

    /** Play note. */
    // the argument 'p' is not used.!!!FIXIT!!!
    public void playPatch(Patch p) { // called by core and some Editors
        try {
// 	    sendPatch(p);
	    Thread.sleep(100);
	    PatchEdit.MidiOut.writeShortMessage
		(port,
		 (byte) (0x90 + (channel - 1)),	// Note On with channel
		 (byte) PatchEdit.appConfig.getNote(),
		 (byte) PatchEdit.appConfig.getVelocity());
	    Thread.sleep(PatchEdit.appConfig.getDelay());
	    PatchEdit.MidiOut.writeShortMessage
		(port,
		 (byte) (0x80 + (channel - 1)),	// Note Off
		 (byte) PatchEdit.appConfig.getNote(),
		 (byte) 0);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    //
    // For storable interface
    //
    private String[] storedPropertyNames = {
	"deviceNum", "driverNum", "port", "inPort", "channel"
    };

    /**
     * Get the names of properties that should be stored and loaded.
     * @return a Set of field names
     */
    public Set storedProperties() { // extending a public method
	HashSet set = new HashSet();
	set.addAll(Arrays.asList(this.storedPropertyNames));
	return set;
    }

    /** Method that will be called after loading. */
    public void afterRestore() { // extending a public method
	// do nothing
    }

    //
    // For debugging.
    //
    /*
     * Returns String .. full name for referring to this patch for
     * debugging purposes
     */
    protected String getFullPatchName(Patch p) {
	return getManufacturerName() + " | " + getModelName() + " | "
	    + getPatchType() + " | " + getSynthName() + " | " + getPatchName(p);
    }

    /*
     * Returns String .. full name for referring to this Driver for
     * debugging purposes
     */
    protected String getDriverName() {
	return getManufacturerName() + " " + getModelName() + " "
	    + getPatchType() + " ";
    }
}
