/*
 * DeviceListWriter.java
 *
 */
package org.jsynthlib.core;

import java.util.*;
import java.io.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 * @see DevicesConfig
 */
public final class DeviceListWriter {

    private static boolean verbose = false;

    // don't have to call constructor for Utility class.
    private DeviceListWriter() {
    }

    private static void writeList() throws FileNotFoundException {
        Properties props = new java.util.Properties();

        FileOutputStream out;
            out = new FileOutputStream(Constants.RESOURCE_NAME_DEVICES_CONFIG);
        File synthdevicesDir = new File("synthdrivers");
    	// select only directories
        File[] synthDirs = synthdevicesDir.listFiles(new SynthDirsFilter());

    	// for all subdirectories = synthesizer models
        for (int i = 0; i < synthDirs.length; i++) {
    	    // select *Device.java
            if(verbose)
                System.out.println("In dir " + synthDirs[i].toString() + ":");
    	    File actSynthDir = new File(synthdevicesDir.toString(), synthDirs[i].getName());
    	    String[] synthDevices = actSynthDir.list(new SynthFileFilter());
	        try {
		        MyClassLoader loader = new MyClassLoader(actSynthDir.getPath());
    	    	// for each Device class
	    	    for( int j = 0; j < synthDevices.length; j++) {
	        	    // get Device class name by removing the ".java" from the list of files
	    	        String devName = synthDevices[j].substring(0, synthDevices[j].indexOf('.'));
                    // compute full class name
                    devName = actSynthDir.toString().replace(File.separatorChar, '.') + "." + devName;
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
        System.out.println("Found " + deviceclass.getName());

        Device dev = (Device) deviceclass.newInstance();
        // shortname : delete "Device" at the tail of devName
        String shortname = devName.substring(devName.lastIndexOf('.')+1, devName.lastIndexOf("Device"));

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
    private static class SynthFileFilter implements FilenameFilter {
    	public SynthFileFilter() {
	    }

    	public boolean accept (File dir, String name) {
// 	        return ((name.endsWith ("Driver.class")||name.endsWith("Converter.class"))
// 		        && name.indexOf ('$')==-1);
            if(verbose)
                System.out.println("Checking " + dir.toString() + " & " + name);
            return ((name.indexOf('$') == -1) &&
                    (name.endsWith("Device.java") || name.endsWith("Device.class")));
	    }
    } // SynthFileFilter

    /** FileFilter which select a directory. */
    private static class SynthDirsFilter implements FileFilter {
    	public SynthDirsFilter() {
    	}

    	public boolean accept(File dir) {
	        return (dir.isDirectory());
	    }
    } // class SyntDirsFilter

    /**
     * This class loader uses an alternate directory for loading
     * classes.  When a class is resolved, its class loader is
     * expected to be able to load any additional classes, but this
     * loader doesn't want to have to figure out where to find
     * java.lang.Object, for instance, so it uses Class.forName to
     * locate classes that the system already knows about.<p>
     *
     * Created on 12. September 1999, 00:30
     */
    private static class MyClassLoader extends ClassLoader {
        private String classDir; // root dir to load classes from
        private Hashtable loadedClasses; // Classes that have been loaded

        public MyClassLoader(String classDir) {
            this.classDir = classDir;
            loadedClasses = new Hashtable();
        }

        public synchronized Class loadClass(String className,
                            boolean resolve) throws ClassNotFoundException {
            //System.out.println("loadClass: "+className);
            Class newClass = findLoadedClass(className);
            if (newClass != null)
                return newClass;

            // If the class was in the loadedClasses table, we don't
            // have to load it again, but we better resolve it, just
            // in case.
            newClass = (Class) loadedClasses.get(className);

            if (newClass != null) {
                if (resolve) {  // Should we resolve?
                    resolveClass(newClass);
                }
                return newClass;
            }

            try {
                // Read in the class file
                byte[] classData = getClassData(className);
                // Define the new class
                newClass = defineClass(null, classData, 0, classData.length);
            } catch (IOException readError) {
                // Before we throw an exception, see if the system
                // already knows about this class
                try {
                    newClass = findSystemClass(className);
                    return newClass;
                } catch (Exception any) {
                    throw new ClassNotFoundException(className);
                }
            }

            // Store the class in the table of loaded classes
            loadedClasses.put(className, newClass);

            // If we are supposed to resolve this class, do it
            if (resolve) {
            resolveClass(newClass);
            }

            return newClass;
        }

        // This version of loadClass uses classDir as the root directory
        // for where to look for classes, it then opens up a read stream
        // and reads in the class file as-is.

        protected byte[] getClassData(String className)
            throws IOException {
            // Rather than opening up a FileInputStream directly, we create
            // a File instance first so we can use the length method to
            // determine how big a buffer to allocate for the class

            File classFile = new File(classDir, className + ".class");

            byte[] classData = new byte[(int) classFile.length()];

            // Now open up the input stream
            FileInputStream inFile = new FileInputStream(classFile);

            // Read in the class
            int length = inFile.read(classData);

            inFile.close();

            return classData;
        }
    } // class MyClassLoader

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException if unable to create output file
     */
    public static void main(String[] args) throws FileNotFoundException {
        if(args.length>1 && args[0].equals("-v"))
            verbose  = true;
        DeviceListWriter.writeList();
        System.exit(0);
    }
}
