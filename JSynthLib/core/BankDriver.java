package core;
import javax.swing.JOptionPane;

/**
 * This is an implementation of IBankDriver and the base class for bank
 * drivers which use <code>Patch<IPatch>.<p>
 */
abstract public class BankDriver extends Driver implements IPatchDriver {
     /**
     * The Number of Patches the Bank holds.
     */
    private final int numPatches;
    /**
     * How many columns to use when displaying the patches as a table.
     */
    private final int numColumns;

    // for default canHoldPatch
    /**
     * The Sysex header for the patches which go in this bank.  This
     * should be same value as the <code>sysexID</code> field of the
     * single driver.  It can be up to 16 bytes and have wildcards
     * (<code>*</code>).  (ex. <code>"F041.*003F12"</code>)
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
    //
    // IDriver interface methods
    //
    public final boolean isSingleDriver() {
        return false;
    }

    public final boolean isBankDriver() {
        return true;
    }

    public final boolean isConverter() {
        return false;
    }
    // end of IDriver methods

    //
    // IPatchDriver interface methods
    //
    /**
     * Store the bank to a given bank on the synth. Ignores the
     * patchNum parameter. Should probably be overridden in most
     * drivers
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch bank, int bankNum, int patchNum) {
	setBankNum(bankNum);
	super.sendPatch(bank);
    }
    /**
     * @see Patch#hasEditor()
     */
    public boolean hasEditor() {
        return true;
    }

    /**
     * Creates a default bank editor window to edit this bank.
     * @see Patch#edit()
     */
    protected JSLFrame editPatch(Patch bank) {
	return new BankEditorFrame(bank);
    }
    //
    // for IPatch interface methods
    //
    /**
     * Get name of the bank.
     * @see Patch#getName()
     */
    public String getPatchName(Patch bank) {
	// Most Banks have no name.
	return "-";
    }

    /**
     * Set name of the bank.
     * @see Patch#setName(String)
     */
    public void setPatchName(Patch bank, String name) {
	// Most Banks have no name.
    }
    // end of IPatch interface methods
    //
    // for IBankPatch interface methods
    //
    /**
     * @see Patch#getNumPatches()
     */
    public final int getNumPatches() {
	return numPatches;
    }

    /**
     * @see Patch#getNumColumns()
     */
    public final int getNumColumns() {
	return numColumns;
    }

    /**
     * Compares the header & size of a Single Patch to this driver to
     * see if this bank can hold the patch.
     * @see Patch#put(IPatch, int)
     * @see Driver#supportsPatch
     */
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

    /**
     * Puts a patch into the bank, converting it as
     * needed. <code>single</code> is already checked by
     * <code>canHoldPatch</code>, although it was not.
     * @see Patch#put(IPatch, int)
     */
    abstract protected void putPatch(Patch bank, Patch single, int patchNum);

    /** 
     * Delete a patch.
     * @see Patch#delete(int)
     */
    protected void deletePatch(Patch single, int patchNum) {
	setPatchName(single, patchNum, "          ");
    }

    /**
     * Gets a patch from the bank, converting it as needed.
     * @see Patch#get(int)
     */
    abstract protected Patch getPatch(Patch bank, int patchNum);

    /**
     * Get the name of the patch at the given number <code>patchNum</code>.
     * @see Patch#getName(int)
     */
    abstract protected String getPatchName(Patch bank, int patchNum);

    /**
     * Set the name of the patch at the given number <code>patchNum</code>.
     * @see Patch#setName(int, String)
     */
    abstract protected void setPatchName(Patch bank, int patchNum, String name);
    // end of IBankDriver methods

    //
    // remove the following lines after 0.20 is released.
    //
    /*
     * @deprecated Use BankDriver(String, String, int, int).
     */
    /*
    public BankDriver() {
	super();
    }
    */
    /**
     * This is never called.  Don't have to be implemented.
     * @deprecated This method is unnecesarry for bank driver.
     */
    protected void sendPatch(Patch bank) {  // not used
	JOptionPane.showMessageDialog
	    (null, "You can not send bank data (use store).",
	     "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Banks cannot play.
     * @deprecated This is never called.
     */
    protected void playPatch(Patch bank) { // not used
	JOptionPane.showMessageDialog
	    (null, "Can not Play Banks, only individual patches.",
	     "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Chooses which bank to put the patch into and stores the patch
     * in it .
     * @deprecated Don't use this.
     */
    /*
    protected void choosePatch(Patch p) { // not used
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
    */
}
