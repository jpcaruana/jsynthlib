package org.jsynthlib.jsynthlib.xml;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import core.AppConfig;
import core.Device;
import core.ErrorMsg;
import core.IPatch;
import core.ISingleDriver;
import core.JSLFrame;
import core.MidiUtil;
import core.SysexWidget;

public class XMLDriver implements ISingleDriver {
    private String authors;
    private XMLPatch base_patch;

    private XMLDevice device;
    private XMLDriverImplementation imp;
    private String manufacturer;
    private String model;
    private String name;
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
        return null;
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
    
    // XXX: XML Editors
    public boolean hasEditor() {
        return false;
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

    public final boolean isNullDriver() {
        return this == AppConfig.getNullDriver();
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
}
