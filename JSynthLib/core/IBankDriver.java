package core;

/**
 * This includes methods only for Bank Drivers.
 *
 * @author ribrdb
 * @version $Id$
 * @see IDriver
 * @see IPatchDriver
 */
public interface IBankDriver extends IPatchDriver {
    /** returns the number of patches the bank holds. */
    int getNumPatches();

    /** returns number of columns in bank editor frame. */
    int getNumColumns();

    /** Get the name of the patch at the given number <code>patchNum</code>. */
    String getPatchName(IPatch bankData, int patchNum);

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    void setPatchName(IPatch bankData, int patchNum, String string);

    /** Gets a patch from the bank, converting it as needed. */
    IPatch getPatch(IPatch bankData, int patchNum);

    /**
     * Check a single patch into the bank patch. If the single patch is not for
     * the bank patch, do nothing.
     */
    void checkAndPutPatch(IPatch bankData, IPatch patch, int patchNum);

    /** Delete a patch in the bank. */
    void deletePatch(IPatch bankData, int patchNum);
}
