/*
 * MT32Device.java
 *
 */

package synthdrivers.RolandMT32;
import core.Device;
import java.util.prefs.Preferences;

/**
 * Device class for Roland MT32.
 * @author  Fred Jan Kraan
 * @version $Id$
 */
public class RolandMT32Device extends Device {
    private static final String INFO_TEXT
    = "The driver for this synthesiser is created with an absolute minimal knowledge " +
      "of the JSynthLib architecture. Only the SingleDriver is implemented, and not " +
      "even complete.\n" +
      "Bugs:\n" +
      "- The driver can retrieve all eight Timbre Temp Areas, but all changes are applied " + 
      "to the Timbre Temp Area 1.\n" +
      "- Save and send do not work.";
      

    /** Constructor for DeviceListWriter. */
    public RolandMT32Device() {
	super("Roland", "MT32", "F041**16",
	      INFO_TEXT, "Fred Jan Kraan");
    }

    /** Constructor for for actual work. */
    public RolandMT32Device(Preferences prefs) {
	this();
	this.prefs = prefs;

//        addDriver(new KawaiK4BulkConverter());
        addDriver(new RolandMT32SingleDriver());
//        addDriver(new KawaiK4BankDriver());
//        addDriver(new KawaiK4MultiDriver());
//        addDriver(new KawaiK4MultiBankDriver());
//        addDriver(new KawaiK4EffectDriver());
//        addDriver(new KawaiK4EffectBankDriver());
//        addDriver(new KawaiK4DrumsetDriver());
    }
}
