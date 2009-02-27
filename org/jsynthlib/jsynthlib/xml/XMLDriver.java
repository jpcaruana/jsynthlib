package org.jsynthlib.jsynthlib.xml;

import java.io.File;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.jsynthlib.xml.editor.EditorDescription;
import org.jsynthlib.jsynthlib.xml.editor.EditorParser;
import org.jsynthlib.jsynthlib.xml.editor.XMLEditor;
import org.xml.sax.SAXException;

import core.Device;
import core.ErrorMsg;
import core.IPatch;
import core.IPatchDriver;
import core.JSLFrame;
import core.MidiUtil;
import core.SysexWidget;

public class XMLDriver implements IPatchDriver {
    private String authors;
    private XMLPatch base_patch;
    
    private XMLDevice device;
    private XMLDriverImplementation imp;
    private String manufacturer;
    private String model;
    private String name;
    private File editorPath;
    private EditorDescription editor;
    private final String[] patch_numbers;
    private final String[] writable_patch_numbers;
    private final String[] bank_numbers;
    private final String[] writable_bank_numbers;
    
    
    XMLDriver(final String[] patch_numbers,
            final String[] writable_patch_numbers, final String[] bank_numbers,
            final String[] writable_bank_numbers, XMLPatch p, XMLDriverImplementation imp) {
        this.patch_numbers = patch_numbers;
        if (writable_patch_numbers == null)
            this.writable_patch_numbers = patch_numbers;
        else
            this.writable_patch_numbers = writable_patch_numbers;
        this.bank_numbers = bank_numbers;
        if (writable_bank_numbers == null)
            this.writable_bank_numbers = bank_numbers;
        else
            this.writable_bank_numbers = writable_bank_numbers;
        base_patch = p;
        this.imp = imp;
    }
    
    /* TODO
     public String[] getBankNumbersForStore() {
     return writable_bank_numbers;
     }
     */
    
    public void calculateChecksum(IPatch patch) {
        ((XMLPatch)patch).calculateChecksum();
    }
    
    public boolean canCreatePatch() {
        return true;
    }
    
    public IPatch createPatch() {
        return base_patch.newPatch();
    }
    
    public IPatch createPatch(byte[] sysex) {
        SysexMessage[] msgs;
        try {
            msgs = MidiUtil.byteArrayToSysexMessages(sysex);
        } catch (InvalidMidiDataException e) {
            return null;
        }
        return createPatch(msgs);
    }
    
    public IPatch[] createPatches(SysexMessage[] msgs) {
        return new IPatch[] { createPatch(msgs) };
    }
    
    private IPatch createPatch(SysexMessage[] msgs) {
        XMLPatch np = base_patch.newEmptyPatch();
        try {
            np.setMessages(msgs);
            return np;
        } catch (IllegalArgumentException ex) {
            ErrorMsg.reportStatus(ex);
            return null;
        }
    }
    
    public JSLFrame edit(IPatch p) {
        EditorDescription editor = getEditor();
        try {
            if (editor != null)
                return new XMLEditor(editor, (XMLPatch) p);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            ErrorMsg.reportError("Error Creating Editor", "There was an error creating the patch editor", e);
        }
        return null;
    }
    
    private EditorDescription getEditor() {
        EditorParser p = new EditorParser();
        try {
            p.parse(editorPath);
        } catch (SAXException e) {
            Exception ex = e;
            if (e.getException() != null)
                ex = e.getException();
            e.printStackTrace();
            ErrorMsg.reportError("Parse Error", "Error parsing editor", ex);
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            ErrorMsg.reportError("Parse Error", "Error parsing editor", e);
            return null;
        }
        return p.getEditor();
    }
    
    public boolean hasEditor() {
        return (editorPath != null) && editorPath.canRead();
    }
    
    public String getAuthors() {
        return authors;
    }
    void setAuthors(String s) {
        authors = s;
    }
    
    public String[] getBankNumbers() {
        return bank_numbers;
    }
    
    public int getChannel() {
        return device.getChannel();
    }
    
    public Device getDevice() {
        return device;
    }
    
    public int getInPort() {
        return device.getInPort();
    }
    
    public String getManufacturerName() {
        return manufacturer;
    }
    
    void setManufacturerName(String s) {
        manufacturer = s;
    }
    
    public String getModelName() {
        return model;
    }
    
    void setModelName(String s) {
        model = s;
    }
    
    public String[] getPatchNumbers() {
        return patch_numbers;
    }
    
    public String[] getPatchNumbersForStore() {
        return writable_patch_numbers;
    }
    
    public int getPatchSize() {
        return base_patch.getSize();
    }
    
    public String getPatchType() {
        return name;
    }
    protected void setPatchType(String s) {
        name = s;
    }
    
    public void play(IPatch patch) {
        imp.playPatch((XMLPatch)patch);
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        imp.requestPatchDump(device, bankNum, patchNum);
        
    }
    
    public void send(MidiMessage msg) {
        device.send(msg);
    }
    
    public void sendParameter(IPatch patch, SysexWidget.IParameter param) {
        imp.sendParameter((XMLPatch)patch, (XMLParameter)param);
        
    }
    
    public void send(IPatch patch) {
        imp.sendPatch((XMLPatch)patch);
    }
    
    public void setDevice(Device device) {
        this.device = (XMLDevice)device;
        if (manufacturer == null)
            manufacturer = device.getManufacturerName();
        if (model == null)
            model = device.getModelName();
        if (authors == null)
            authors = device.getAuthors();
        base_patch.setDevice(this.device);
        base_patch.setDriver(this);
    }
    public void send(IPatch myPatch, int bankNum, int patchNum) {
        imp.storePatch((XMLPatch)myPatch, bankNum, patchNum);    
    }
    
    public boolean supportsPatch(String patchString, byte[] sysex) {
        SysexMessage[] msgs;
        try {
            msgs = MidiUtil.byteArrayToSysexMessages(sysex);
        } catch (InvalidMidiDataException e) {
            return false;
        }
        return base_patch.supportsMessages(msgs);
    }
    
    public boolean supportsPatch(String patchString, IPatch patch) {
        return base_patch.supportsMessages(patch.getMessages());
    }
    
    public final byte[] export(IPatch patch) {
        calculateChecksum(patch);
        return patch.getByteArray();
    }
    
    public final boolean isSingleDriver() {
        return true;
    }
    
    public final boolean isBankDriver() {
        return false;
    }
    
    public final boolean isConverter() {
        return false;
    }
    
    public String toString() {
        return getManufacturerName() + " " + getModelName() + " "
        + getPatchType();
    }
    
    public void setEditor(File file) {
        editorPath = file;
    }
    
    XMLPatch getBasePatch() {
        return base_patch;
    }
}
