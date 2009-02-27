/*
 * GenericDevice.java
 */

package synthdrivers.Generic;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.LookupManufacturer;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;
import org.jsynthlib.core.Utility;

import core.*;

/**
 * A Null Synth Driver.
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class GenericDevice extends Device {
    public GenericDevice() {
	super("Generic", "Unknown", null, null, "Brian Klock");
    }

    public GenericDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver(new GenericDriver());
        addDriver(new IdentityDriver());
    }

    private class GenericDriver extends Driver {
        private GenericDriver() {
            super("-", "Brian Klock");
            patchNumbers = new String[] {"0"};
        }
        
        protected JSLFrame editPatch(Patch p) {
            return (new synthdrivers.Generic.HexDumpEditorFrame(p));
        }
    }

    private class IdentityDriver extends Driver {
        private IdentityDriver() {
            super("Identity", "Joe Emenaker");
            patchNumbers = new String[] {"0"};
            sysexRequestDump = new SysexHandler("F0 7E 7F 06 01 F7");
            sysexID = "F07E**0602"; // Match sysex identity reply messages
        }

        protected JSLFrame editPatch(Patch p) {
            int lengthOfID = LookupManufacturer.lengthOfID(p.sysex,5);
            String manuf = LookupManufacturer.get(p.sysex,5);

            SingleTextAreaFrame f = new SingleTextAreaFrame("Identity Reply Details");
            f.append("MIDI Channel         : " + p.sysex[2] + "\n");
            f.append("Manuf ID             : " + Utility.hexDump(p.sysex, 5, lengthOfID, -1 , true) + " (" + manuf + ")\n");
            f.append("Family (LSB First)   : " + Utility.hexDump(p.sysex, 5 + lengthOfID, 2, -1 , true) + "\n");
            f.append("Product (LSB First)  : " + Utility.hexDump(p.sysex, 7 + lengthOfID, 2, -1 , true) + "\n");
            f.append("Software (LSB First) : " + Utility.hexDump(p.sysex, 9 + lengthOfID, 4, -1 , true) + "\n");
            return(f);
//            return new HexDumpEditorFrame(p);
        }
    }
}
