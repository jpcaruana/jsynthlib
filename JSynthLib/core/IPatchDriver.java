package core;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

/**
 * Interface for methods for both single drivers and bank drivers.
 *
 * @author ribrdb
 * @version $Id$
 * @see IDriver
 */
public interface IPatchDriver extends IDriver {
    /**
     * Return the size of the patch which the driver handles.
     * <code>SysexGetDialog</code> uses this to estimate timeout value.
     * @see SysexGetDialog
     */
    int getPatchSize();

    /**
     * Returns String[] returns full list of patchNumbers.
     * @see DriverUtil#generateNumbers
     */
    String[] getPatchNumbers();

    /**
     * Returns String[] list of patch numbers for writable patches.
     * This can be overridden if some patch locations are read only.
     * e.g. the Waldorf Pulse has 100 patches, but only 0 to 39 are writable.
     * Currently writable patches are assumed to start at patch location 0.
     * (This has nothing to with the "Storable" class in JSynthLib.)
     * @see DriverUtil#generateNumbers
     */
    String[] getPatchNumbersForStore();

    /**
     * Returns String[] returns full list of bankNumbers.
     * @see DriverUtil#generateNumbers
     */
    String[] getBankNumbers();

    /**
     * Check if this driver supports (implements createPatch()) creating a new
     * patch.
     * @see #createPatch()
     */
    boolean canCreatePatch();

    /**
     * Create a new Patch for this driver.
     */
    IPatch createPatch();

    /**
     * Create a patch from a byte array for the driver. This must be called only
     * when <code>IDriver.supportsPatch()</code> returns <code>true</code>.
     * 
     * @param sysex a byte array of sysex data.
     * @return a array of <code>IPatch</code> object.
     * @see IDriver#supportsPatch(String, byte[])
     * @see DriverUtil#createPatch(byte[])
     */
    IPatch createPatch(byte[] sysex);

    /**
     * Create an array of patches from an array of SysexMessage for the driver.
     * Returns an array of patches because Converter may be used. This is used
     * for SysexMessages received by using <code>requestPatchDump(int, int)</code>.
     * 
     * @param msgs an array of SysexMessage.
     * @return an array of <code>IPatch</code> value.
     * @see #requestPatchDump(int, int)
     * @see SysexGetDialog
     */
    IPatch[] createPatches(SysexMessage[] msgs);

    /**
     * Request the synth to send a patch dump.
     */
    void requestPatchDump(int bankNum, int patchNum);

    /**
     * Send a MidiMessage to the MIDI outport for this driver.
     */
    void send(MidiMessage msg);

    /**
     * Returns full name for referring to this Driver. Used for labels
     * by driver selection comboboxes.
     */
    String toString();
}
