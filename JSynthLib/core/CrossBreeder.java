package core;
/*
 * As of version 0.14 the actual functionality of the crossbreeder
 * dialog is hidden away in this file. It seems like a good idea to be
 * seperating functionality from GUI code, something I didn't do when
 * I first started JSynthLib.
 */
/**
 * @author bklock
 * @version $Id$
 */
public class CrossBreeder {
    /** The patch we are working on. */
    Patch p;
    /** The patch library we are working on. */
    PatchBasket library;

    public void generateNewPatch() {
	try {
	    Patch father = getRandomPatch();
	    Patch source;
	    byte[] sysex = new byte[father.sysex.length];
	    p = new Patch(sysex);
	    for (int i = 0; i < father.sysex.length; i++) {
		// look for a patch with same Driver and enough length
		do {
		    source = getRandomPatch();
		} while (source.driverNum != father.driverNum
			 || source.sysex.length < i
			 || source.deviceNum != father.deviceNum);
		p.sysex[i] = source.sysex[i];
	    }
	    p.driverNum = father.driverNum;
	    p.deviceNum = father.deviceNum;
	    p.getDriver().calculateChecksum(p);
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Source Library Must be Focused", e);
	}
    }
    public Patch getCurrentPatch() {
	return p;
    }

    public void workFromLibrary (PatchBasket lib) {
	library = lib;
    }

    public Patch getRandomPatch() {
	int num = (int) (Math.random() * library.getPatchCollection().size());
	return (Patch) (library.getPatchCollection().get(num));
    }
}
