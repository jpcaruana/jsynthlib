package core;

import javax.sound.midi.MidiMessage;

/**
 * This includes methods shared by ISingleDriver and IBankDriver which are used
 * by LibraryFrame, etc.
 * 
 * @author ribrdb
 */
public interface IPatchDriver extends IDriver {

    /**
     * Return the size of the patch which the driver handles.
     * <code>SysexGetDialog</code> uses this to estimate timeout value.
     */
    public abstract int getPatchSize();

    /** Return MIDI input port number. */
    public abstract int getInPort();

    /** Return the name of manufacturer of synth. */
    public abstract String getManufacturerName();

    /** Return the name of model of synth. */
    public abstract String getModelName();

    /** Return MIDI channel number (<code>device.channel</code>). */
    public abstract int getChannel();

    /**
     * Check if this driver supports creating a new patch. By default it uses
     * reflection to test if the method createNewPatch is declared in the
     * subclass of Driver.
     */
    public abstract boolean canCreatePatch();

    /**
     * Create a new Patch.
     */
    // XXX replace with IPatch createPatch()
    public abstract Patch createNewPatch();

    /**
     * Create a patch from a byte array for the driver.
     * @param sysex Byte array of sysex data.
     * @return <code>IPatch</code> value.
     */
    public abstract IPatch createPatch(byte[] sysex);
    //public abstract IPatch createPatch(SysexMessage[] msgs);

    /**
     * Returns true if a Patch Editor is implemented.
     * 
     * @see #editPatch
     */
    public abstract boolean hasEditor();

    /**
     * Returns a Patch Editor Window for this Patch.
     * 
     * @see #hasEditor
     */
    public abstract JSLFrame editPatch(IPatch p);

    //----- Start phil@muqus.com
    /**
     * Request MIDI synth to send a patch dump. If <code>sysexRequestDump</code>
     * is not <code>null</code>, a request dump message is sent. Otherwise a
     * dialog window will prompt users.
     * 
     * @see SysexHandler
     */
    public abstract void requestPatchDump(int bankNum, int patchNum);

    /** Send MidiMessage to MIDI outport. */
    public abstract void send(MidiMessage msg);

    /**
     * Returns full name for referring to this Driver. Used by driver selection
     * comboboxes.
     */
    public abstract String toString();

    /**
     * Returns String[] returns full list of patchNumbers
     */
    public abstract String[] getPatchNumbers();

    /**
     * Returns String[] list of patch numbers for writable patches. (This has
     * nothing to with the "Storable" class in JSynthLib.)
     */
    public abstract String[] getPatchNumbersForStore();

    /**
     * Returns String[] returns full list of bankNumbers
     */
    public abstract String[] getBankNumbers();

    /**
     * Caluculate check sum of a <code>patch</code>.<p>
     *
     * @param patch a <code>IPatch</code> value
     */
    public abstract void calculateChecksum(IPatch patch);

    /**
     * Sends a patch to a set location on a synth.
     */
    public abstract void storePatch(IPatch myPatch, int bankNum, int patchNum);

    /**
     * This method trims a patch. Useful for files containg more than one bank for example.
     * 
     * @param patch
     *            the patch, which should be trimmed.
     * 
     * XXX: I think we can remove this once we have a propper patch factory.
     */
    public abstract void trimSysex(IPatch patch);
}