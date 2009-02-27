/*
 * RolandGP16Device.java
 *
 */

package org.jsynthlib.drivers.roland.gp16;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/**
 * Device class for ROLAND GP16.
 * @author  Mikael Kurula
 * @version $Id$
 */
public class RolandGP16Device extends Device {
    
    private static final String INFO_TEXT=
		"Driver set to provide librarian support for the guitar processor Roland GP-16. The Play "+
		"function performs the same thing as Send,since the GP-16 obviously cannot perform a Play.\n\n"+
		"There are many problems, but also some useful functionality. Please feel free to fix the bugs! :)\n\n"+
		"Drivers originally written by Mikael Kurula, to be reached at alcarola AT gmail DOT com.";
    
	/** Constructor for DeviceListWriter. */
    public RolandGP16Device() {
		super("Roland", "GP16", null, INFO_TEXT, "Mikael Kurula");
    }

	/** Constructor for for actual work. */
    public RolandGP16Device(Preferences prefs) {
		this();
		this.prefs = prefs;
	
		RolandGP16SingleDriver singleDriver = new RolandGP16SingleDriver();
		addDriver(singleDriver);
	    addDriver(new RolandGP16BankDriver(singleDriver));
		addDriver(new RolandGP16GroupDriver(singleDriver));
		addDriver(new RolandGP16AllDriver(singleDriver));
     }
}
