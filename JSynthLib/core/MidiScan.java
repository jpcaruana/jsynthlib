/*
 * MidiScan.java
 *
 */
package core; //TODO org.jsynthlib.midi;

import java.util.Arrays;
import java.util.Enumeration;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.swing.ProgressMonitor;
import javax.swing.JDialog;

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

    private SynthTableModel model;
    private ProgressMonitor pb;
    private JDialog parent;

    private boolean anyUnknown;
    private String report;

    //private static Object lock = new Object();

    /** Creates new MidiScan
     * @param m
     * @param pb  */
    public MidiScan(SynthTableModel m, ProgressMonitor pb, JDialog parent) {
        model = m;
        this.pb = pb;
        this.parent = parent;
    }

    public MidiScan(SynthTableModel m) {
        this(m, null, null);
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
		    MidiUtil.send(rcvr, inqMsg);
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
		    if (((answerData[0] & 0xff) == 0xf0)
			&& (answerData[1] == 0x7e)
			&& (answerData[3] == 0x06)
			&& (answerData[4] == 0x02)) {
			// Look in all loaded modules
			checkResponseData(answerData, sysexSize, j, i, devID);
		    } else if (! Arrays.equals(idData, answerData)) {
			// don't show debug messge for the inquiry request
			ErrorMsg.reportStatus("MidiScan : received non inquiry response data. Ingored.");
		    }
		} // i : input
	    } // devID
	    rcvr.close();
	} //  j : output
	if (pb != null)
	    // clear progress bar
	    pb.setProgress(maxout * (MAX_DEVICE_ID + 1));
	if (anyUnknown && (parent != null)) {
	    ScanUnkownReportDialog surd = new ScanUnkownReportDialog(parent, true);
	    surd.addToReport(report);
	    surd.show();
	}
    }

    private void checkResponseData(byte[] answerData, int msgsize, int midiout, int midiin, int devID) {
	StringBuffer responseString = new StringBuffer("F0");
	for (int k = 1; k < msgsize; k++) {
	    if ((answerData[k] < 16) && (answerData[k] >= 0))
		responseString.append("0");
	    responseString.append(Integer.toHexString(0xff  & answerData[k]));
	}
	//  System.out.println ("ResponseString "+responseString);
	boolean found = false;
	Enumeration synthisenum = PatchEdit.devConfig.IDStrings();
	// synthisenum.reset();
	while (synthisenum.hasMoreElements()) {
	    String se = (String) synthisenum.nextElement();
	    // System.out.println ("Checking "+se.getManufacturerName ()
	    //+" "+se.getModelName ());
	    if (checkInquiry(responseString, se)) {
		// System.out.print ("  Found!: ");
		// System.out.println (se.getManufacturerName ()+" "+se.getModelName ());
		// Check, wether the driver is already in the list
		boolean dontadd = false;
		for (int checkloop = 0; checkloop < PatchEdit.appConfig.deviceCount(); checkloop++) {
		    String checkDevice = ((Device) PatchEdit.appConfig.getDevice(checkloop)).getClass().getName();
		    if (checkDevice.equalsIgnoreCase(PatchEdit.devConfig.classNameForIDString(se))) {
			dontadd = true; // Oh, its already there....
		    }
		}
		if (!dontadd) { // add it only, if it is not in the list
		    String cls = PatchEdit.devConfig.classNameForIDString(se);
		    Device useDevice = PatchEdit.appConfig.addDevice(cls);
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
		if ((0xff & (int) answerData[x]) < 0x10)
		    report += "0";
		report += Integer.toHexString(0xff & (int) answerData[x]);
		if ((answerData[x] & 0xff) == 0xf7) break;
	    }
	    report += "\n";
	}
    }

    private boolean checkInquiry(StringBuffer patchString, String inquiryID) {
	StringBuffer inquiryString = new StringBuffer(inquiryID);
	if (inquiryString.length() > patchString.length())
	    return false;
	for (int j = 0; j < inquiryString.length(); j++)
	    if (inquiryString.charAt(j) == '*')
		inquiryString.setCharAt(j, patchString.charAt(j));
	return (inquiryString.toString().equalsIgnoreCase(patchString.toString().substring(0, inquiryString.length())));
    }

    public void close() {
    }

    public void finalize() {
	close();
    }
}
