package core; //TODO org.jsynthlib.midi;

import javax.sound.midi.*;
import java.util.*;
import javax.swing.*;

/**
 * @author Emenaker - 2003.03.20
 *
 * This class is for running tests on the Midi subsystem
 */
public class MidiTest implements Runnable {
	
	/**
	 * This runs a few tests on a midi in/out pair. The idea is that you connect the two ports with a
	 * midi cable and it basically tries to send one of every kind of message and it checks to see if
	 * those messages get back to the "in" port intact. - emenake 2003.03.20
	 * 
	 * @param midiDriver The midiwrapper that we're using
	 * @param inport The number of the port for receiving
	 * @param outport The number of the port for sending
	 */
	static boolean runLoopbackTest(MidiWrapper midiDriver, int inport, int outport) {
		core.ErrorMsg.reportError ("Before we start",
			"This process is designed to test the communication of\n" +
			"your MIDI ports. Please connect a MIDI cable from the\n" +
			"selected \"IN\" port to the selected \"OUT\" port.\n" +
			"When you press \"OK\", a variety of MIDI messages will\n" +
			"be sent from the OUT to the IN to make sure that they\n" +
			"transferred properly. This process usually takes about\n" +
			"5 to 10 seconds, so be patient.");

		try {
			Vector msgList = getMidiMessages();
			
			/*
			 * This section is for creating/driving a progress bar. However, since this
			 * method is currently getting run by the UI thread, all UI updates stop while
			 * the test is running, so the progress bar never gets updated. I probably
			 * need to execute this thing in a separate thread in order to make this
			 * work. - emenaker 2003.03.26
			 */
/*
 			JDialog dialog = new JDialog();
			JPanel panel = new JPanel();
			JProgressBar pbar = new JProgressBar();
			panel.add(pbar);
			dialog.getContentPane().add(panel);
			dialog.pack();
			dialog.show();
			
			pbar.setMinimum(0);
			pbar.setMaximum(msgList.size());
*/
			for(int i=0; i<msgList.size(); i++) {
				//pbar.setValue(i);
				runLoopbackTest(midiDriver, inport, outport, (MidiMessage) msgList.elementAt(i));
			}
			
			// If we get this far, then things must have gone okay....
			core.ErrorMsg.reportError ("Congratulations!",
				"The test appears to have completed successfully\n" +
				"Don't forget to hook your MIDI cables back up to your devices.");
		} catch(Exception e) {
			core.ErrorMsg.reportError ("Warning","The test failed.\n" +
				"There are many reasons why this could have happened.\n" +
				"Most likely, you don't have the selected \"IN\" and \"OUT\" ports\n" +
				"connected to each other with a MIDI cable.\n",e);
			e.printStackTrace();
			return(false);
		}
		return(true);
	}

	/**
	 * This runs a few tests on a midi in/out pair. The idea is that you connect the two ports with a
	 * midi cable and it basically tries to send one of every kind of message and it checks to see if
	 * those messages get back to the "in" port intact.
	 */
	static void runLoopbackTest(MidiWrapper midiDriver, int inport, int outport, MidiMessage msg) throws Exception{
		MidiMessage inmsg;
		try {
			// Empty the input queue
			while(midiDriver.messagesWaiting(inport)>0) {
				midiDriver.readMessage(inport);
			}

			// If it's a sysex message, we need to make sure that it's got a 0xF7 on the end.
			// If not, we'll put one on...
			if(msg instanceof SysexMessage) {
				// We need to send the stop message....
				SysexMessage sysexstop = new SysexMessage();
				byte[] buffer = msg.getMessage();
				if(buffer[buffer.length-1] != (byte) ShortMessage.END_OF_EXCLUSIVE) {
					// There's not a 0xF7 at the end. We need to put one there....
					buffer = new byte[buffer.length+1];
					System.arraycopy(msg.getMessage(),0,buffer,0,msg.getLength());
					buffer[msg.getLength()]=(byte) ShortMessage.END_OF_EXCLUSIVE;
					((SysexMessage) msg).setMessage(buffer, buffer.length);
				}
			}
			
			// Send it
			midiDriver.send(outport,msg);
			
			// Wait for up to a second for the message to come in...
			long time = System.currentTimeMillis();
			while(System.currentTimeMillis()-time < 1000) {
				if(midiDriver.messagesWaiting(inport)>0) {
					inmsg = midiDriver.readMessage(inport);
					if(inmsg != null) {
						if(areEqual(msg,inmsg)) {
							return;
						} else {
							throw(new Exception("Test failed on a message of "+msg.getLength()+" bytes.\n" +
								messageToString(msg) + "\n" +
								"doesn't match\n" +
								messageToString(inmsg)));
						}
					} else {
						throw(new Exception("MidiMessage read was null"));
					}
				}
			}
			// If we get here, then time expired without seeing anything.		
			core.ErrorMsg.reportError ("Warning","Didn't see anything come into the input");
			throw(new Exception("Time expired without seeing any message come in"));
		} catch(Exception e) {
			core.ErrorMsg.reportError ("Error","Exception",e);
			throw e;
		}
	}

	/**
	 * This just returns a vector full of MidiMessage objects that the tester uses to send out
	 * through the loopback. - emenaker 2003.03.24
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Vector getMidiMessages() throws Exception {
		Vector msgList = new Vector();
		ShortMessage msg = new ShortMessage();

		// Make a bunch of messages and try sending them. Why use data bytes 0x4B, 0x70?
		// Well, it's binary 0100110001110000 (a zero, a one, two zeroes, two ones, etc.)
		// I wanted to pick some sequence that, if it was shifted a little bit, wouldn't
		// match iteself - emenaker 2003.03.20

		// Channel Voice Messages
		msg.setMessage(ShortMessage.NOTE_OFF,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.NOTE_ON,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.POLY_PRESSURE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.CONTROL_CHANGE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.PROGRAM_CHANGE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.CHANNEL_PRESSURE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.PITCH_BEND ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());

		// System Real-Time Messages
		msg.setMessage(ShortMessage.TIMING_CLOCK ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.START ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.CONTINUE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.STOP ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		// Active sensing didn't work on for me with JavaMidi... so I'm omitting it here - emenaker 2003.03.20
		//msg.setMessage(ShortMessage.ACTIVE_SENSING ,(byte)0x4B, (byte)0x70);
		//msgList.addElement(msg.clone());
		
		// System Common Messages
		msg.setMessage(ShortMessage.MIDI_TIME_CODE ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.SONG_POSITION_POINTER ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		msg.setMessage(ShortMessage.SONG_SELECT ,(byte)0x4B, (byte)0x70);
		msgList.addElement(msg.clone());
		
		// Sysex messages
		SysexMessage sysexmsg = new SysexMessage();
			
		byte[] sysexpayload = new byte[500];
		// Initialize the buffer... making sure that no bytes are greater than 128
		sysexpayload[0] = (byte) 0xF0; // Sysex-Start command
		for(int i=1; i<sysexpayload.length; i++) {
			sysexpayload[i] = (byte) (i % 128);
		}
		// Start with a message of a certain size and keep increasing until we get to the
		// biggest size we've got - emenaker 2003.03.20
		for(int i=10; i<sysexpayload.length; i+=10) {
			sysexmsg.setMessage(sysexpayload,i);
			msgList.addElement(sysexmsg.clone());
		}
		return(msgList);
	}

	/**
	 * This is so that we can run this as a separate thread. I haven't actually done this yet,
	 * because I'm worried about other threads being able to do wierd things with the 
	 * MidiWrapper while the test is running. - emenaker 2003.03.26
	 */
	public void run() {
		
	}

	/**
	 * This tests two MidiMessage objects for equality. A byte-by-byte comparison of the actual
	 * message data is performed. - emenaker 2003.03.23
	 * @param msg1
	 * @param msg2
	 * @return True if the messages are equal
	 */
	public static boolean areEqual(MidiMessage msg1, MidiMessage msg2) {
		if(msg1.getLength() == msg2.getLength()) {
			byte[] thisdata = msg1.getMessage();
			byte[] thatdata = msg2.getMessage();
			for(int i=0; i<thisdata.length; i++) {
				if(thisdata[i] != thatdata[i]) {
					return(false);
				}
			}
			// If we made it this far, then we checked all bytes and none mismatched
			return(true);
		}
		return(false);
	}
	
	/**
	 * This converts a MidiMessage to a string of hex values, with 16 values per line
	 * and each line terminated by a newline character (but the last line is not unless
	 * it has 16 values in it) - emenaker 2003.03.25
	 * @param msg
	 * @return
	 */
	public static String messageToString(MidiMessage msg) {
		String str = new String();
		byte[] msgarray = msg.getMessage();
		char[] hexchar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		for(int i=0; i<msgarray.length; i++) {
			str = str + " 0x" + hexchar[(msgarray[i] & 0xFF) / 16] + hexchar[(msgarray[i] & 0x0F)];
			if(i % 16 == 15) {
				str = str + "\n"; 
			}
		}
		return(str);
	}
}
