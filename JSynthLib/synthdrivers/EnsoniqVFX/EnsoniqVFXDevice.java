
package synthdrivers.EnsoniqVFX;

import core.*;
import java.util.prefs.Preferences;

/**
 * Ensoniq VFX device. Copied from the ESQ1.
 * My VFX version seems to be 2.10 (02 0A in the inquiry result ID message).
 * Unfortunately, my documentation is not up to date so if this driver does not work
 * with yours, check the sysex size first and then the message types.
 * I can't deal with several version of VFX because I have only one. This could be
 * made with first getting the version and then choosing the good parameters in a
 * table with the version as the key.
 *
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 * @created    17 septembre 2002
 * @version    $Id$
 */
public class EnsoniqVFXDevice extends Device
{
        private static final String infoText="JSynthLib supports librarian functions on VFX single/bank/multi patches.\n"+
	         "This driver has been tested with VFX 2.10 version,  "+
	         "older versions could not work if different sysex length are used.\n"+
		 "The patch store send the patch in the edit buffer.";

	/**
	 *  Creates new EnsoniqVFXDevice
	 */
	public EnsoniqVFXDevice()
	{
		// inquiryID:
		// i fix the required major version to 2
		// i let the minor version number empty
		super ("Ensoniq","VFX","F07E..06020F05000000000002..F7",infoText,"Denis Queffeulou");
	}

	/** Constructor for for actual work. */
	public EnsoniqVFXDevice(Preferences prefs) {
		this();
		this.prefs = prefs;

		//setSynthName("VFX");
		addDriver(new EnsoniqVFXBankDriver());
		addDriver(new EnsoniqVFXSingleDriver());
		addDriver(new EnsoniqVFXMultiDriver());
	}
}

