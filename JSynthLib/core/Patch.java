package core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

/**
 * A class for MIDI System Exclusive Message patch data.
 * <p>
 * 
 * There are many kinds of constructors. Driver can use one of the follows (in
 * preferred order).
 * <ol>
 * <li><code>Patch(byte[], Driver)</code>
 * <li><code>Patch(byte[], Device)</code>
 * <li><code>Patch(byte[])</code>
 * </ol>
 * 
 * Use <code>Patch(byte[], Driver)</code> form if possible. The latter two
 * constructors <b>guesses </b> the proper driver by using the
 * <code>Driver.supportsPatch</code> method. It is not efficient.
 * <p>
 * 
 * Use <code>Patch(byte[])</code> only when you have no idea about either
 * Driver or Device for which your Patch is. If you know that the patch you are
 * creating does not correspond to any driver, use
 * <code>Patch(byte[], (Driver) null)</code>, since it is much more efficient
 * than <code>Patch(byte[])</code>.
 * 
 * @author ???
 * @version $Id$
 * @see Driver#supportsPatch
 */
public class Patch implements IPatch {
    /** Driver for this Patch. */
    private transient Driver driver;

    /**
     * MIDI System Exclusive Message byte array.
     */
    public byte[] sysex;

    // 'String' is better. But 'StringBuffer' is used to keep
    // the compatibility for serialized files
    /** "Field 1" comment. */
    private StringBuffer date;

    /** "Field 2" comment. */
    private StringBuffer author;

    /** "Comment" comment. */
    private StringBuffer comment;

    // not used. What's this?
    // I belive this is used by java to maintain backwords compatabl
    static final long serialVersionUID = 2220769917598497681L;

    /**
     * Constructor - Driver is known. This is often used by a Single Driver and
     * its subclass.
     * 
     * @param gsysex
     *            The MIDI SysEx message.
     * @param driver
     *            a <code>Driver</code> instance. If <code>null</code>, a
     *            null driver (Generic Driver) is used.
     */
    public Patch(byte[] gsysex, Driver driver) {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
        sysex = gsysex;
        setDriver(driver);
    }

    /**
     * Constructor - Device is known but Driver is not. This is often used by a
     * Bank Driver and its subclass.
     * 
     * @param gsysex
     *            The MIDI SysEx message.
     * @param device
     *            a <code>Device</code> instance.
     */
    public Patch(byte[] gsysex, Device device) {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
        sysex = gsysex;
        chooseDriver(device);
    }

    /**
     * Constructor - Either Device nor Driver is not known. Consider using
     * <code>Patch(byte[], Driver)</code> or <code>Patch(byte[],
     * Device)</code>.
     * If you know that the patch you are creating does not correspond to any
     * driver, use <code>Patch(byte[],
     * (Driver) null)</code>, since it is
     * much more efficient than this.
     * 
     * @param gsysex
     *            The MIDI SysEx message.
     */
    public Patch(byte[] gsysex) {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
        sysex = gsysex;
        chooseDriver();
    }

    /**
     * Constructor - only sysex is known.
     * 
     * @param gsysex
     *            The MIDI SysEx message.
     * @param offset
     *            offset address in <code>gsysex</code>.
     */
    // called by LibraryFrame and SceneFrame
    Patch(byte[] gsysex, int offset) {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
        sysex = new byte[gsysex.length - offset];
        System.arraycopy(gsysex, offset, sysex, 0, gsysex.length - offset);
        chooseDriver();
    }

    /**
     * Set <code>driverNum</code> by guessing from <code>sysex</code> by
     * using <code>Driver.suportsPatch</code> method.
     * 
     * @param dev
     *            The known device
     * @see Driver#supportsPatch
     */
    private boolean chooseDriver(Device dev) {
        String patchString = getPatchHeader();

        for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
            // iterating over all Drivers of the given device
            IPatchDriver drv = (IPatchDriver) dev.getDriver(idrv);
            if (drv.supportsPatch(patchString, this)) {
                drv.trimSysex(this);
                setDriver(drv);
                return true;
            }
        }
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a "
                + LookupManufacturer.get(sysex[1], sysex[2], sysex[3])
                + " Patch, Size: " + sysex.length);
        setDriver(AppConfig.getNullDriver());
        return false;
    }

    public boolean chooseDriver() {
        //Integer intg = new Integer(0);
        //StringBuffer driverString = new StringBuffer();
        String patchString = getPatchHeader();

        for (int idev = 0; idev < AppConfig.deviceCount(); idev++) {
            // Outer Loop, iterating over all installed devices
            Device dev = AppConfig.getDevice(idev);
            for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
                IPatchDriver drv = (IPatchDriver) dev.getDriver(idrv);
                // Inner Loop, iterating over all Drivers of a device
                if (drv.supportsPatch(patchString, this)) {
                    drv.trimSysex(this);
                    setDriver(drv);
                    return true;
                }
            }
        }
        // Unkown patch, try to guess at least the manufacturer
        comment = new StringBuffer("Probably a "
                + LookupManufacturer.get(sysex[1], sysex[2], sysex[3])
                + " Patch, Size: " + sysex.length);
        setDriver(AppConfig.getNullDriver());
        return false;
    }

    public String getDate() {
        return date.toString();
    }

    public void setDate(String date) {
        this.date = new StringBuffer(date);
    }

    public String getAuthor() {
        return author.toString();
    }

    public void setAuthor(String author) {
        this.author = new StringBuffer(author);
    }

    public String getComment() {
        return comment.toString();
    }

    public void setComment(String comment) {
        this.comment = new StringBuffer(comment);
    }

    public Device getDevice() {
        return driver.getDevice();
    }

    public IPatchDriver getDriver() {
        return driver;
    }

    public void setDriver(IPatchDriver driver) {
        this.driver = (driver == null) ? (Driver) AppConfig.getNullDriver()
                : (Driver) driver;
    }

    public byte[] getByteArray() {
        return sysex;
    }

    public SysexMessage[] getMessages() {
        try {
            return MidiUtil.byteArrayToSysexMessages(sysex);
        } catch (InvalidMidiDataException ex) {
            return null;
        }
    }

    // Transferable interface methods

    public Object getTransferData(DataFlavor p1)
            throws UnsupportedFlavorException, IOException {
        return this;
    }

    public boolean isDataFlavorSupported(final DataFlavor p1) {
        // ErrorMsg.reportStatus("isDataFlavorSupported "+driverNum);
        return p1.match(PatchTransferHandler.PATCH_FLAVOR);
    }

    public DataFlavor[] getTransferDataFlavors() {
        // ErrorMsg.reportStatus("getTransferDataFlavors "+driverNum);
        DataFlavor[] df = { PatchTransferHandler.PATCH_FLAVOR };
        return df;
    }

    // end of Transferable interface methods

    // Clone interface method
    public Object clone() {
        try {
            Patch p = (Patch) super.clone();
            p.sysex = (byte[]) sysex.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            // Cannot happen -- we support clone, and so do arrays
            throw new InternalError(e.toString());
        }
    }

    // end of Clone interface method

    public IPatch[] dissect() {
        IPatch[] patarray;
        Device dev = getDevice();
        search: {
            String patchString = this.getPatchHeader();

            for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
                IDriver drv = dev.getDriver(idrv);
                if ((drv instanceof IConverter)
                        && drv.supportsPatch(patchString, this)) {
                    patarray = ((IConverter) drv).extractPatch(this);
                    if (patarray != null)
                        break search; // found!
                }
            }
            // No conversion. Try just the original patch....
            return new IPatch[] { this };
        }
        // Conversion was sucessfull, we have at least one
        // converted patch. Assign a proper driver to each patch of patarray
        for (int i = 0; i < patarray.length; i++) {
            String patchString = patarray[i].getPatchHeader();
            for (int jdrv = 0; jdrv < dev.driverCount(); jdrv++) {
                IPatchDriver drv = (IPatchDriver) dev.getDriver(jdrv);
                if (drv.supportsPatch(patchString, patarray[i]))
                    patarray[i].setDriver(drv);
            }
        }
        return patarray;
    }

    /**
     * Return a hexadecimal string for Driver.supportsPatch at most 16 byte
     * sysex data.
     * 
     * @see Driver#supportsPatch
     */
    public String getPatchHeader() {
        StringBuffer patchstring = new StringBuffer("F0");

        // Some Sysex Messages are shorter than 16 Bytes!
        // 	for (int i = 1; (sysex.length < 16) ? i < sysex.length : i < 16; i++)
        // {
        for (int i = 1; i < Math.min(16, sysex.length); i++) {
            if ((int) (sysex[i] & 0xff) < 0x10)
                patchstring.append("0");
            patchstring.append(Integer.toHexString((int) (sysex[i] & 0xff)));
        }
        return patchstring.toString();
    }

    /**
     * Dump byte data array. Only for debugging.
     * 
     * @return string like "[2,3] f0 a3 00"
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + driver + "] "
                + Utility.hexDumpOneLine(sysex, 0, -1, 20));
        return buf.toString();
    }

    public String getName() {
        return driver.getPatchName(this);
    }

    public void setName(String s) {
        driver.setPatchName(this, s);
    }

    public void calculateChecksum() {
        driver.calculateChecksum(this);
    }

    public JSLFrame edit() {
        return driver.editPatch(this);
    }

    public void store(int bankNum, int patchNum) {
        driver.storePatch(this, bankNum, patchNum);
    }

    public void trimSysex() {
        driver.trimSysex(this);
    }

    // only for single patch
    public void play() {
        ((ISingleDriver) driver).playPatch(this);
    }

    public void send() {
        ((ISingleDriver) driver).sendPatch(this);
    }

    // only for bank patch
    public void put(IPatch singlePatch, int patchNum) {
        ((IBankDriver) driver).checkAndPutPatch(this, singlePatch, patchNum);
    }

    public void delete(int patchNum) {
        ((IBankDriver) driver).deletePatch(this, patchNum);
    }

    public IPatch get(int patchNum) {
        return ((IBankDriver) driver).getPatch(this, patchNum);
    }

    public String getName(int patchNum) {
        return ((IBankDriver) driver).getPatchName(this, patchNum);
    }

    public void setName(int patchNum, String name) {
        ((IBankDriver) driver).setPatchName(this, patchNum, name);
    }

    public void useSysexFromPatch(IPatch ip) {
        byte[] s = ip.getByteArray();
        if (s.length != sysex.length)
            throw new IllegalArgumentException();
        sysex = s;
    }
}