package core;
import javax.swing.JOptionPane;

/** This is the base class for all Bank / Bulk Drivers. */
public class BankDriver extends Driver {
    /**
     * The Number of Patches the Bank holds.
     * @deprecated Use the getter method.
     */
    // can be private final
    protected int numPatches;
    /**
     * How many columns to use when displaying the patches as a table.
     * @deprecated Use the getter method.
     */
    // can be private final
    protected int numColumns;

    // for default canHoldPatch
    /**
     * The Sysex header for the patches which go in this bank.  This
     * should be same value as the <code>sysexID</code> field of the
     * single driver.  It can be up to 16 bytes and have wildcards
     * (<code>*</code>).  (ex. <code>"F041**003F12"</code>)
     * @see Driver#sysexID
     * @see #canHoldPatch
     */
    // This can be "private static final".
    protected String singleSysexID;
    /**
     * The size of the patches which go in this bank.
     * @see #canHoldPatch
     */
    // This can be "private static final".
    protected int singleSize;

    /**
     * Creates a new <code>BankDriver</code> instance.
     *
     * @param patchType The patch type. eg. "Bank", "Multi Bank",
     * "Drum Bank", etc.
     * @param authors The names of the authors of this driver.
     * @param numPatches The Number of Patches the Bank holds.
     * @param numColumns How many columns to use when displaying the
     * patches as a table.
     */
    public BankDriver(String patchType, String authors,
		      int numPatches, int numColumns) {
	super(patchType, authors);
	this.numPatches = numPatches;
	this.numColumns = numColumns;
    }

    /**
     * @deprecated Use BankDriver(String, String, int, int).
     */
    public BankDriver() {
	super();
    }

    /** Getter for property <code>numPatches</code>. */
    protected int getNumPatches() {
	return numPatches;
    }
    /** Getter for property <code>numColumns</code>. */
    protected int getNumColumns() {
	return numColumns;
    }

    /** Set name of the bank. */
    public void setPatchName(Patch p, String name) {
	// Most Banks have no name.
    }

    /** Get name of the bank. */
    public String getPatchName(Patch p) {
	// Most Banks have no name.
	return "-";
    }

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    protected void setPatchName(Patch p, int patchNum, String name) {
	// override this.
    }

    /** Get the name of the patch at the given number <code>patchNum</code>. */
    protected String getPatchName(Patch p, int patchNum) {
	// override this.
	return "-";
    }

    /** Delete a patch. */
    protected void deletePatch(Patch p, int patchNum) {
	setPatchName(p, patchNum, "          ");
    }

    /**
     * Puts a patch into the bank, converting it as
     * needed. <code>p</code> is already checked by
     * <code>canHoldPatch</code>, although it was not.
     */
    protected void putPatch(Patch bank, Patch p, int patchNum) {
    }

    /**
     * Check a patch by using <code>canHoldPatch()</code> and put it
     * into the bank.
     * @see #putPatch
     * @see #canHoldPatch
     */
    void checkAndPutPatch(Patch bank, Patch p, int patchNum) {
	if (canHoldPatch(p)) {
	    putPatch(bank, p, patchNum);
	} else {
	    JOptionPane.showMessageDialog
		(null,
		 "This type of patch does not fit in to this type of bank.",
		 "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
    }

    /** Gets a patch from the bank, converting it as needed. */
    public Patch getPatch(Patch bank, int patchNum) { // called by YamahaFS1RBankEditor
	return null;
    }

    /**
     * Store the bank to a given bank on the synth. Ignores the
     * patchNum parameter. Should probably be overridden in most
     * drivers
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
	setBankNum(bankNum);
	super.sendPatch(p);
    }

    /** Show an error dialog. */
    // Cannot we disable the menu for this?
    protected void sendPatch(Patch p) {
	JOptionPane.showMessageDialog
	    (null, "You can not send bank data (use store).",
	     "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Chooses which bank to put the patch into and stores the patch
     * in it .
     * @deprecated Nobody uses this method now.
     */
    protected void choosePatch(Patch p) {
	int bank = 0;
	if (bankNumbers.length > 1) {
	    try {
		String bankstr = (String) JOptionPane.showInputDialog
		    (null, "Please Choose a Bank", "Storing Patch",
		     JOptionPane.QUESTION_MESSAGE, null,
		     bankNumbers, bankNumbers[0]);
		if (bankstr == null) // cancel
		    return;

		for (bank = 0; bank < bankNumbers.length; bank++)
		    if (bankstr.equals(bankNumbers[bank]))
			break;
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	    }
	    ErrorMsg.reportStatus("BankDriver:ChoosePatch  Bank = " + bank);
	}
	storePatch(p, bank, 0);
    }

    /** Banks cannot play. */
    // Cannot we disable the menu for this?
    public void playPatch(Patch p) {
	JOptionPane.showMessageDialog
	    (null, "Can not Play Banks, only individual patches.",
	     "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Creates an editor window to edit this bank. */
    protected JSLFrame editPatch(Patch p) {
	return new BankEditorFrame(p);
    }

    /**
     * Compares the header & size of a Single Patch to this driver to
     * see if this bank can hold the patch.
     */
    // cf. Driver.supportsPatch
    protected boolean canHoldPatch(Patch p) {
        if ((singleSize != p.sysex.length) && (singleSize != 0))
	    return false;

        String patchString = p.getPatchHeader().toString();
        StringBuffer driverString = new StringBuffer(singleSysexID);
	for (int j = 0; j < driverString.length(); j++)
	    if (driverString.charAt(j) == '*')
		driverString.setCharAt(j, patchString.charAt(j));
	return (driverString.toString().equalsIgnoreCase
		(patchString.substring(0, driverString.length())));
    }
}
