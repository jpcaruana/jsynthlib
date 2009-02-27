
package synthdrivers.YamahaFS1R;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/**
 * Yamaha FS1R device.
 *
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version    $Id$
 */
public class YamahaFS1RDevice extends Device
{
        private static final String infoText="JSynthLib supports librarian and edit functions on voices/performances patches.\n"+
			"Ensure to edit patches from a bank, not from a library.";

	/**
	 *  Creates new YamahaFS1RDevice
	 */
	public YamahaFS1RDevice()
	{
		super ("Yamaha","FS1R",null,infoText,"Denis Queffeulou");
	}

	/** Constructor for for actual work. */
	public YamahaFS1RDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

//		inquiryID = "F07E**06020F050000000000020AF7";
		addDriver(new YamahaFS1RBankConverter());
		addDriver(new YamahaFS1RBankDriver());
		addDriver(new YamahaFS1RVoiceDriver());
		addDriver(new YamahaFS1RPerformanceDriver());
		addDriver(new YamahaFS1RSystemDriver());
		addDriver(new YamahaFS1RFseqDriver());
	}
}

