package core;

/**
 * This includes methods only for Bank Drivers.
 * 
 * @author ribrdb
 */
public interface IBankDriver extends IPatchDriver{
    /*
     * @deprecated Use BankDriver(String, String, int, int).
     */public abstract int getNumPatches();

    /** Getter for property <code>numColumns</code>. */
    public abstract int getNumColumns();

    // stub methods to convert IPatch -> Patch.
    public abstract void checkAndPutPatch(IPatch bankData, IPatch p,
            int selectedPatchNum);

    public abstract void deletePatch(IPatch bankData, int selectedPatchNum);

    public abstract IPatch getPatch(IPatch bankData, int selectedPatchNum);

    public abstract String getPatchName(IPatch bankData, int i);

    public abstract void setPatchName(IPatch bankData, int patchNum,
            String string);
}