package org.jsynthlib.jsynthlib.xml;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import core.Device;
import core.ErrorMsg;
import core.IBankDriver;
import core.IPatch;
import core.IPatchDriver;
import core.ISingleDriver;
import core.JSLFrame;
import core.PatchTransferHandler;

/**
 * @author ribrdb
 */
public class XMLPatch implements IPatch {
    private byte[][] sysex;

    private String date, author, comment;
    private String name;

    private transient HashMap parameters;
    private transient HashMap cache = new HashMap();

    private transient XMLDriver driver;
    private transient XMLDevice device;
    private transient SysexGroup[] groups;
    private transient SysexDesc[] descs;
    
    protected XMLPatch(HashMap parameters, SysexGroup[] groups, SysexDesc[] descs, int size) {
        this.parameters = parameters;
        this.groups = groups;
        this.descs = descs;
        this.size = size;
    }
    
    protected XMLPatch(HashMap parameters, XMLDriver driver, XMLDevice device, SysexGroup[] groups,
            SysexDesc[] descs, int size) {
        this.parameters = parameters;
        this.driver = driver;
        this.device = device;
        this.groups = groups;
        this.descs = descs;
        sysex = new byte[descs.length][];
    }

    private int size;
    
    protected int getSize() {
        return size;
    }
    protected void setSize(int size) {
        this.size = size;
    }
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
        this.driver = (XMLDriver)driver;
    }

    public String getPatchHeader() {
        return getHeader(sysex[0]);
    }
    
    protected String getHeader(byte[] data) {
        StringBuffer patchstring = new StringBuffer("F0");
        int end = Math.min(16, data.length);
        for (int i = 1; i < end; i++) {
            if ((int) (data[i] & 0xff) < 0x10)
                patchstring.append("0");
            patchstring.append(Integer.toHexString((int) (data[i] & 0xff)));
        }
        return patchstring.toString();        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void calculateChecksum() {
        for (int i = 0; i < descs.length; i++) {
            descs[i].getChecksum().checksum(this, sysex[i]);
        }
    }

    public JSLFrame edit() {
        return driver.edit(this);
    }

    public void store(int bankNum, int patchNum) {
        driver.send(this, bankNum, patchNum);
    }

    // only for single patch
    public void play() {
        ((ISingleDriver) driver).play(this);
    }

    public void send() {
        ((ISingleDriver) driver).send(this);
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
        SysexMessage[] msgs = new SysexMessage[sysex.length];
        for (int i = 0; i < sysex.length; i++) {
            msgs[i] = new SysexMessage();
            try {
                msgs[i].setMessage(SysexMessage.SYSTEM_EXCLUSIVE,
                        sysex[i], sysex[i].length);
            } catch (InvalidMidiDataException e) {
                ErrorMsg.reportStatus(e);
            }
        }
        return msgs;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < sysex.length; i++) {
            out.write((byte)SysexMessage.SYSTEM_EXCLUSIVE);
            out.write(sysex[i],0,sysex[i].length);
        }
        return out.toByteArray();
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

    public XMLParameter getParameter(String group, String message, String name) {
        return getParameter(group + '\u0000' + message + '\u0000' + name);
    }

    public XMLParameter getParameter(String id) {
        return (XMLParameter)parameters.get(id);
    }

    public Object clone() {
        throw new RuntimeException("Unimplemented");
    }

    public void useSysexFromPatch(IPatch p) {
        if (p.getClass() != XMLPatch.class) {
            throw new IllegalArgumentException();
        }
        sysex = ((XMLPatch) p).sysex;
    }
    
    protected XMLPatch newEmptyPatch() {
        return new XMLPatch(parameters, driver, device, groups, descs, size);
    }
    protected void setDriver(XMLDriver d) {
        driver = d;
    }
    protected void setDevice(XMLDevice d) {
        device = d;
    }
    protected void setMessages(SysexMessage[] messages) {
        if (messages.length != sysex.length) {
            throw new IllegalArgumentException("Wrong number of sysex messages.");
        }
        LinkedList l = new LinkedList();
        for (int i = 0; i < descs.length; i++) {
            l.add(descs[i]);
            sysex[i] = null;
        }
        for (int i = 0; i < messages.length; i++) {
            String header = getHeader(messages[i].getData());
            Iterator it = l.iterator();
            boolean found = false;
            while (it.hasNext()) {
                SysexDesc d = (SysexDesc)it.next();
                if (d.matches(header)) {
                    found = true;
                    sysex[d.getId()] = messages[i].getData();
                    it.remove();
                    break;
                }
            }
            if (!found)
                throw new IllegalArgumentException("Unrecognized message with header "+header);
        }
    }
    boolean supportsMessages(SysexMessage[] messages) {
        if (messages.length != sysex.length) {
            return false;
        }
        LinkedList l = new LinkedList();
        for (int i = 0; i < descs.length; i++) {
            l.add(descs[i]);
        }
        for (int i = 0; i < messages.length; i++) {
            String header = getHeader(messages[i].getData());
            Iterator it = l.iterator();
            boolean found = false;
            while (it.hasNext()) {
                SysexDesc d = (SysexDesc)it.next();
                if (d.matches(header)) {
                    found = true;
                    it.remove();
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }
    protected HashMap getCache() {
        return cache;
    }
    /**
     * @param sysex_index
     * @return
     */
    public byte[] getMessage(int sysex_index) {
        return sysex[sysex_index];
    }
    
    public void flush() {
        Iterator it = cache.keySet().iterator();
        while (it.hasNext()) {
            ((XMLParameter)it.next()).store(this);
            it.remove();
        }
    }

    public XMLPatch newPatch() {
        XMLPatch patch = newEmptyPatch();
        for (int i = 0; i < descs.length; i++) {
            sysex[i] = new byte[descs[i].getSize() - 1];
            sysex[i][sysex[i].length - 1] = (byte) 0xF7;
            XMLParameter[] params = descs[i].getParams();
            for (int j = 0; j < params.length; j++) {
                XMLParameter p = params[i];
                if (p.getDefault() != 0) {
                    descs[i].getDecoder().encode(p.getDefault(),p, sysex[i]);
                }
            }
        }
        return patch;
    }
}
