package core;

/**
 * This includes methods shared by ISingleDriver and IBankDriver which are
 * used by LibraryFrame, etc.
 * 
 * @author ribrdb
 */
public interface IPatchDriver extends IDriver{
    
    /** Getter for property <code>patchSize</code>. */
    public abstract int getPatchSize();

    /** Setter for property <code>inPort</code>. */
    public abstract int getInPort();

    /** Getter for property <code>device.manufacturerName</code>. */
    public abstract String getManufacturerName();

    /** Getter for property <code>device.modelName</code>. */
    public abstract String getModelName();

    /** Getter for property <code>device.channel</code>. */
    public abstract int getChannel();

    /**
     * Check if this driver supports creating a new patch.
     * By default it uses reflection to test if the method createNewPatch
     * is declared in the subclass of Driver.
     * @return
     */
    public abstract boolean canCreatePatch();

    /**
     * Create a new Patch.
     */
    public abstract Patch createNewPatch();

    /**
     * Returns true if an Editor is implemented.
     * @see #editPatch
     */
    public abstract boolean hasEditor();

    //----- Start phil@muqus.com
    public abstract void requestPatchDump(int bankNum, int patchNum);

    /** Send Sysex byte array data to MIDI outport. */
    public abstract void send(byte[] sysex);

    /**
     * Returns full name for referring to this Driver for
     * debugging purposes
     */
    public abstract String toString();

    /**
     * Returns String[] returns full list of patchNumbers
     */
    public abstract String[] getPatchNumbers();

    /**
     * Returns String[] list of patch numbers for writable patches.
     * (This has nothing to with the "Storable" class in JSynthLib.)
     */
    public abstract String[] getPatchNumbersForStore();

    /**
     * Returns String[] returns full list of bankNumbers
     */
    public abstract String[] getBankNumbers();

    // stub methods to convert IPatch -> Patch.
    public abstract void calculateChecksum(IPatch myPatch);

    /**
     * Returns an Editor Window for this Patch.
     * @see #hasEditor
     */
    public abstract JSLFrame editPatch(IPatch p);

    /**
     * Sends a patch to a set location on a synth.<p>
     * Override this if required.
     */
    public abstract void storePatch(IPatch myPatch, int bankNum, int patchNum);

    /**
     * This method trims a patch, containing more than one real
     * patch to a correct size. Useful for files containg more than one
     * bank for example. 
     * @param p the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     * 
     * XXX: I think we can remove this once we have a propper patch factory.
     */
    public abstract void trimSysex(IPatch pk);
    
    public abstract IPatch createPatch(byte[] sysex);
    //public abstract IPatch createPatch(SysexMessage[] msgs);
}