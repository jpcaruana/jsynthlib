/**
 * Constants.java - Central place for definition of constants
 * @author Zellyn Hunter (zellyn@zellyn.com)
 */

package core;

/**
 * @version $Id$
 */
public class Constants {

	/** Devices Config properties file name */
	public static String RESOURCE_NAME_DEVICES_CONFIG =
		"synthdrivers.properties";

	/** App Config properties file name */
	public static String FILE_NAME_APP_CONFIG = "JSynthLib.properties";
	/** App Config file header */
	public static String APP_CONFIG_HEADER = "JSynthLib Saved Properties";

	/** Driver properties name prefix */
	public static String PROP_PREFIX_DEVICE_NAME = "deviceName.";
	/** Device properties class prefix */
	public static String PROP_PREFIX_DEVICE_CLASS = "deviceClass.";
        /** ID properties prefix */
        public static String PROP_PREFIX_ID_STRING = "inquriyID.";
        /** manufacturer name prefix */
        public static String PROP_PREFIX_MANUFACTURER = "manufacturer.";

	/** Number of faders */
	public static int NUM_FADERS = 33;

}
