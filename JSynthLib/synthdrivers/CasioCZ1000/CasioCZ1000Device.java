/*
 * CasioCZ1000PC1600Device.java
 */

package synthdrivers.CasioCZ1000;

import java.util.prefs.Preferences;

import core.Device;


/**
 * Single patch driver for Casio CZ-101 and CZ-1000.
 * Note : on the casio, if you initiate a dump (from the PC or the Casio itself), 
 * you will get a patch of 263 bytes. you CAN'T send that patch back to the Casio... 
 * The patch must be 264 bytes long.  supportsPatch and createPatch fix up short
 * patches.
 * 
 * @author Brian Klock
 * @author Yves Lefebvre
 * @author Bill Zwicky
 * 
 * @version $Id$
 */
public class CasioCZ1000Device extends Device
{
    private static final String INFO_TEXT =
        "Casio CZ-101/1000.  " +
        "  CZ-101 is the base unit; the 1000 is the same, but with" +
        " larger keys.  Single driver works, bank driver not" +
        " tested, editor under construction.  Parts were developed" +
        " variously on 101 and 1000, but should work on both." +
        "  No work done at all for the CZ-5000, though it's" +
        " fundamentally the same.";

    public CasioCZ1000Device ()
    {
        super ("Casio", "CZ-101/1000",
                null, INFO_TEXT,
                "Yves Lefebvre, Bill Zwicky");
    }

    /** Constructor for for actual work. */
    public CasioCZ1000Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        addDriver(new CasioCZ1000SingleDriver());
        addDriver(new CasioCZ1000BankDriver());
    }
}
