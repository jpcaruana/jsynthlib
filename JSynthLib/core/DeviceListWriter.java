/*
 * DeviceListWriter.java
 *
 */
package core;

import java.util.*;
import java.io.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 * @see DevicesConfig
 */
public class DeviceListWriter {

    private static void writeList() {
        Properties props = new java.util.Properties();

        FileOutputStream out;
        try {
            out = new FileOutputStream(Constants.RESOURCE_NAME_DEVICES_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        File synthdevicesDir = new File("synthdrivers");
	// select only directories
        File[] synthDirs = synthdevicesDir.listFiles(new SynthDirsFilter());

	// for all subdirectories = synthesizer models
        for (int i = 0; i < synthDirs.length; i++) {
	    // select *Device.class
	    File actSynthDir = new File("synthdrivers", synthDirs[i].getName());
	    String[] synthDevices = actSynthDir.list(new SynthFileFilter());
	    try {
		MyClassLoader loader = new MyClassLoader(actSynthDir.getPath());
		// for each Device class
		for (int j = 0; j < synthDevices.length; j++) {
		    // get Device class name by removing the ".class" from the list of files
		    String devName = synthDevices[j].substring(0, synthDevices[j].indexOf('.'));
		    try {
			setProperty(loader, props, devName);
		    } catch (Exception e) {
			ErrorMsg.reportStatus(e);
			ErrorMsg.reportStatus("Exception with " + devName);
		    }
		}
	    } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	    }
	}
	// save into synthdrivers.properties
        try {
            props.store(out, "Generated devicesfile");
            out.close();
            System.out.println("Done!");
        } catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
    }

    private static void setProperty(MyClassLoader loader, Properties props, String devName)
	throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	Class deviceclass = loader.loadClass(devName, true);
	System.out.println(deviceclass);

	Device dev = (Device) deviceclass.newInstance();
	// shortname : delete "Device" at the tail of devName
	String shortname = devName.substring(0, devName.lastIndexOf("Device"));

	props.setProperty(Constants.PROP_PREFIX_DEVICE_CLASS + shortname,
			  deviceclass.getName());
	props.setProperty(Constants.PROP_PREFIX_MANUFACTURER + shortname,
			  dev.getManufacturerName());
	props.setProperty(Constants.PROP_PREFIX_ID_STRING + shortname,
			  dev.getInquiryID());
	// saving only model name is better.
	props.setProperty(Constants.PROP_PREFIX_DEVICE_NAME + shortname,
			  dev.getManufacturerName() + " " + dev.getModelName() + " Driver");
    }

    /** FilenameFilter which select <code>*Device.class</code>. */
    static class SynthFileFilter implements FilenameFilter {
	public SynthFileFilter() {
	}

	public boolean accept (File dir, String name) {
// 	    return ((name.endsWith ("Driver.class")||name.endsWith("Converter.class"))
// 		    && name.indexOf ('$')==-1);
	    return ((name.endsWith("Device.class")) && (name.indexOf('$') == -1));
	}
    }

    /** FileFilter which select a directory. */
    static class SynthDirsFilter implements FileFilter {
	public SynthDirsFilter() {
	}

	public boolean accept(File dir) {
	    return (dir.isDirectory());
	}
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DeviceListWriter.writeList();
        System.exit(0);
    }
}
