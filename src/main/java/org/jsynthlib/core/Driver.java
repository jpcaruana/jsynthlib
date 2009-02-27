package org.jsynthlib.core;

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
abstract public class Driver implements IPatchDriver {
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
     * @see DriverUtil#generateNumbers
     */
    protected String[] patchNumbers;
    /**
     * Array holding names or numbers for all banks.  Used for
     * comboBox selection.
     * @see #getBankNumbers
     * @see DriverUtil#generateNumbers
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

    //
    // IDriver interface methods
    //
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

    // These are not 'final' because BankDriver and Converter class override
    // them.
    // Synth drivers should not override these.
    public boolean isSingleDriver() {
        return true;
    }

    public boolean isBankDriver() {
        return false;
    }

    public boolean isConverter() {
        return false;
    }
    // end of IDriver interface methods
    //
    // IPatchDriver interface methods
    //
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

    /**
     * Check if this driver supports creating a new patch. By default it uses
     * reflection to test if the method createNewPatch() is overridden by the
     * subclass of Driver.
     */
    public boolean canCreatePatch() {
        try {
            getClass().getDeclaredMethod("createNewPatch", (Class[])null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public final IPatch createPatch() {
        return createNewPatch();
    }

    /**
     * Create a new Patch. Don't override this unless your driver properly
     * implement this method.
     * @see IPatchDriver#createPatch()
     * @see #createPatch()
     */
    protected Patch createNewPatch() { // overridden by subclass
        return null;
    }

    public IPatch createPatch(byte[] sysex) {
        return new Patch(sysex, this);
    }

    public IPatch[] createPatches(SysexMessage[] msgs) {
        byte[] sysex = MidiUtil.sysexMessagesToByteArray(msgs);
        IPatch[] patarray = DriverUtil.createPatches(sysex, getDevice());

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
     * Look for a proper driver and trim the patch.
     * @see #createPatches(SysexMessage[])
     * @see IPatchDriver#createPatches(SysexMessage[])
     */
    private IPatch fixPatch(Patch pk, String patchString) {
        byte[] sysex = pk.getByteArray();
        for (int i = 0; i < AppConfig.deviceCount(); i++) {
            // first check the device for the patch requested.
            // then starting index '1'. (index 0 is 'generic driver')
            Device device = (i == 0) ? pk.getDevice() : AppConfig.getDevice(i);
            for (int j = 0; j < device.driverCount(); j++) {
                IDriver d = device.getDriver(j);
                if (d instanceof Driver
                        && d.supportsPatch(patchString, sysex)) {
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
                + pk.lookupManufacturer()
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
     * @see #fixPatch(Patch, String)
     * @see IPatchDriver#createPatches(SysexMessage[])
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

    /**
     * Request the synth to send a patch dump. If <code>sysexRequestDump</code>
     * is not <code>null</code>, a request dump message is sent. Otherwise a
     * dialog window will prompt users.
     * @see IPatchDriver#requestPatchDump(int, int)
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

    // MIDI in/out mothods to encapsulate lower MIDI layer
    public final void send(MidiMessage msg) {
        device.send(msg);
    }

    public String toString() {
        return getManufacturerName() + " " + getModelName() + " "
        + getPatchType();
    }

    // end of IPatchDriver interface methods
    //
    // mothods for Patch class
    //
    /**
     * Gets the name of the patch from the sysex. If the patch uses
     * some weird format or encoding, this needs to be overidden in
     * the particular driver.
     * @see Patch#getName()
     */
    protected String getPatchName(Patch p) {
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
     * @see Patch#setName(String)
     */
    protected void setPatchName(Patch p, String name) {
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
                p.sysex[patchNameStart + i] = namebytes[i];
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    /**
     * Sends a patch to a set location on a synth.<p>
     * Override this if required.
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);
    }

    /**
     * Send Program Change MIDI message.
     * @see #storePatch(Patch, int, int)
     */
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

    /**
     * Send Control Change (Bank Select) MIDI message.
     * @see #storePatch(Patch, int, int)
     */
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

    /**
     * @see Patch#hasEditor()
     */
    boolean hasEditor() {
        try {
            getClass().getDeclaredMethod("editPatch",
                    new Class[] { Patch.class });
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Override this if your driver implement Patch Editor.  Don't
     * override this otherwise.
     * @see Patch#edit()
     */
    protected JSLFrame editPatch(Patch p) {
        ErrorMsg.reportError("Error", "The Driver for this patch does not support Patch Editing.");
        return null;
    }

    //
    // methods for ISinglePatch
    //
    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable).
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    protected void sendPatch(Patch p) {
        sendPatchWorker(p);
    }

    /**
     * Set Device ID and send the sysex data to MIDI output.
     * @see #sendPatch(Patch)
     */
    protected final void sendPatchWorker(Patch p) {
        if (deviceIDoffset > 0)
            p.sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);

        send(p.sysex);
    }

    /**
     * Play note.
     * plays a MIDI file or a single note depending which preference is set.
     * Currently the MIDI sequencer support isn't implemented!
     * @see Patch#play()
     * @see ISinglePatch#play()
     */
    protected void playPatch(Patch p) {
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
    // end of ISinglePatch methods

    //
    // Driver class utility methods
    //
    /** Return the name of manufacturer of synth. */
    protected final String getManufacturerName() {
        return device.getManufacturerName();
    }

    /** Return the name of model of synth. */
    protected final String getModelName() {
        return device.getModelName();
    }

    /** Return the personal name of the synth. */
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

    /** Getter of patchNameSize. */
    public int getPatchNameSize() {
        return patchNameSize;
    }

    /**
     * Calculate check sum of a <code>Patch</code>.<p>
     *
     * Need to be overridden if a patch is consist from multiple SysEX
     * messages.
     *
     * @param p a <code>Patch</code> value
     */
    protected void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    /**
     * Calculate check sum of a <code>Patch</code>.
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
     * @see #calculateChecksum(Patch)
     */
    protected void calculateChecksum(Patch patch, int start, int end, int offset) {
        DriverUtil.calculateChecksum(patch.sysex, start, end, offset);
    }

    /**
     * Send Sysex byte array data to MIDI outport.
     *
     * @param sysex
     *            a byte array of Sysex data. If it has checksum, the checksum
     *            must be calculated before calling this method.
     */
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

    //
    // For debugging.
    //
    /**
     * Returns String .. full name for referring to this patch for
     * debugging purposes.
     */
    protected String getFullPatchName(Patch p) {
        return getManufacturerName() + " | " + getModelName() + " | "
        + p.getType() + " | " + getSynthName() + " | " + getPatchName(p);
    }
}
