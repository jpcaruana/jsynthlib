package core;

/**
 * This includes methods only for Bank Drivers.
 * 
 * @author ribrdb
 */
public interface IBankDriver extends IPatchDriver {
    /** returns the number of patches the bank holds.. */
    public abstract int getNumPatches();

    /** returns number of columns in bank editor frame. */
    public abstract int getNumColumns();

    /**
     * Check a patch if it is for the bank patch and put it into the
     * bank.
     */
    public abstract void checkAndPutPatch(IPatch bankData, IPatch p,
            int patchNum);

    /** Delete a patch. */
    public abstract void deletePatch(IPatch bankData, int patchNum);

    /** Gets a patch from the bank, converting it as needed. */
    public abstract IPatch getPatch(IPatch bankData, int patchNum);

    /** Get the name of the patch at the given number <code>patchNum</code>. */
    public abstract String getPatchName(IPatch bankData, int patchNum);

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    public abstract void setPatchName(IPatch bankData, int patchNum,
            String string);
}