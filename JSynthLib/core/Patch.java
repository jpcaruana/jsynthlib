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

    // This is used by java to maintain backwords compatibility.
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
        // commented out not to break backward compatibility
        //driver.trimSysex(this);
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
        setDriver((Driver) DriverUtil.chooseDriver(sysex, device));
        driver.trimSysex(this);
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
        setDriver((Driver) DriverUtil.chooseDriver(sysex));
        driver.trimSysex(this);
    }

    /**
     * Constructor - only sysex is known.
     * 
     * @param sysex
     *            The MIDI SysEx message.
     * @param offset
     *            offset address in <code>gsysex</code>.
     * @deprecated Don't use this.
     */
    // nobody uses this.
    Patch(byte[] sysex, int offset) {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
        this.sysex = new byte[sysex.length - offset];
        System.arraycopy(sysex, offset, this.sysex, 0, sysex.length - offset);
        setDriver((Driver) DriverUtil.chooseDriver(sysex));
        driver.trimSysex(this);
    }

    // IPatch interface methods
    public final String getDate() {
        return date.toString();
    }

    public final void setDate(String date) {
        this.date = new StringBuffer(date);
    }

    public final String getAuthor() {
        return author.toString();
    }

    public final void setAuthor(String author) {
        this.author = new StringBuffer(author);
    }

    public final String getComment() {
        return comment.toString();
    }

    public final void setComment(String comment) {
        this.comment = new StringBuffer(comment);
    }

    public final Device getDevice() {
        return driver.getDevice();
    }

    public final IPatchDriver getDriver() {
        return driver;
    }

    public final void setDriver(IPatchDriver driver) {
        this.driver = (driver == null) ? (Driver) AppConfig.getNullDriver()
                : (Driver) driver;
    }

    public final void setDriver() {
        setDriver((IPatchDriver) DriverUtil.chooseDriver(sysex));
    }

    public final byte[] export() {
        return ((IPatchDriver) driver).export(this);
    }

    public final byte[] getByteArray() {
        return sysex;
    }

    public final SysexMessage[] getMessages() {
        try {
            return MidiUtil.byteArrayToSysexMessages(sysex);
        } catch (InvalidMidiDataException ex) {
            return null;
        }
    }

    public int getSize() {
        return sysex.length;
    }
    
    public final String lookupManufacturer() {
        return LookupManufacturer.get(sysex[1], sysex[2], sysex[3]);
    }

    public void useSysexFromPatch(IPatch ip) {
        if (ip.getSize() != sysex.length)
            throw new IllegalArgumentException();
        sysex = ip.getByteArray();
    }

    // Transferable interface methods

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return (Patch) this;
    }

    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        ErrorMsg.reportStatus("Patch.isDataFlavorSupported " + flavor);
        return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { PatchTransferHandler.PATCH_FLAVOR };
    }

    // end of Transferable interface methods

    // Clone interface method
    public final Object clone() {
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

    /*
    public IPatch[] dissect() {
        IPatch[] patarray;
        Device dev = getDevice();
        search: {
            String patchString = this.getPatchHeader();

            for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
                IDriver drv = dev.getDriver(idrv);
                if ((drv.isConverter())
                        && drv.supportsPatch(patchString, this.sysex)) {
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
                if (drv.supportsPatch(patchString, patarray[i].getByteArray()))
                    patarray[i].setDriver(drv);
            }
        }
        return patarray;
    }
    */

    public String getPatchHeader() {
        return DriverUtil.getPatchHeader(sysex);
    }

    //
    // delegation methods
    //
    public final String getName() {
        return driver.getPatchName(this);
    }

    public final void setName(String s) {
        driver.setPatchName(this, s);
    }

    public final void calculateChecksum() {
        driver.calculateChecksum(this);
    }

    public final JSLFrame edit() {
        return driver.editPatch(this);
    }

    public final void send(int bankNum, int patchNum) {
        driver.storePatch(this, bankNum, patchNum);
    }

    // only for single patch
    public final void play() {
        driver.play(this);
    }

    public final void send() {
        driver.send(this);
    }

    // only for bank patch
    public final void put(IPatch singlePatch, int patchNum) {
        ((BankDriver) driver).checkAndPutPatch(this, singlePatch, patchNum);
    }

    public final void delete(int patchNum) {
        ((BankDriver) driver).deletePatch(this, patchNum);
    }

    public final IPatch get(int patchNum) {
        return ((BankDriver) driver).getPatch(this, patchNum);
    }

    public final String getName(int patchNum) {
        return ((BankDriver) driver).getPatchName(this, patchNum);
    }

    public final void setName(int patchNum, String name) {
        ((BankDriver) driver).setPatchName(this, patchNum, name);
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

}