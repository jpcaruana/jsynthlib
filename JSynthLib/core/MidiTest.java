package core; //TODO org.jsynthlib.midi;

import javax.sound.midi.*;
import java.util.*;
import javax.swing.*;
// import core.ErrorMsg;

/**
 * @author Emenaker - 2003.03.20
 *
 * This class is for running tests on the MIDI subsystem
 */
public class MidiTest implements Runnable {

	/** Max data size of system exclusive message test. */
	private static final int MAX_SYSEX_SIZE = 500;

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
		ErrorMsg.reportWarning("Before we start",
				       "This process is designed to test the communication of\n" +
				       "your MIDI ports. Please connect a MIDI cable from the\n" +
				       "selected \"IN\" port to the selected \"OUT\" port.\n" +
				       "When you press \"OK\", a variety of MIDI messages will\n" +
				       "be sent from the OUT to the IN to make sure that they\n" +
				       "transferred properly. This process usually takes about\n" +
				       "5 to 10 seconds, so be patient.\n");

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
			ErrorMsg.reportWarning("Congratulations!",
					       "The test appears to have completed successfully.\n" +
					       "Don't forget to hook your MIDI cables back up to your devices.");
		} catch(Exception e) {
			ErrorMsg.reportError("Warning","The test failed.\n" +
					     "There are many reasons why this could have happened.\n" +
					     "Most likely, you don't have the selected \"IN\" and \"OUT\" ports\n" +
					     "connected to each other with a MIDI cable.",e);
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
// 		try {
			midiDriver.clearMidiInBuffer(inport);

			// If it's a sysex message, we need to make sure that it's got a 0xF7 on the end.
			// If not, we'll put one on...
			if(msg instanceof SysexMessage) {
				// We need to send the stop message....
				SysexMessage sysexstop = new SysexMessage();
				byte[] buffer = msg.getMessage();
				int len = msg.getLength();
				// buffer.length may not be equal to msg.getLength()
 				//if(buffer[buffer.length-1] != (byte) ShortMessage.END_OF_EXCLUSIVE) {
				if(buffer[len - 1] != (byte) ShortMessage.END_OF_EXCLUSIVE) {
					// There's not a 0xF7 at the end. We need to put one there....
					buffer = new byte[len+1];
					System.arraycopy(msg.getMessage(),0,buffer,0,len);
					buffer[len]=(byte) ShortMessage.END_OF_EXCLUSIVE;
					((SysexMessage) msg).setMessage(buffer, buffer.length);
				}
			}

			// Send it
			midiDriver.send(outport,msg);

			try {
				// 1 sec =~ 4KB sysex data
				MidiMessage inmsg = midiDriver.readMessage(inport, 1000);
				if (areEqual(msg,inmsg)) {
					return;
				} else {
					ErrorMsg.reportError("Error",
							     "Data Compare Error:"
							     + "\nreceived data: "
							     + MidiUtil.midiMessageToString(inmsg)
							     + "\nexpected data: "
							     + MidiUtil.midiMessageToString(msg));
					throw new Exception("Data mismatch");
				}
			} catch (MidiWrapper.TimeoutException e) {
				ErrorMsg.reportError("Warning","Didn't see anything come into the input");
				throw e;
				//throw(new Exception("Time expired without seeing any message come in"));
			}
// 		} catch (Exception e) {
// 			ErrorMsg.reportError("Error","Exception",e);
// 			throw e;
// 		}
	}

	/**
	 * This just returns a vector full of MidiMessage objects that
	 * the tester uses to send out through the loopback. -
	 * emenaker 2003.03.24
	 *
	 * @return
	 * @throws Exception
	 */
	private static final boolean testShortMessage = true;
	private static final boolean testSysexMessage = true;
	private static Vector getMidiMessages() throws Exception {
		Vector msgList = new Vector();
		ShortMessage msg = new ShortMessage();
		if (testShortMessage) {
			// Make a bunch of messages and try sending
			// them. Why use data bytes 0x4B, 0x70?  Well,
			// it's binary 0100110001110000 (a zero, a
			// one, two zeroes, two ones, etc.)  I wanted
			// to pick some sequence that, if it was
			// shifted a little bit, wouldn't match
			// iteself - emenaker 2003.03.20

			// Channel Voice Messages
			// lower MIDI driver may convert to NOTE_ON for running status
			//msg.setMessage(ShortMessage.NOTE_OFF, 0x4B, 0x70); // 2B
			//msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.NOTE_ON, 0x4B, 0x70); // 2B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.POLY_PRESSURE, 0x4B, 0x70); // 2B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0x4B, 0x70); // 2B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.PROGRAM_CHANGE, 0x4B, 0x70); // 1B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.CHANNEL_PRESSURE, 0x4B, 0x70); // 1B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.PITCH_BEND, 0x4B, 0x70); // 2B
			msgList.addElement(msg.clone());

			// System Real-Time Messages (all 0 byte data)
			// commented out since some Wrappers filter them out
			/*
			msg.setMessage(ShortMessage.TIMING_CLOCK, 0x4B, 0x70);
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.START, 0x4B, 0x70);
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.CONTINUE, 0x4B, 0x70);
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.STOP, 0x4B, 0x70);
			msgList.addElement(msg.clone());
			*/
			// Active sensing didn't work on for me with
			// JavaMidi... so I'm omitting it here -
			// emenaker 2003.03.20
			//msg.setMessage(ShortMessage.ACTIVE_SENSING, 0x4B, 0x70);
			//msgList.addElement(msg.clone());

			// System Common Messages
			msg.setMessage(ShortMessage.MIDI_TIME_CODE, 0x4B, 0x70); // 1B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.SONG_POSITION_POINTER, 0x4B, 0x70); // 2B
			msgList.addElement(msg.clone());
			msg.setMessage(ShortMessage.SONG_SELECT, 0x4B, 0x70); // 1B
			msgList.addElement(msg.clone());
		}
		if (testSysexMessage) {
			// Sysex messages
			SysexMessage sysexmsg = new SysexMessage();

			byte[] sysexpayload = new byte[MAX_SYSEX_SIZE];
			// Initialize the buffer... making sure that
			// no bytes are greater than 128
			sysexpayload[0] = (byte) 0xF0; // Sysex-Start command
			for(int i=1; i<sysexpayload.length; i++) {
				sysexpayload[i] = (byte) (i % 128);
			}
			// Start with a message of a certain size and
			// keep increasing until we get to the biggest
			// size we've got - emenaker 2003.03.20
			for(int i=10; i<sysexpayload.length; i+=20) {
				sysexmsg.setMessage(sysexpayload,i);
				msgList.addElement(sysexmsg.clone());
			}
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
		ErrorMsg.reportStatus(msg1 + ", " + msg1.getLength()
				      + ": " + msg2 + ", " + msg2.getLength());
		if (msg1.getLength() != msg2.getLength())
			return false;

		byte[] thisdata = msg1.getMessage();
		byte[] thatdata = msg2.getMessage();
		for (int i = 0; i < thisdata.length; i++) {
			if (thisdata[i] != thatdata[i]) {
				return false;
			}
		}
		// If we made it this far, then we checked all bytes and none mismatched
		return true;
	}

	/**
	 * This converts a MidiMessage <code>msg</code> to a string of
	 * hex values, with 16 values per line and each line
	 * terminated by a newline character (but the last line is not
	 * unless it has 16 values in it).
	 */
	//- emenaker 2003.03.25
	/*
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
	*/
}
//(setq c-basic-offset 8)
