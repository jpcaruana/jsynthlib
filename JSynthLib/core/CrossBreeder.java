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
/* XXX: How do we make this work with IPatches instead of Patches?
 *      Does this need to be a method of IPatch or Driver or something?
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

    public void generateNewPatch(PatchBasket library) {
        this.library = library;
        try {
	    Patch father = getRandomPatch();
	    Driver drv = (Driver)father.getDriver();
	    byte[] sysex = new byte[father.sysex.length];
	    p = new Patch(sysex, drv);
	    ErrorMsg.reportStatus("num : " + father.sysex.length + ", " + sysex.length);
	    for (int i = 0; i < father.sysex.length; i++) {
		Patch source;
		// look for a patch with same Driver and enough length
		do {
		    source = getRandomPatch();
		} while (source.getDriver() != drv || source.sysex.length <= i);
		p.sysex[i] = source.sysex[i];
	    }
	    ErrorMsg.reportStatus("patch : " + father + ", " + p);
	    p.calculateChecksum();
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Internal Error", e);
	}
    }

    public IPatch getCurrentPatch() {
	return p;
    }

    private Patch getRandomPatch() {
	int num = (int) (Math.random() * library.getPatchCollection().size());
	return (Patch) (library.getPatchCollection().get(num));
    }
}
