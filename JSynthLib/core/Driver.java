package core;

import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.InvalidMidiDataException;


/**
 * This is an implementation of ISingleDriver and the base class for single
 * drivers which use <code>Patch<IPatch>.<p>
 *
 * Compatibility Note: The following fields are now
 * <code>private</code>.  Use setter/getter method to access them.
 * <pre>
 *   	device, patchType, authors
 * </pre>
 * Compatibility Note: The following fields are now obsoleted.  Use a
 * getter method to access them.  The getter method queries parent
 * Device object.
 * <pre>
 *   	deviceNum, driverNum,
 *   	channel, port, inPort, manufacturer, model, inquiryID, id
 * </pre>
 * Compatibility Note:
 *	SysexHandler.send(getPort(), sysex);
 * or
 *	PatchEdit.MidiOut.writeLongMessage(getPort(), sysex);
 * was replaced by
 *	send(sysex);
 *
 * @author Brian Klock
 * @version $Id$
 * @see Patch
 */
abstract public class Driver implements ISingleDriver {
    /**
     * Which device does this driver go with?
     */
    private Device device;

    /**
     * The patch type. eg. "Single", "Bank", "Drumkit", etc.
     */
    private final String patchType;

    /**
     * The names of the authors of this driver.
     */
    private final String authors;

    /**
     * Array holding names/numbers for all patches.  Used for comboBox
     * selection.
     * @see #getPatchNumbers
     * @see #getPatchNumbersForStore
     * @see #generateNumbers
     */
    protected String[] patchNumbers;
    /**
     * Array holding names or numbers for all banks.  Used for
     * comboBox selection.
     * @see #getBankNumbers
     * @see #generateNumbers
     */
    protected String[] bankNumbers;

    /*
     * The following fields are used by default methods defined in
     * this file.  If your extending driver can use a default method
     * as is, set the corresponding fields.  Otherwise override the
     * method.
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
     * @see #calculateChecksum(Patch)
     */
    protected int checksumOffset;
    /**
     * Start of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumStart;
    /**
     * End of range that Checksum covers.<p>
     * Need to be set if default <code>calculateChecksum(Patch)</code>
     * method is used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumEnd;

    // for default trimSysex method
    /**
     * The size of the patch for trimming purposes.
     * @see #trimSysex
     */
    protected int trimSize = 0;

    // for default supportsPatch method
    /**
     * The size of the patch this Driver supports (or 0 for variable).
     * @see #supportsPatch
     */
    protected int patchSize;
    /**
     * The hex header that sysex files of the format this driver
     * supports will have.  The program will attempt to match loaded
     * sysex drivers with the sysexID of a loaded driver.  It can be
     * up to 16 bytes and have wildcards (<code>*</code>).
     * (ex. <code>"F041**003F12"</code>)
     * @see #supportsPatch
     */
    protected String sysexID;

    // for sendPatchWorker method
    /**
     * Offset of deviceID in sysex. Used by
     * <code>sendPatchWorker</code> method.
     * @see #sendPatchWorker
     */
    protected int deviceIDoffset;  // array index of device ID

    /**
     * SysexHandler object to request dump.  You don't have to use
     * this field if you override <code>requestPatchDump</code>
     * method.
     * @see #requestPatchDump
     * @see SysexHandler
     */				// - phil@muqus.com
    protected SysexHandler sysexRequestDump = null;

    /** Number of sysex messages in patch dump.  Not used now. */
    protected int numSysexMsgs;

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

    /*
     * Constructs a generic Driver.
     * @deprecated Use Driver(String, String).
     */
    /*
    public Driver() {
    }
    */

    // IDriver interface methods
    public final String getPatchType() {
	return patchType;
    }

    public final String getAuthors() {
	return authors;
    }

    public final void setDevice(Device d) {
	device = d;
    }
    public final Device getDevice() {
	return device;
    }

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     *
     * @param patchString the result of <code>p.getPatchHeader()</code>.
     * @param sysex a byte array of sysex message
     * @return <code>true</code> if this driver supports the Patch.
     * @see #patchSize
     * @see #sysexID
     */
    public boolean supportsPatch(String patchString, byte[] sysex) {
  	// check the length of Patch
        if ((patchSize != sysex.length) && (patchSize != 0))
	    return false;

        if (sysexID == null || patchString.length() < sysexID.length())
	    return false;

        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
	    switch (sysexID.charAt(i)) {
	    case '*':
		compareString.append(patchString.charAt(i));
		break;
	    default:
		compareString.append(sysexID.charAt(i));
            }
        }
// 	ErrorMsg.reportStatus(toString());
// 	ErrorMsg.reportStatus("Comp.String: " + compareString);
// 	ErrorMsg.reportStatus("DriverString:" + driverString);
// 	ErrorMsg.reportStatus("PatchString: " + patchString);
        return (compareString.toString().equalsIgnoreCase
		(patchString.substring(0, sysexID.length())));
    }

    public IPatch[] createPatch(byte[] sysex) {
        return new IPatch[] {new Patch(sysex, this)};
    }
    // end of IDriver interface methods

    // IPatchDriver interface methods
    public int getPatchSize() {
	return patchSize;
    }

    public String[] getPatchNumbers() {
        return patchNumbers;
    }

    public String[] getPatchNumbersForStore() {
        // All patches assumed to be writable by default
        return patchNumbers;
    }

    public String[] getBankNumbers() {
        return bankNumbers;
    }

    public boolean canCreatePatch() {
        try {
            getClass().getDeclaredMethod("createNewPatch", null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public final IPatch createPatch() {
	return (IPatch) createNewPatch();
    }

    /**
     * Create a new Patch. Don't override this unless your driver properly
     * implement this method.
     * @see IPatchDriver#createNewPatch
     */
    protected Patch createNewPatch() { // overridden by subclass
	return null;
    }

    public IPatch[] createPatch(SysexMessage[] msgs) {
        // convert to one byte array.
        int sysexSize = 0;
        for (int i = 0; i < msgs.length; i++)
            sysexSize += msgs[i].getLength();
        byte[] patchSysex = new byte[sysexSize];
        for (int size, ofst = 0, i = 0; i < msgs.length; ofst += size, i++) {
            size = msgs[i].getLength();
            byte[] d = msgs[i].getMessage();
            System.arraycopy(d, 0, patchSysex, ofst, size);
        }

        // if Conveter for the patch exist, use it.
        IDriver drv = DriverUtil.chooseDriver(patchSysex, getDevice());
        IPatch[] patarray = drv.createPatch(patchSysex);

        // Maybe you don't get the expected patch!
        // Check all devices/drivers again! Call fixpatch() if supportsPatch
        // returns false.
        // XXX Why don't we simply cause error? Hiroo
        for (int k = 0; k < patarray.length; k++) {
            IPatch pk = patarray[k];
            String patchString = pk.getPatchHeader();
            if (!(pk.getDriver().supportsPatch(patchString, pk.getByteArray()))) {
                patarray[k] = fixPatch((Patch) pk, patchString);
            }
        }
        return patarray;
    }

    /**
     * Look for a proper driver and trim the patch
     */
    private IPatch fixPatch(Patch pk, String patchString) {
        for (int i = 0; i < AppConfig.deviceCount(); i++) {
            // first check the requested device.
            // then starting index '1'. (index 0 is 'generic driver')
            // XXX pk.getDevice() -> getDevice()?
            Device device = (i == 0) ? pk.getDevice() : AppConfig.getDevice(i);
            for (int j = 0; j < device.driverCount(); j++) {
                IDriver d = device.getDriver(j);
                if (d instanceof Driver
                        && d.supportsPatch(patchString, pk.getByteArray())) {
                    // driver found
                    Driver driver = (Driver) d;
                    pk.setDriver(driver);
                    driver.trimSysex(pk);
                    JOptionPane
                            .showMessageDialog(null, "You requested a "
                                    + driver.toString() + " patch!"
                                    + "\nBut you got a "
                                    + pk.getDriver().toString() + " patch.",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
                    return pk;
                }
            } // end of driver (j) loop
        } // end of device (i) loop

        // driver not found
        pk.setDriver(null); //reset
        pk.setComment("Probably a "
                + LookupManufacturer.get(pk.getByteArray()[1], pk
                        .getByteArray()[2], pk.getByteArray()[3])
                + " Patch, Size: " + pk.getByteArray().length);
        JOptionPane.showMessageDialog(null, "You requested a "
                + this.toString() + " patch!"
                + "\nBut you got a not supported patch!\n" + pk.getComment(),
                "Warning", JOptionPane.WARNING_MESSAGE);
        return pk;
    }

    /**
     * This method trims a patch, containing more than one real
     * patch to a correct size. Useful for files containg more than one
     * bank for example. Some drivers are incompatible with this method
     * so it reqires explicit activation with the trimSize variable.
     * @param patch the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     */
    protected int trimSysex(Patch patch) { // no driver overrides this now.
        if (trimSize > 0 && patch.sysex.length > trimSize
                && patch.sysex[trimSize - 1] == (byte) 0xf7) {
            byte[] sysex = new byte[trimSize];
            System.arraycopy(patch.sysex, 0, sysex, 0, trimSize);
            patch.sysex = sysex;
        }
        return patch.sysex.length; // == trimSize
    }

    public final void calculateChecksum(IPatch myPatch) {
        calculateChecksum((Patch) myPatch);
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.<p>
     *
     * Need to be overridden if a patch is consist from multiple SysEX
     * messages.
     *
     * @param p a <code>Patch</code> value
     * @see IPatchDriver#calculateChecksum(IPatch)
     */
    protected void calculateChecksum(Patch p) {
	calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    /**
     * Caluculate check sum of a <code>Patch</code>.
     * <p>
     *
     * This method is called by calculateChecksum(Patch). The checksum
     * calculation method of this method is used by Roland, YAMAHA, etc.
     * Override this for different checksum calculation method.
     * <p>
     *
     * Compatibility Note: This method became 'static' method.
     *
     * @param patch
     *            a <code>Patch</code> value
     * @param start
     *            start offset
     * @param end
     *            end offset
     * @param offset
     *            offset of the checksum data
     * @see #calculateChecksum(IPatch)
     */
    protected static void calculateChecksum(Patch patch, int start, int end, int offset) {
	DriverUtil.calculateChecksum(patch.sysex, start, end, offset);
    }

    public final void send(IPatch myPatch, int bankNum, int patchNum) {
        storePatch((Patch) myPatch, bankNum, patchNum);
    }

    /**
     * Sends a patch to a set location on a synth.<p>
     * Override this if required.
     * @see IPatchDriver#send(IPatch, int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);
    }

    /**
     * Request the synth to send a patch dump. If <code>sysexRequestDump</code>
     * is not <code>null</code>, a request dump message is sent. Otherwise a
     * dialog window will prompt users.
     *
     * @see SysexHandler
     */
    public void requestPatchDump(int bankNum, int patchNum) {
	//clearMidiInBuffer(); now done by SysexGetDialog.GetActionListener.
	setBankNum(bankNum);
	setPatchNum(patchNum);
	if (sysexRequestDump == null) {
	    JOptionPane.showMessageDialog
		(PatchEdit.getInstance(),
		 "The " + toString()
		 + " driver does not support patch getting.\n\n"
		 + "Please start the patch dump manually...",
		 "Get Patch", JOptionPane.WARNING_MESSAGE);
	} else
	    send(sysexRequestDump.toSysexMessage(getDeviceID(),
						 new SysexHandler.NameValue("bankNum", bankNum),
						 new SysexHandler.NameValue("patchNum", patchNum)));
    }

    /** Send Program Change MIDI message. */
    protected void setPatchNum(int patchNum) {
        try {
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.PROGRAM_CHANGE, getChannel() - 1,
			   patchNum, 0); // Program Number
	    send(msg);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /** Send Control Change (Bank Select) MIDI message. */
    protected void setBankNum(int bankNum) {
        try {
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1,
			   0x00, //  Bank Select (MSB)
			   bankNum / 128); // Bank Number (MSB)
	    send(msg);
	    msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1,
			   0x20, //  Bank Select (LSB)
			   bankNum % 128); // Bank Number (MSB)
	    send(msg);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    public boolean hasEditor() {
	if (this instanceof IBankDriver)
	    return true;
	else if (getClass().equals(Driver.class)) // ex. Generic Driver
	    return false;
	else {
	    try {
		getClass().getDeclaredMethod("editPatch",
					     new Class[] {Patch.class});
		return true;
	    } catch (NoSuchMethodException e) {
		return false;
	    }
	}
    }

    public final JSLFrame edit(IPatch p) {
        return editPatch((Patch) p);
    }

    /**
     * Override this if your driver implement Patch Editor.  Don't
     * override this otherwise.
     * @see IPatchDriver#edit(IPatch)
     */
    protected JSLFrame editPatch(Patch p) {
	ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Editing.");
	return null;
    }

    // MIDI in/out mothods to encapsulate lower MIDI layer
    /** Send MidiMessage to MIDI outport. */
    public final void send(MidiMessage msg) {
	device.send(msg);
    }

    /** Send Sysex byte array data to MIDI outport. */
    // Called from SysexWidget
    public final void send(byte[] sysex) {
	try {
	    SysexMessage[] a = MidiUtil.byteArrayToSysexMessages(sysex);
	    for (int i = 0; i < a.length; i++)
		device.send(a[i]);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    /** Send ShortMessage to MIDI outport. */
    public final void send(int status, int d1, int d2) {
	ShortMessage msg = new ShortMessage();
	try {
	    msg.setMessage(status, d1, d2);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
	send(msg);
    }

    /** Send ShortMessage to MIDI outport. */
    public final void send(int status, int d1) {
	send(status, d1, 0);
    }

    public void sendParameter(IPatch patch, SysexWidget.IParameter param) {
        // Subclasses of Driver should use SysexSenders, no this.
    }

    public String toString() {
	return getManufacturerName() + " " + getModelName() + " "
	    + getPatchType();
    }
    // end of IPatchDriver methods

    // ISingleDriver methods
    public final void play(IPatch p) {
        playPatch((Patch) p);
    }

    protected void playPatch(Patch p) {
	playPatch();
    }

    /**
     * Play note.
     * plays a MIDI file or a single note depending which preference is set.
     * Currently the MIDI sequencer support isn't implemented!
     * @see ISingleDriver#play(IPatch)
     */
    protected void playPatch() {
	if (AppConfig.getSequencerEnable())
            playSequence();
        else
            playNote();
    }

    private void playNote() {
        try {
// 	    sendPatch(p);
	    Thread.sleep(100);
	    ShortMessage msg = new ShortMessage();
	    msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
			   AppConfig.getNote(),
			   AppConfig.getVelocity());
	    send(msg);

	    Thread.sleep(AppConfig.getDelay());

	    msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
			   AppConfig.getNote(),
			   0);	// expecting running status
	    send(msg);
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    private void playSequence() {
	MidiUtil.startSequencer(getDevice().getPort());
    }

    public final void send(IPatch p) {
        sendPatch((Patch) p);
    }

    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable).
     * @see ISingleDriver#send(IPatch)
     */
    protected void sendPatch(Patch p) {
	sendPatchWorker(p);
    }

    /**
     * Set Device ID and send the sysex data to MIDI output.
     */
    protected final void sendPatchWorker(Patch p) {
        if (deviceIDoffset > 0)
	    ((Patch)p).sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);

        send(((Patch)p).sysex);
    }
    // end of ISingleDriver methods

    /** Return the name of manufacturer of synth. */
    protected final String getManufacturerName() {
	return device.getManufacturerName();
    }

    /** Return the name of model of synth. */
    protected final String getModelName() {
	return device.getModelName();
    }

    /**
     *  Return the personal name of the synth.
     * 
     * @see Device#setSynthName(String)
     */
    protected final String getSynthName() {
	return device.getSynthName();
    }

    /** Return MIDI devide ID. */
    public final int getDeviceID() {
	return device.getDeviceID();
    }

    /** Return MIDI channel number. */
    public final int getChannel() {
        return device.getChannel();
    }

    /**
     * Gets the name of the patch from the sysex. If the patch uses
     * some weird format or encoding, this needs to be overidden in
     * the particular driver.
     * @see Patch#getName
     */
    protected String getPatchName(Patch p) { // called by bank driver
        if (patchNameSize == 0)
	    return ("-");
        try {
 	    return new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII");
	} catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

    /**
     * Set the name of the patch in the sysex. If the patch uses some
     * weird format or encoding, this needs to be overidden in the
     * particular driver.
     * @see Patch#setName
     */
    protected void setPatchName(Patch p, String name) { // called by bank driver
        if (patchNameSize == 0) {
	    ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Name Editing.");
	    return;
	}

	while (name.length() < patchNameSize)
		name = name + " ";

        byte[] namebytes = new byte[patchNameSize];
        try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		((Patch)p).sysex[patchNameStart + i] = namebytes[i];
	} catch (UnsupportedEncodingException ex) {
	    return;
	}
        calculateChecksum(p);	// Is this required here?
    }

    /** Getter of patchNameSize. */
    public int getPatchNameSize() {
        return patchNameSize;
    }

    //
    // For debugging.
    //
    /**
     * Returns String .. full name for referring to this patch for
     * debugging purposes.
     */
    protected String getFullPatchName(Patch p) {
	return getManufacturerName() + " | " + getModelName() + " | "
	    + getPatchType() + " | " + getSynthName() + " | " + getPatchName(p);
    }

    //
    // Remove the following lines after 0.20 is released.
    //
    // deviceNum and driverNum are set by
    // PatchEdit.appConfig.reassignDeviceDriverNums method.
    /** Which deviceNum does the device of this driver goes with? */
    //private int deviceNum;
    /** Which driverNum does the device of this driver goes with? */
    //private int driverNum;

    /*
     * The following fields are obsoleted when the Device class was
     * introduced.  Use getter functions to access them.
     */
    /**
     * The channel the user assigns to this driver.
     */
    //private int channel = 1;

    /**
     * The MIDI Out port the user assigns to this driver.
     */
    //private int port;
    /**
     * The MIDI In port the user assigns to this driver.
     */
    //private int inPort;	// phil@muqus.com
    /**
     * The company which made the Synthesizer
     */
    //private String manufacturer;
    /**
     * The models supported by this driver eg TG33/SY22
     */
    //private String model;
    /**
     * The response to the Universal Inquiry Message.  It can have
     * wildcards (*). It can be up to 16 bytes
     */
    // ADDED BY GERRIT GEHNEN
    //private String inquiryID;
    /**
     * A Shorthand alias for the Synth this driver supports (eg TG33,K5k)
     */
    //private String id;

    /**
     * Getter for property <code>deviceNum</code>.<p>
     * This method will be deprecated.
     * Use <code>getDevice()</code> instead of
     * <code>PatchEdit.appConfig.getDevice(getDeviceNum())</code>.
     * Use <code>new Patch(sysex, getDevice())</code> instead of
     * <code>new Patch(getDeviceNum(), sysex)</code>.
     * @deprecated Don't use this.
     */
    final int getDeviceNum() {
        return device.getDeviceNum();
    }

    /**
     * Getter for property <code>driverNum</code>.<p>
     * This method will be deprecated.
     * Use <code>new Patch(sysex, this)</code> instead of
     * <code>new Patch(sysex, getDeviceNum(), getDriverNum())</code>.
     * @deprecated Don't use this.
     */
    final int getDriverNum() {
        return device.getDriverNum(this);
    }

    /** @deprecated use getDevice().getPort() */
    protected final int getPort() { // called by bank driver
        return device.getPort();
    }

    /** @deprecated use getDevice().getInPort() */
    public final int getInPort() {
	return device.getInPort();
    }

    /**
     * Prompts the user for the location to store the patch and stores
     * it.
     *
     * @param p a <code>Patch</code> value
     * @param bankNum initially selected bank number.
     * @param patchNum initially selected patch number.
     * @deprecated Nobody uses this method now.
     */
    protected void choosePatch(Patch p, int bankNum, int patchNum) {
	int bank = 0;
	int patch = 0;
	String bankstr;
	String patchstr;

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
     * @deprecated Nobody uses this method now.
     */
    protected void choosePatch(Patch p) {
	choosePatch(p, 0, 0);
    }

    /*
    void setDeviceNum(int deviceNum) {
	this.deviceNum = deviceNum;
    }

    void setDriverNum(int driverNum) {
	this.driverNum = driverNum;
    }

    public void setPort(int port) { // 'public' for storable interface
	this.port = port;
	//device.setPort(port);
    }

    public void setInPort(int inPort) { // 'public' for storable interface
	this.inPort = inPort;
	//device.setInPort(inPort);
    }

    public void setChannel(int channel) { // called by Device and for storable interface
	this.channel = channel;
    }

    protected void setSynthName(String s) {
	id = s;
    }
    */
}
