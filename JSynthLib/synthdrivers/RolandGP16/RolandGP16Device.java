/*
 * RolandGP16Device.java
 *
 */

package synthdrivers.RolandGP16;
import core.Device;
import java.util.prefs.Preferences;

/**
 * Device class for ROLAND GP16.
 * @author  Mikael Kurula
 * @version $Id$
 */
public class RolandGP16Device extends Device {
    
    private static final String INFO_TEXT=
		"Driver set to provide librarian support for the guitar processor Roland GP-16. The Play "+
		"function performs the same thing as Send,since the GP-16 obviously cannot perform a Play.\n\n"+
		"Had to change return new IPatch[] {this}; to new Patch[] {this}; in core/Patch.java/dissect() "+
		"to make it work. The Delete Dups throws an ArrayIndexOutOfBounds Exception. Requesting an All "+
		"patch and receiving a Group patch writes 'requested a Group patch' in the status box. "+
		"Any clue why?\n\n"+
		"Drivers written by Mikael Kurula, released under the Gnu GPL. The librarian seems to work well "+
		"for me, but of course I take no responsibility for lost patches.\n\n"+
		"All comments are appreciated at alcarola@kravbrev.se.";
    
/** Constructor for DeviceListWriter. */
    public RolandGP16Device() {
	super("Roland", "GP16", "", INFO_TEXT, "Mikael Kurula");
    }

/** Constructor for for actual work. */
    public RolandGP16Device(Preferences prefs) {
	this();
	this.prefs = prefs;

	addDriver(new RolandGP16SingleDriver());
        addDriver(new RolandGP16BankDriver());
	addDriver(new RolandGP16GroupDriver());
	addDriver(new RolandGP16AllDriver());
     }
}
