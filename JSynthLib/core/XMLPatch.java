package core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.midi.SysexMessage;

/**
 * @author ribrdb
 */
public class XMLPatch implements IPatch {
    private SysexMessage[] messages;

    private String date, author, comment;

    private String name;

    private transient HashMap parameters;

    private transient IPatchDriver driver;

    private transient Device device;

    private transient XMLDecoder decoder;

    public boolean chooseDriver() {
        return false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String _date) {
        date = _date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Device getDevice() {
        return device;
    }

    public IPatchDriver getDriver() {
        return driver;
    }

    public void setDriver(IPatchDriver driver) {
        this.driver = driver;
    }

    public IPatch[] dissect() {
        // TODO Auto-generated method stub
        return null;
    }

    public StringBuffer getPatchHeader() {
        StringBuffer patchstring = new StringBuffer("F0");
        byte[] data = null;
        try {
            data = messages[0].getData();
        } catch (NullPointerException ex) {
            return null;
        }

        int end = Math.min(16, data.length);
        for (int i = 1; i < end; i++) {
            if ((int) (data[i] & 0xff) < 0x10)
                patchstring.append("0");
            patchstring.append(Integer.toHexString((int) (data[i] & 0xff)));
        }
        return patchstring;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SysexMessage[] getMessages() {
        return messages;
    }

    public byte[] getByteArray() {
        SysexMessage[] msgs = getMessages();
        if (msgs == null)
            return null;
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        for (int i = 0; i < msgs.length; i++)
            fileOut.write(msgs[i].getMessage(), 0, msgs[i].getLength());
        return fileOut.toByteArray();
    }

    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] df = { PatchTransferHandler.PATCH_FLAVOR };
        return df;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return this;
    }

    public void setParameter(String message, String name, Object value) {
        decoder.setParameter(message, name, value);
    }

    public void setParameter(String group, String message, String name,
            Object value) {
        decoder.setParameter(group, message, name, value);
    }

    public Object getParameter(String name) {
        return decoder.getParameter(name);
    }

    public void setParameter(String name, Object value) {
        decoder.setParameter(name, value);
    }

    public Object getParameter(String group, String message, String name) {
        return decoder.getParameter(group, message, name);
    }

    public Object getParameter(String message, String name) {
        return decoder.getParameter(message, name);
    }

    public Object clone() {
        throw new RuntimeException("Unimplemented");
    }

    public void useSysexFromPatch(IPatch p) {
        if (p.getClass() != XMLPatch.class) {
            throw new IllegalArgumentException();
        }
        messages = ((XMLPatch) p).messages;
    }
}