/*
 * MidiScan.java
 *
 * Created on 1. Oktober 2000, 12:27
 */
package core;

import javax.sound.midi.*;
import java.util.Vector;
import java.util.*;
import javax.swing.table.AbstractTableModel;
/**
 *
 * @author Gerrit Gehnen <Gerrit.Gehnen@gmx.de>
 * @version 0.1
 */

public class MidiScan extends  Thread {
    
    private static Object lock=new Object();
    
    final int MAXCHANNEL=16;
    final int WAITFORRESPONSE=100; // Wait at least 100ms for a response from a device
    
    public SynthTableModel model;
    javax.swing.ProgressMonitor pb;
    public byte channel=0;
    
    private javax.swing.JDialog parent;
    RecognizerEnumeration synthisenum;
    
    /** Creates new MidiScan
     * @param m
     * @param pb  */
    public MidiScan(SynthTableModel m,javax.swing.ProgressMonitor pb,javax.swing.JDialog parent) {
        model=m;
        this.pb=pb;
        this.parent=parent;
        synthisenum=new RecognizerEnumeration();
    }
    
    public MidiScan(SynthTableModel m) {
        this(m,null,null);
    }
    
    public void run() {
        byte idData[]=  // The magical ID-Request Message
        {(byte)0xf0,(byte)0x7e,(byte)0,(byte)0x06,(byte)0x01,(byte)0xf7};
        byte answerData[]=new byte[50]; // That should be enough
        
        int maxin=0,maxout=0;	// Counters for Loops
        MidiWrapper inarray[];
        Device se;
        boolean found=false;
        boolean anyUnkown=false;
        String report=new String();
        
        try {
            maxin=PatchEdit.MidiIn.getNumInputDevices();
            maxout=PatchEdit.MidiOut.getNumOutputDevices();
            // System.out.println ("Iterating "+maxin+" Inputs and "+maxout+" Outputs");
        }catch (Exception e)
        {ErrorMsg.reportStatus(e);}
        inarray=new MidiWrapper[maxin];
        if (pb!=null) pb.setMaximum(maxin+maxout-1);// Preparation for a progress bar
        try {
            for (int i=0;i<maxin;i++) // Create an array of receivers for all In-Ports
            {
                inarray[i]=(MidiWrapper)PatchEdit.MidiIn.getClass().newInstance();
                inarray[i].setInputDeviceNum(i);
                if (pb!=null) pb.setProgress(i);
            }
        } catch(Exception e)
        {ErrorMsg.reportStatus(e);}
        
        if (pb!=null) pb.setNote("Scanning Midi Devices");
        
        try {
            for (int j=0;j<maxout;j++)   // For all Outputs
            {
                if (pb!=null) pb.setProgress(j+maxin-1);
                for (channel=0;channel<MAXCHANNEL;channel++) // every channel
                {
                    idData[2]=channel; // Set the transmit channel
                    
                    try {
                        PatchEdit.MidiOut.writeLongMessage(j,idData);
                        sleep(WAITFORRESPONSE);
                        for (int i=0;i<maxin;i++) {
                            byte buffer[]=new byte[50];
                            int sysexSize=0;
                            int msgsize=0;
                            //System.out.println("Examine inport "+i);
                            while (inarray[i].messagesWaiting(i)>0) {
                                msgsize=inarray[i].readMessage(i,buffer,buffer.length);
                                System.arraycopy(buffer, 0, answerData, sysexSize, msgsize);
                                sysexSize+=msgsize;
                            }
                            
                            if (sysexSize>0) {
                                sleep(100);
                                //int msgsize=inarray[i].readMessage (i,answerData,answerData.length);
                                msgsize=sysexSize;
                                //System.out.println ("msgSize "+msgsize);
                                found=false;
                                // Look in all loaded modules
                                StringBuffer responseString= new StringBuffer("F0");
                                if (((answerData[0]&0xff)==0xf0)&&(answerData[1]==0x7e)&&(answerData[3]==6)&&(answerData[4]==2)) {  // check, wether it is really an inquiry response
                                    for (int k=1;k<msgsize;k++) {
                                        if (answerData[k]<16) responseString.append("0");
                                        responseString.append(Integer.toHexString(0xff&answerData[k]));
                                    }
                                    //  System.out.println ("ResponseString "+responseString);
                                    synthisenum.reset();
                                    while (synthisenum.hasMoreElements()) {
                                        se=synthisenum.nextElement();
                                        // System.out.println ("Checking "+se.getManufacturerName ()+" "+se.getModelName ());
                                        if (se.checkInquiry(responseString)) {
                                            // System.out.print ("  Found!: ");
                                            // System.out.println (se.getManufacturerName ()+" "+se.getModelName ());
                                            try {
                                                // Check, wether the driver is already in the list
                                                boolean dontadd=false;
                                                for (int checkloop=0;checkloop<PatchEdit.deviceList.size();checkloop++) {
                                                    Device checkDevice=(Device)PatchEdit.deviceList.get(checkloop);
                                                    
                                                    if ((se.getManufacturerName().equals(checkDevice.getManufacturerName()))&&(se.getModelName().equals(checkDevice.getModelName()))) {
                                                        dontadd=true; // Oh, its already there....
                                                    }
                                                }
                                                if (!dontadd) // add it only, if it is not in the list
                                                {
                                                    Device useDevice=(Device)se.getClass().newInstance();
                                                    useDevice.setPort(j);
                                                    useDevice.setInPort(i);
                                                    useDevice.setChannel(channel+1);
                                                    if (pb!=null) pb.setNote("Found "+se.getManufacturerName()+" "+se.getModelName());
                                                    
                                                    PatchEdit.deviceList.add(useDevice );
                                                    
                                                }
                                            } catch (Exception e)
                                            {ErrorMsg.reportStatus(e);}
                                            
                                            found=true;
                                            model.fireTableDataChanged();
                                        }
                                    }
                                }
                                if (!found) {
                                    anyUnkown=true;
                                    report+= "Unknown Synthesizer found:\n";
                                    report+= " According to the manufacturer ID it is made by ";
                                    report+= LookupManufacturer.get(answerData[5],answerData[6],answerData[7]);
                                    report+= "\n It is connected on the Output Port: ";
                                    report+= PatchEdit.MidiOut.getOutputDeviceName(j);
                                    report+= " with Channel: ";
                                    report+= channel+1;
                                    report+= "\n The Input Port was: "+PatchEdit.MidiOut.getInputDeviceName(i);
                                    
                                    report+="\n Complete answer was: ";
                                    
                                    for (int x=0;x<msgsize;x++) {
                                        report+= " ";
                                        if ((0xff&(int)answerData[x])<0x10)
                                            report+="0";
                                        report+=Integer.toHexString(0xff&(int)answerData[x]);
                                        if ((answerData[x]&0xff)==0xf7) break;
                                    }
                                    report+="\n";
                                }
                                
                            }
                        }
                    }
                    catch(IllegalStateException e)
                    {ErrorMsg.reportStatus(e);}
                }
            }
        }
        catch(Exception e)
        {e.printStackTrace();
         ErrorMsg.reportStatus(e);}	// This produces "null"
        
        if (pb!=null) pb.setProgress(maxout+maxin);
        
        for (int i=0;i<maxin;i++) {
            inarray[i].close();
        }
        if (anyUnkown&& (parent!=null)) {
            ScanUnkownReportDialog surd=new ScanUnkownReportDialog(parent,true);
            surd.addToReport(report);
            surd.show();
        }
    }
    
    public void close() {
        
    }
    
    public void finalize() {
        close();
    }
    
}