package core;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

/**
 * Interface for methods for both single drivers and bank drivers.
 *
 * @author ribrdb
 * @version $Id$
 * @see IDriver
 * @see ISingleDriver
 * @see IBankDriver
 */
public interface IPatchDriver extends IDriver {
    /**
     * Return the size of the patch which the driver handles.
     * <code>SysexGetDialog</code> uses this to estimate timeout value.
     * @see SysexGetDialog
     */
    int getPatchSize();

    /**
     * Returns String[] returns full list of patchNumbers
     * @see Driver#generateNumbers
     */
    String[] getPatchNumbers();

    /**
     * Returns String[] list of patch numbers for writable patches.
     * This can be overridden if some patch locations are read only.
     * e.g. the Waldorf Pulse has 100 patches, but only 0 to 39 are writable.
     * Currently writable patches are assumed to start at patch location 0.
     * (This has nothing to with the "Storable" class in JSynthLib.)
     * @see Driver#generateNumbers
     */
    String[] getPatchNumbersForStore();

    /**
     * Returns String[] returns full list of bankNumbers
     * @see Driver#generateNumbers
     */
    String[] getBankNumbers();

    /**
     * Check if this driver supports creating a new patch. By default it uses
     * reflection to test if the method createNewPatch is declared in the
     * subclass of Driver.
     */
    boolean canCreatePatch();

    /**
     * Create a new Patch.
     */
    IPatch createPatch();

    /**
     * Create an array of patch from an array of SysexMessage for the driver.
     * @param msgs an array of SysexMessage.
     * @return an array of <code>IPatch</code> value.
     */
    // for SysexGetDialog.pasterInfoSelectedFrame()
    IPatch[] createPatch(SysexMessage[] msgs);

    /**
     * Caluculate check sum of a <code>patch</code>.<p>
     *
     * @param patch a <code>IPatch</code> value
     */
    void calculateChecksum(IPatch patch);

    /**
     * Sends a patch to the specified location on the synth.
     */
    void send(IPatch myPatch, int bankNum, int patchNum);

    /**
     * Request the synth to send a patch dump.
     */
    void requestPatchDump(int bankNum, int patchNum);

    /**
     * Returns true if a Patch Editor Window is implemented.
     *
     * @see #edit
     */
    boolean hasEditor();

    /**
     * Returns a Patch Editor Window for this Patch.
     *
     * @see #hasEditor
     */
    JSLFrame edit(IPatch p);

    /** Send a MidiMessage to the MIDI outport for this driver. */
    void send(MidiMessage msg);

    /**
     * Send a single parameter to the synth.
     *
     * @param patch Patch containing the data.
     * @param param Description of the parameter.
     */
    void sendParameter(IPatch patch, SysexWidget.IParameter param);

    /**
     * Returns full name for referring to this Driver. Used for labels
     * by driver selection comboboxes.
     */
    String toString();
}
