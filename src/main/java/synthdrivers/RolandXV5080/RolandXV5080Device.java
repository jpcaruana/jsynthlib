/*
 * RolandXV5080Device.java
 */

package synthdrivers.RolandXV5080;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
/**
 *
 * @author  Phil Shepherd
 * @version $Id$
 */
public class RolandXV5080Device extends Device
{
    public RolandXV5080Device ()
    {
     super ("Roland","XV5080",null,null,"Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public RolandXV5080Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver (new RolandXV5080PatchDriver ());
        addDriver (new RolandXV5080PatchBankDriver ());
        addDriver (new RolandXV5080PerfDriver ());
        addDriver (new RolandXV5080PerfBankDriver ());
    }
}
