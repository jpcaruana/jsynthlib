package org.jsynthlib.core;

/**
 * Constants.java - Central place for definition of constants
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @version $Id$
 */
public class Constants {
    /** JSynthLib version number */
    static final String VERSION = "0.21-alpha";

    /** Devices Config properties file name */
    public static final String RESOURCE_NAME_DEVICES_CONFIG =
    "synthdrivers.properties";

    /** App Config properties file name */
    public static final String FILE_NAME_APP_CONFIG = "JSynthLib.properties";
    /** App Config file header */
    public static final String APP_CONFIG_HEADER = "JSynthLib Saved Properties";

    /** Driver properties name prefix */
    public static final String PROP_PREFIX_DEVICE_NAME = "deviceName.";
    /** Device properties class prefix */
    public static final String PROP_PREFIX_DEVICE_CLASS = "deviceClass.";
    /** ID properties prefix */
    public static final String PROP_PREFIX_ID_STRING = "inquiryID.";
    /** manufacturer name prefix */
    public static final String PROP_PREFIX_MANUFACTURER = "manufacturer.";

    /** Number of faders */
    public static final int NUM_FADERS = 33;
}
