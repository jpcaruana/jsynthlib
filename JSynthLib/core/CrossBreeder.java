package core;
/*
 * Generates a patch with random combinations of the patches for a
 * driver in a library.
 *
 * As of version 0.14 the actual functionality of the crossbreeder
 * dialog is hidden away in this file. It seems like a good idea to be
 * seperating functionality from GUI code, something I didn't do when
 * I first started JSynthLib.
 */
/**
 * @author bklock
 * @version $Id$
 * @see CrossBreedDialog
 */
public class CrossBreeder {
    /** The patch we are working on. */
    private Patch p;
    /** The patch library we are working on. */
    private PatchBasket library;

    // Why this is not 'Patch generateNewPatch(PatchBasket lib)'?
    public void generateNewPatch() {
	try {
	    Patch father = getRandomPatch();
	    Driver drv = father.getDriver();
	    byte[] sysex = new byte[father.sysex.length];
	    p = new Patch(sysex, drv);
	    ErrorMsg.reportStatus("num : " + father.sysex.length + ", " + sysex.length);
	    for (int i = 0; i < father.sysex.length; i++) {
		Patch source;
		// look for a patch with same Driver and enough length
		do {
		    source = getRandomPatch();
		} while (source.getDriver() != drv
			 || source.sysex.length <= i);
		p.sysex[i] = source.sysex[i];
	    }
	    ErrorMsg.reportStatus("patch : " + father + ", " + p);
	    p.getDriver().calculateChecksum(p);
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Internal Error", e);
	}
    }
    public Patch getCurrentPatch() {
	return p;
    }

    public void workFromLibrary (PatchBasket lib) {
	library = lib;
    }

    private Patch getRandomPatch() {
	int num = (int) (Math.random() * library.getPatchCollection().size());
	return (Patch) (library.getPatchCollection().get(num));
    }
}
