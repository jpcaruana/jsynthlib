/*
 * PC1600Device.java
 */

package synthdrivers.PeaveyPC1600;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author  Phil Shepherd
 * @version $Id$
 */
public class PeaveyPC1600Device extends Device
{
    public PeaveyPC1600Device ()
    {
     super ("Peavey","PC1600",null,null,"Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public PeaveyPC1600Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver (new PeaveyPC1600SingleDriver ());
        addDriver (new PeaveyPC1600BankDriver ());
    }
}
