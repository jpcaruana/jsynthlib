/*
 * CasioCZ1000PC1600Device.java
 */

package synthdrivers.CasioCZ1000;

import core.*;
import java.util.prefs.Preferences;
/**
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class CasioCZ1000Device extends Device
{
    public CasioCZ1000Device ()
    {
	super ("Casio","CZ1000",null,null,"Yves Lefebvre");;
    }

    /** Constructor for for actual work. */
    public CasioCZ1000Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("CZ");
        addDriver (new CasioCZ1000SingleDriver ());
        addDriver (new CasioCZ1000BankDriver ());
    }
}
