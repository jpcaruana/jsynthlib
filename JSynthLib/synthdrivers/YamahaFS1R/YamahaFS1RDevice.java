
package synthdrivers.YamahaFS1R;

import core.*;

/**
 * Yamaha FS1R device. 
 *
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 */
public class YamahaFS1RDevice extends Device
{

	/**
	 *  Creates new YamahaFS1RDevice
	 */
	public YamahaFS1RDevice()
	{
		manufacturerName = "Yamaha";
		modelName = "FS1R";
		synthName = "FS1R";
//		inquiryID = "F07E**06020F050000000000020AF7";
		addDriver(new YamahaFS1RBankConverter());    
		addDriver(new YamahaFS1RBankDriver());
		addDriver(new YamahaFS1RVoiceDriver());
		addDriver(new YamahaFS1RPerformanceDriver());
		addDriver(new YamahaFS1RSystemDriver());
		addDriver(new YamahaFS1RFseqDriver());
        infoText="JSynthLib supports librarian and edit functions on voices/performances patches.\n"+
			"Ensure to edit patches from a bank, not from a library.";
	}

}

