/**
 * DevicesConfig.java - handles the data-side of figuring out what
 * devices are available.  Not responsible for display.  Reads the
 * device config from a properties file, synthdrivers.properties,
 * expected to be in /synthdrivers.properties in the jar file or
 * elsewhere on the path.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */

package core;

import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;

public class DevicesConfig {

	/* Properties representing config file */
	private Properties configProps = new Properties();
	/* Properties representing devices and class names */
	private Properties deviceProps = new Properties();
	/* ArrayList of device names */
	private ArrayList deviceNames = new ArrayList();
        /* Properties representing the ID-Strings and device names */
        private Properties deviceIDProps = new Properties();

	/**
	 * Main method for debugging - print out all configured Devices
	 */
	public static void main(String args[]) {
		DevicesConfig devConf = new DevicesConfig();
		devConf.printAll();
	}

	/**
	 * Constructor
	 */
	public DevicesConfig() {

		// Load properties file
		InputStream in = this.getClass().getResourceAsStream("/" + Constants.RESOURCE_NAME_DEVICES_CONFIG);
		//Properties configProps = new Properties();
		try {
			configProps.load(in);
		}
		catch (IOException e) {
			ErrorMsg.reportError("Failed Loading Devices",
								 "Failed loading devices property file. "
								 + "Resource " + Constants.RESOURCE_NAME_DEVICES_CONFIG
								 + " cannot be loaded", e);
		}
		finally {
			try {
				in.close();
			}
			catch (IOException e) {
				// do nothing
			}
		}

		//configProps.propertyNames()
		Enumeration propNames = configProps.propertyNames();
		while (propNames.hasMoreElements())
		{
			String propName = (String)propNames.nextElement();
			if (propName.startsWith(Constants.PROP_PREFIX_DEVICE_NAME)) {
				String deviceKey = propName.substring(
					Constants.PROP_PREFIX_DEVICE_NAME.length());
				String deviceName = configProps.getProperty(propName);
				String deviceClass = configProps.getProperty(
					Constants.PROP_PREFIX_DEVICE_CLASS + deviceKey);

				if ((deviceName==null) || (deviceClass==null)) {
					ErrorMsg.reportError("Failed loading Devices",
										 "Config file inconsistency found "
										 + "for '" + deviceKey + "' device");
				}
				else {
					deviceProps.setProperty(deviceName, deviceClass);
					this.deviceNames.add(deviceName);
				}
			}
                        if (propName.startsWith(Constants.PROP_PREFIX_ID_STRING)) {
				String deviceKey = propName.substring(
					Constants.PROP_PREFIX_ID_STRING.length());
				String deviceName = configProps.getProperty(Constants.PROP_PREFIX_DEVICE_NAME+deviceKey);
				String IDString = configProps.getProperty(
					Constants.PROP_PREFIX_ID_STRING + deviceKey);

				if ((deviceName==null) || (IDString==null)) {
					ErrorMsg.reportError("Failed loading Devices",
										 "Config file inconsistency found "
										 + "for '" + deviceKey + "' ID string");
				}
				else {
					deviceIDProps.setProperty(IDString,deviceName);
					//this.deviceNames.add(deviceName);
				}
			}
		}
		Collections.sort(this.deviceNames);
	}

	/**
	 * Return the device names
	 * @return the names of configured devices
	 */
	public String[] deviceNames() {
		String[] retVal = new String[this.deviceNames.size()];
		return (String[])this.deviceNames.toArray(retVal);
	}

        public Enumeration IDStrings() {
            return deviceIDProps.keys();
        }
	/**
	 * Given a device name, return its class name
	 * @param deviceName the name of the device
	 * @return the class name
	 */
	public String classNameForDevice(String deviceName) {
		return this.deviceProps.getProperty(deviceName);
	}
        
        public Device classForIDString(String IDString)
        {
            Device device=null;
            String deviceName=this.deviceIDProps.getProperty(IDString);
           return this.classForDevice(deviceName);
	}   

	/**
	 * Given a device name, return an instance of its class
	 * @param deviceName the name of the device
	 * @return an instance of the device's class
	 */
	public Device classForDevice(String deviceName) {
		Device device = null;
		String className = this.deviceProps.getProperty(deviceName);
		try {
			Class deviceClass = Class.forName(className);
			device = (Device)deviceClass.newInstance();
		}
		catch (Exception e) {
			ErrorMsg.reportError("Failed to find class for device",
								 "Failed to find class for device '"
								 + deviceName + "'");
		}
		return device;
	}

	/**
	 * Dump out all properties
	 */
	public void printAll() {
		for (Iterator i = this.deviceNames.iterator(); i.hasNext();)
		{
			String deviceName = (String)i.next();
			String deviceClass = this.deviceProps.getProperty(deviceName);
			System.out.println(deviceName + ":" + deviceClass);
		}
	}

}
