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
 */
public class DeviceListWriter {
    
    public static void  writeList() {
        Enumeration enum;
        Vector synthis;
        Properties props;
        
        props=new java.util.Properties();
        FileOutputStream out;
        try {
            out=new FileOutputStream(Constants.RESOURCE_NAME_DEVICES_CONFIG);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MyClassLoader loader;
        File synthdevicesDir =new File("synthdrivers");
        File synthDirs[]=synthdevicesDir.listFiles(new SynthDirsFilter());
        synthis=new Vector();
        
        for (int i=0;i<synthDirs.length ;i++) // for all subdirectories = synthesizer models
        {
            File actSynthDir= new File("synthdrivers",synthDirs[i].getName());
            String synthDevices[]=actSynthDir.list(new SynthFileFilter());
            try {
                loader=new MyClassLoader(actSynthDir.getPath());
                for (int j=0;j<synthDevices.length ;j++) {
                    // Remove the ".class" from the list of files
                    synthDevices[j]=synthDevices[j].substring(0,synthDevices[j].indexOf('.'));
                    try {
                        
                        Class deviceclass=loader.loadClass(synthDevices[j],true);
                        System.out.println(deviceclass);
                        Device dev=(Device)deviceclass.newInstance();
                        
                        String shortname=synthDevices[j].substring(0,synthDevices[j].lastIndexOf("Device"));
                        
                        String manufacturer= dev.getManufacturerName();
          //              System.out.println("Field:Manufacturer for "+synthDevices[j]+" "+manufacturer);
                        props.setProperty(Constants.PROP_PREFIX_MANUFACTURER+shortname, dev.getManufacturerName());
                        props.setProperty(Constants.PROP_PREFIX_ID_STRING+shortname, dev.getInquiryID());
                        props.setProperty(Constants.PROP_PREFIX_DEVICE_NAME+shortname, dev.getManufacturerName()+" "+dev.getModelName()+" Driver");
                        props.setProperty(Constants.PROP_PREFIX_DEVICE_CLASS+shortname, deviceclass.getName());
                    }
                    catch (Exception e) {
                        ErrorMsg.reportStatus(e);
                        System.out.println("Exception with "+synthDevices[j]);
                    }
                }
            } catch (Exception e) {
                ErrorMsg.reportStatus(e);
            }
        }
        try{
            props.store(out,"Generated devicesfile");
            out.close();
            System.out.println("Done!");
        }
        catch (Exception e) {e.printStackTrace();}
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DeviceListWriter.writeList();
        System.exit(0);
    }
    
    
    
}
