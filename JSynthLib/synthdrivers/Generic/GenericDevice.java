/*
 * GenericDevice.java
 */

package synthdrivers.Generic;
import java.util.prefs.Preferences;

import core.Device;
import core.Driver;

/**
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class GenericDevice extends Device {
    public GenericDevice() {
	super("Generic", "Device", null, null, "Brian Klock");
    }

    public GenericDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver(new Driver("Sysex", "Brian Klock"));
    }
}
