/**
 * DevicesConfig.java - handles the data-side of figuring out what
 * devices are available.  Not responsible for display.  Reads the
 * device config from a properties file, synthdrivers.properties,
 * expected to be in /synthdrivers.properties in the jar file or
 * elsewhere on the path.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 * @see DeviceListWriter
 */

package core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.prefs.Preferences;

class DevicesConfig {

    /** Properties representing config file, synthdrivers.properties. */
    private Properties configProps = new Properties();

    /**
     * Properties representing devices and class names.
     * Long name -&gt class name.
     */
    private Properties deviceProps = new Properties();
    /**
     * Properties representing the inquery ID Strings.
     * Inquery ID string -&gt class name.
     */
    private Properties inqueryIDProps = new Properties();
    /**
     * Properties representing the short name Strings.
     * Short name string -&gt class name.
     */
    private Properties shortNameProps = new Properties();

    /** ArrayList of device names (long name). */
    private ArrayList deviceNames = new ArrayList();

    /**
     * Constructor
     */
    DevicesConfig() {
	// Load properties file
	InputStream in = this.getClass().getResourceAsStream("/" + Constants.RESOURCE_NAME_DEVICES_CONFIG);
	//Properties configProps = new Properties();
	try {
	    configProps.load(in);
	} catch (IOException e) {
	    ErrorMsg.reportError("Failed Loading Devices",
				 "Failed loading devices property file. "
				 + "Resource " + Constants.RESOURCE_NAME_DEVICES_CONFIG
				 + " cannot be loaded", e);
	} finally {
	    try {
		in.close();
	    } catch (IOException e) {
		// do nothing
	    }
	}

	//configProps.propertyNames()
	Enumeration propNames = configProps.propertyNames();
	while (propNames.hasMoreElements()) {
	    String propName = (String) propNames.nextElement();
	    if (propName.startsWith(Constants.PROP_PREFIX_DEVICE_NAME)) {
		// /deviceName\./ : Kawai K4/K4R Driver
		// /deviceName\./(.*)/
		String shortName = propName.substring(Constants.PROP_PREFIX_DEVICE_NAME.length());
		String deviceName  = configProps.getProperty(propName);
		String deviceClass = configProps.getProperty(Constants.PROP_PREFIX_DEVICE_CLASS + shortName);
		String IDString    = configProps.getProperty(Constants.PROP_PREFIX_ID_STRING + shortName);

		try {
		    deviceNames.add(deviceName);
		    // deviceProps: deviceName -> deviceClass
		    deviceProps.setProperty(deviceName, deviceClass);
		    // inqueryIDProps: inquiry ID String -> deviceName
		    inqueryIDProps.setProperty(IDString, deviceClass);
		    // shortNameProps: short name String -> deviceName
		    shortNameProps.setProperty(shortName, deviceClass);
		} catch (NullPointerException e) {
		    ErrorMsg.reportError("Failed loading Devices",
					 "Config file inconsistency found "
					 + "for '" + shortName + "' device");
		}
		/*
	    } else if (propName.startsWith(Constants.PROP_PREFIX_ID_STRING)) {
		// /inquiryID\./ : ex. F07E**0602400000040000000000f7
		// /inquiryID\./(.*)/
		String shortName = propName.substring(Constants.PROP_PREFIX_ID_STRING.length());
		//String deviceName = configProps.getProperty(Constants.PROP_PREFIX_DEVICE_NAME + shortName);
		String deviceClass = configProps.getProperty(Constants.PROP_PREFIX_DEVICE_CLASS + shortName);
		String IDString = configProps.getProperty(Constants.PROP_PREFIX_ID_STRING + shortName);

		if ((deviceClass != null) && (IDString != null)) {
		    // inqueryIDProps: IDString -> deviceName
		    inqueryIDProps.setProperty(IDString, deviceClass);
		    //deviceNames.add(deviceName);
		} else {
		    ErrorMsg.reportError("Failed loading Devices",
					 "Config file inconsistency found "
					 + "for '" + shortName + "' ID string");
		}
		*/
	    }
	}
	String[][] xmldevices = XMLDeviceFactory.getDeviceNames();
	if (xmldevices != null) {
		for (int i = 0; i < xmldevices.length; i++) {
			String deviceName = xmldevices[i][0];
			String deviceClass = ":" + xmldevices[i][2];
			deviceNames.add(deviceName);
			inqueryIDProps.setProperty(xmldevices[i][1], deviceClass);
			deviceProps.setProperty(deviceName, deviceClass);
			//XXX: just use the name for now
			shortNameProps.setProperty(deviceName, deviceClass);
		}
	}
	Collections.sort(deviceNames);
    }

    /**
     * Return the device names (long name).
     * @return the names of configured devices
     */
    String[] deviceNames() {
	String[] retVal = new String[deviceNames.size()];
	return (String[]) deviceNames.toArray(retVal);
    }

    /**
     * Given a device name, i.e. "Kawai K4/K4R Driver", return its
     * class name.
     * @param deviceName the name of the device
     * @return the class name
     */
    String classNameForDevice(String deviceName) {
	return deviceProps.getProperty(deviceName);
    }

    /*
     * Given a device name, return an instance of its class.
     * @param deviceName the name of the device.
     * @return an instance of the device's class.
     */
    /*
    Device classForDevice(String deviceName) {
	return createDevice(deviceProps.getProperty(deviceName));
    }
    */
        
    Device createDevice(String className, Preferences prefs) {
    		if (className.charAt(0) == ':') {
    			return XMLDeviceFactory.createDevice(className.substring(1),
    					prefs);
    		} else {
    			try {
    				Device device;
    				Class c = Class.forName(className);
    			    Class[] args = { Class.forName("java.util.prefs.Preferences") };
    			    Constructor con = c.getConstructor(args);
    			    device = (Device) con.newInstance(new Object[] { prefs });
    			    return device;
    			} catch (Exception e) {
    			    ErrorMsg.reportError("Failed to create device",
    						 "Failed to create device of class '"
    						 + className + "'");
    			    return null;
    			}
    		}
    }
    
    /**
     * Given a inquery ID String, return its Device.
     * @param IDString inquery ID String
     * @return the Device class
     */
    String classNameForIDString(String IDString) {
	return inqueryIDProps.getProperty(IDString);
    }

    /** Return Enumeration of inquery ID Strings. */
    Enumeration IDStrings() {
	return inqueryIDProps.keys();
    }

    /**
     * Given a short device name, i.e. "KawaiK4", return its class name
     * @param shortName the short name of the device
     * @return the class name
     */
    String classNameForShortName(String shortName) {
	return shortNameProps.getProperty(shortName);
    }

    /**
     * Main method for debugging - print out all configured Devices
     */
    public static void main(String[] args) {
	DevicesConfig devConf = new DevicesConfig();
	devConf.printAll();
    }

    /**
     * Dump out all properties
     */
    private void printAll() {
	for (Iterator i = deviceNames.iterator(); i.hasNext();) {
	    String deviceName = (String) i.next();
	    String deviceClass = classNameForDevice(deviceName);
	    System.out.println(deviceName + ":" + deviceClass);
	}
    }
}
