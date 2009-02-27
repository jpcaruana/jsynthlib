/*
 * MidiScan.java
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.swing.JDialog;
import javax.swing.ProgressMonitor;
import javax.swing.table.AbstractTableModel;

/**
 * Detect MIDI devices by sending out Inquery ID Sysex Message to
 * every MIDI outport port with every DeviceID and poll every MIDI
 * input port.
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class MidiScan extends Thread {
    // should be 0x7D, but...
    private static final int MAX_DEVICE_ID = 0x7D;
    // Wait at least 100ms for a response from a device
    private static final int WAITFORRESPONSE = 100;
    
    private AbstractTableModel model;
    private ProgressMonitor pb;
    private JDialog parent;
    
    private boolean anyUnknown;
    private String report;
    
    //private static Object lock = new Object();
    
    /** Creates new MidiScan
     * @param model
     * @param pb
     */
    public MidiScan(AbstractTableModel model, ProgressMonitor pb, JDialog parent) {
        this.model = model;
        this.pb = pb;
        this.parent = parent;
    }
    
    public MidiScan(AbstractTableModel model) {
        this(model, null, null);
    }
    
    public void run() {
        // The magical ID-Request Message
        byte[] idData = {
                (byte) 0xf0, (byte) 0x7e, (byte) 0,
                (byte) 0x06, (byte) 0x01, (byte) 0xf7
        };
        
        anyUnknown = false;
        report = new String();
        SysexMessage inqMsg = new SysexMessage();
        
        int maxin = MidiUtil.getInputMidiDeviceInfo().length;
        int maxout = MidiUtil.getOutputMidiDeviceInfo().length;
        
        if (pb != null) {
            // Preparation for a progress bar
            pb.setMaximum(maxout * (MAX_DEVICE_ID + 1));
            pb.setNote("Scanning MIDI Devices");
        }
        for (int j = 0; j < maxout; j++)  {   // For all Outputs
            ErrorMsg.reportStatus("Out port : " + j);
            Receiver rcvr;
            try {
                rcvr = MidiUtil.getReceiver(j);
            } catch (MidiUnavailableException e) {
                ErrorMsg.reportStatus(e + ": Ignored.");
                continue;
            }
            for (int devID = 0; devID <= MAX_DEVICE_ID; devID++) { // every devID
                if (pb != null)
                    pb.setProgress(j * (MAX_DEVICE_ID + 1) + devID);
                //ErrorMsg.reportStatus("  device ID : " + devID);
                idData[2] = (byte) devID; // Set the transmit devID
                try {
                    inqMsg.setMessage(idData, idData.length);
                } catch (InvalidMidiDataException e) {
                    ErrorMsg.reportStatus(e);
                    continue;
                }
                
                // clear all input queue
                for (int i = 0; i < maxin; i++)
                    MidiUtil.clearSysexInputQueue(i);
                
                // send Inquiry ID Sysex Message
                try {
                    MidiUtil.send(rcvr, inqMsg, 
                            AppConfig.getMidiOutBufSize(), AppConfig.getMidiOutDelay());
                } catch (MidiUnavailableException e) {
                    ErrorMsg.reportStatus(e);
                    continue;
                } catch (InvalidMidiDataException e) {
                    ErrorMsg.reportStatus(e);
                    continue;
                }
                try {
                    sleep(WAITFORRESPONSE);
                } catch (InterruptedException e) {
                    ErrorMsg.reportStatus(e);
                    // what should we do?
                }
                for (int i = 0; i < maxin; i++) { // For all Inputs
                    //ErrorMsg.reportStatus("    in port : " + i);
                    if (MidiUtil.isSysexInputQueueEmpty(i))
                        continue;
                    
                    //ErrorMsg.reportStatus("    Message Received");
                    SysexMessage msg;
                    try {
                        msg = (SysexMessage) MidiUtil.getMessage(i, 1);
                    } catch (InvalidMidiDataException e) {
                        ErrorMsg.reportStatus(e);
                        continue;
                    } catch (MidiUtil.TimeoutException e) {
                        ErrorMsg.reportStatus(e);
                        continue;
                    }
                    int sysexSize = msg.getLength();
                    if (sysexSize <= 0)
                        continue;
                    byte[] answerData = msg.getMessage();
                    // check, whether it is really an inquiry response
                    //
                    // The (answerData[2] == 0x06) and (answerData[3] == 0x02) parts
                    // are a hack that is needed e.g. for the Waldorf Microwave 2
                    // which does not obey the MIDI specification
                    if (((answerData[0] & 0xff) == 0xf0)
                        && (answerData[1] == 0x7e)
                        && ( (answerData[2] == 0x06) || (answerData[3] == 0x06))
                        && ( (answerData[3] == 0x02) || (answerData[4] == 0x02)) )
                    {
                        // Look in all loaded modules
                        checkResponseData(answerData, sysexSize, j, i, devID);
                    } else if (! Arrays.equals(idData, answerData)) {
                        // don't show debug messge for the inquiry request
                        ErrorMsg.reportStatus("MidiScan : received non inquiry response data. Ingored.");
                    }
                } // i : input
            } // devID
        } //  j : output
        if (pb != null)
            // clear progress bar
            pb.setProgress(maxout * (MAX_DEVICE_ID + 1));
        if (anyUnknown && (parent != null)) {
            ScanUnkownReportDialog surd = new ScanUnkownReportDialog(parent, true);
            surd.addToReport(report);
            surd.setVisible(true);
        }
    }
    
    private void checkResponseData(byte[] answerData, int msgsize, int midiout, int midiin, int devID) {
        StringBuffer responseString = new StringBuffer("F0");
        for (int k = 1; k < msgsize; k++) {
            if ((answerData[k] < 16) && (answerData[k] >= 0))
                responseString.append("0");
            responseString.append(Integer.toHexString(0xff  & answerData[k]));
        }
        //  ErrorMsg.reportStatus ("ResponseString "+responseString);
        boolean found = false;
        Iterator synthIDs = PatchEdit.devConfig.getIDStrings().iterator();
        while (synthIDs.hasNext()) {
            String se = (String) synthIDs.next();
            // ErrorMsg.reportStatus ("Checking "+se.getManufacturerName ()
            //+" "+se.getModelName ());
            if (checkInquiry(responseString, se)) {
                // Check, wether the driver is already in the list
                boolean dontadd = false;
                for (int checkloop = 0; checkloop < AppConfig.deviceCount(); checkloop++) {
                    String checkDevice = AppConfig.getDevice(checkloop).getClass().getName();
                    if (checkDevice.equalsIgnoreCase(PatchEdit.devConfig.getClassNameForIDString(se))) {
                        dontadd = true; // Oh, its already there....
                    }
                }
                if (!dontadd) { // add it only, if it is not in the list
                    String cls = PatchEdit.devConfig.getClassNameForIDString(se);
                    Device useDevice = AppConfig.addDevice(cls);
                    ErrorMsg.reportStatus("MidiOut: " + midiout
                            + ", MidiIn: " + midiin
                            + ", devID: " + devID);
                    
                    useDevice.setPort(midiout);
                    useDevice.setInPort(midiin);
                    useDevice.setDeviceID(devID + 1);
                    if (pb != null)
                        pb.setNote("Found " + useDevice.getManufacturerName()
                                + " " + useDevice.getModelName());
                }
                
                found = true;
                model.fireTableDataChanged();
            }
        }
        if (!found) {
            anyUnknown = true;
            report += "Unknown Synthesizer found:\n"
                + " According to the manufacturer ID it is made by "
                + LookupManufacturer.get(answerData[5], answerData[6], answerData[7])
                + "\n It is connected on the Output Port: "
                + MidiUtil.getOutputMidiDeviceInfo(midiout).toString()
                + " with Device ID: "
                + devID + 1
                + "\n The Input Port was: "
                + MidiUtil.getInputMidiDeviceInfo(midiin).toString()
                
                + "\n Complete answer was: ";
            
            for (int x = 0; x < msgsize; x++) {
                report += " ";
                if ((0xff & answerData[x]) < 0x10)
                    report += "0";
                report += Integer.toHexString(0xff & answerData[x]);
                if ((answerData[x] & 0xff) == 0xf7)
                    break;
            }
            report += "\n";
        }
    }
    
    private boolean checkInquiry(StringBuffer patchString, String inquiryID) {
        Pattern p = Pattern.compile(inquiryID,Pattern.CASE_INSENSITIVE);
        return p.matcher(patchString).lookingAt();
    }
    
    public void close() {
    }
    
    public void finalize() {
        close();
    }
}
