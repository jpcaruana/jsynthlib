package core;

import java.io.*;
import javax.swing.*;
import java.util.*;
import javax.sound.midi.*;

import com.apple.component.*;
import com.apple.audio.midi.*;
import com.apple.audio.hardware.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

/**
 * Midi wrapper for MacOS X.2
 * This version is not X.1 compatible. Get the older version.
 * @author Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id$
 */
public class MacOSXMidiWrapper extends MidiWrapper
	implements MIDICompletionProc
{
	private static List mInputNames = new ArrayList();
	private static List mOutputNames = new ArrayList();

	private static int mNumInputs;
	private static int mNumOutputs;

	private static MIDIClient mClient;

	/** device output */
	private MIDIOutputPort mOutput;

	/** all inputs */
	private MIDIInputPort mInputs[];

	/** contains List of MIDIData by port (MIDIInputPort) */
	private Map mReceivedDataMap = new Hashtable();

	/** when init done... */
	private boolean mInitDone = false;


	/** MIDI paquet for channel messages , it seems important for
		handling the controller device data smoothy to create only one and reuse it
		because allocate takes too much time
		*/
	private MIDIPacketList mShortMessagePaquetList;


	public MacOSXMidiWrapper() throws Exception {
		if (mClient == null)
		{
			// make static initializations
			mClient = new MIDIClient(new CAFString("JSynthLib"), null);
			loadDevicesNames();
		}
	}

	public void init (int inport, int outport) throws Exception
	{
		mInputs = new MIDIInputPort[mNumInputs];

		// device input
		mInputs[inport] = mClient.inputPortCreate(new CAFString(getInputDeviceName(inport)), new ReadProcImpl());
		MIDIEndpoint oIn = MIDISetup.getSource(inport);
		mInputs[inport].connectSource(oIn);

		// device output
		mOutput = mClient.outputPortCreate(new CAFString(getInputDeviceName(outport)));

		mInitDone = true;
	}

	public String getWrapperName() {
		return("Mac OS/X");
	}


	/**
		Init for input read proc. Puts MIDIInputPort in mInputs.
	*/
	private void inputInit(int inport) throws Exception
	{
		// controller device
		mInputs[inport] = mClient.inputPortCreate(new CAFString(getInputDeviceName(inport)), new ReadProcImpl());
		MIDIEndpoint oInContr = MIDISetup.getSource(inport);
		mInputs[inport].connectSource(oInContr);
	}

	/*
	public void setInputDeviceNum (int port) throws Exception
	{
//		System.out.println("setInputDeviceNum port = "+port);
	}

	public void setOutputDeviceNum (int port) throws Exception
	{
//		System.out.println("setOutputDeviceNum port = "+port);
	}
	*/
	public void send(int port, MidiMessage msg) throws Exception
	{
		byte[] sysex = msg.getMessage();
		int length = msg.getLength();
		if (msg instanceof ShortMessage) {
			switch (length) {
			case 1:
				writeShortMessage (port, sysex[0], (byte) 0, (byte) 0);
				break;
			case 2:
				writeShortMessage (port, sysex[0], sysex[1], (byte) 0);
				break;
			case 3:
				writeShortMessage (port, sysex[0], sysex[1], sysex[2]);
				break;
			default:
				throw new InvalidMidiDataException();
			}
		} else {
			// send sysex
			MIDIEndpoint oOut = MIDISetup.getDestination(port);
//			MIDIData oData = MIDIData.newMIDIPacketData(length);
			MIDIData oData = MIDIData.newMIDIRawData(length);

			oData.addRawData(sysex, 0, length);
			MIDISysexSendRequest oSysex = new MIDISysexSendRequest(oOut, oData);
			oSysex.send(MacOSXMidiWrapper.this);
		}
		MidiUtil.logOut(port, msg);
	}
	/**
		Send a sysex but can be called with note data !!?
		so it wraps writeShortMessage.
	*/
	/*
	public void writeLongMessage (int port,byte []sysex,int length) throws Exception
	{
//		System.out.println("writeLongMessage port = "+port+" len = "+length);
		if (length == 2)
		{
			writeShortMessage (port, sysex[0], sysex[1]);
		}
		else
		if (length == 3)
		{
			writeShortMessage (port, sysex[0], sysex[1], sysex[2]);
		}
		else
		{
			// send sysex
			MIDIEndpoint oOut = MIDISetup.getDestination(port);
//				MIDIData oData = MIDIData.newMIDIPacketData(length);
			MIDIData oData = MIDIData.newMIDIRawData(length);

			oData.addRawData(sysex, 0, length);
			MIDISysexSendRequest oSysex = new MIDISysexSendRequest(oOut, oData);
			oSysex.send(MacOSXMidiWrapper.this);
			MidiUtil.logOut(port, sysex, length);
		}
	}


	public  void writeLongMessage (int port,byte []sysex)throws Exception
	{
		writeLongMessage(port, sysex, sysex.length);
	}


	public void writeShortMessage (int port, byte b1, byte b2) throws Exception
	{
		writeShortMessage(port, b1, b2, (byte)0);
	}
	*/
	private void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception
	{
//		System.out.println("writeShortMessage port = "+port+" b1="+b1+" b2="+b2+" b3= "+b3);
		if (mShortMessagePaquetList == null)
		{
			mShortMessagePaquetList = new MIDIPacketList();
		}
		else
		{	// reuse the paquet
			mShortMessagePaquetList.init();
		}
		MIDIData oData = MIDIData.newMIDIChannelMessage(b1, b2, b3);
		mShortMessagePaquetList.add(0, oData);
		MIDIEndpoint oOut = MIDISetup.getDestination(port);
		mOutput.send(oOut, mShortMessagePaquetList);
	}


	public int getNumInputDevices () throws Exception
	{
		if (!mInitDone)
		{
			mNumInputs = MIDISetup.getNumberOfSources();
		}
		return mNumInputs;
	}

	public int getNumOutputDevices ()throws Exception
	{
		if (!mInitDone)
		{
			mNumInputs = MIDISetup.getNumberOfDestinations();
		}
		return mNumOutputs;
	}

	public  String getInputDeviceName (int port)throws Exception
	{
		String oS2 =  (String)mInputNames.get(port);
		return oS2;
	}

	public  String getOutputDeviceName (int port)throws Exception
	{
		String oS2 =  (String)mOutputNames.get(port);
		return oS2;
	}

	/**
	 * Called periodically to see if controller has sent a note or
	 * device has sent sysex.
	 */
	public  int messagesWaiting (int port) throws Exception
	{
		if (mInputs[port] == null)
		{
			// creates new input
			inputInit(port);
		}
		else
		{
			List oList = (List)mReceivedDataMap.get(mInputs[port]);
			if (oList != null)
			{
				if (oList.size() > 0)
				{
					//		System.out.println("messagesWaiting port = "+port+" len = "+oList.size());
					return oList.size();
				}
			}
		}
		return 0;
	}

	/** get the older data packet received */
	/*
	public int readMessage (int port,byte []sysex,int maxSize) throws Exception
	{
//		List oList = (List)mReceivedDataMap.get(getInputDeviceName(port));
		List oList = (List)mReceivedDataMap.get(mInputs[port]);
		if (oList != null)
		{
			if (oList.size() > 0)
			{
				// get the older packet
				byte[] oData = (byte[])oList.remove(0);
//		System.out.println("readMessage port="+port+" len = "+oData.length);
				System.arraycopy(oData, 0, sysex, 0, oData.length);
				return oData.length;
			}
		}
		return 0;
	}
	*/
	MidiMessage getMessage (int port) throws InvalidMidiDataException {
		// pop the oldest message
		List oList = (List)mReceivedDataMap.get(mInputs[port]);
		if (oList == null || oList.size() == 0)
			throw new InvalidMidiDataException("Illegal MIDI input message");
		// get the older packet
		MidiMessage msg = (MidiMessage) oList.remove(0);

		/*
		byte[] oData = (byte[])oList.remove(0);
// 		System.out.println("readMessage port="+port+" len = "+oData.length);

		MidiMessage msg;
		if ((int) (oData[0] & 0XFF) == SysexMessage.SYSTEM_EXCLUSIVE
		    || (int) (oData[0] & 0xFF) == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
			msg = (MidiMessage) new SysexMessage();
			((SysexMessage) msg).setMessage(oData, oData.length);
		} else {
			msg = (MidiMessage) new ShortMessage();
			switch (oData.length) {
			case 1:
				((ShortMessage) msg).setMessage((int) (oData[0] & 0xff));
				break;
			case 2:
				((ShortMessage) msg).setMessage((int) (oData[0] & 0xff),
								(int) (oData[1] & 0xff),
								0);
				break;
			case 3:
				((ShortMessage) msg).setMessage((int) (oData[0] & 0xff),
								(int) (oData[1] & 0xff),
								(int) (oData[2] & 0xff));
				break;
			default:
				throw new InvalidMidiDataException("Status = "
								   + (int) (oData[0] & 0xFF)
								   + ", length = "
								   + oData.length);
			}
		}
		*/
		MidiUtil.logIn(port, msg);
		return msg;
	}

	public void close()
	{
//		System.out.println("close");
	}

	/**
		Load devices names into static variables
	*/
	private static void loadDevicesNames() throws Exception
	{
		int oEntNum = 0;
		mNumInputs = 0;
		mNumOutputs = 0;
		for (int k = 0; k < MIDIDevice.getNumberOfDevices(); k++)
		{
			MIDIDevice oDev = MIDIDevice.getDevice(k);
//				String oS2 =  oDev.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
//				System.out.println("device = "+oS2);
			for (int entity = 0; entity < oDev.getNumberOfEntities(); entity++)
			{
				MIDIEntity oEnt = oDev.getEntity(entity);
				String oSEntity =  oDev.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
//					System.out.println("entity = "+oSEntity);
				mNumInputs += oEnt.getNumberOfSources();
				mNumOutputs += oEnt.getNumberOfDestinations();
				for (int i = 0; i < oEnt.getNumberOfSources(); i++)
				{
					MIDIEndpoint oIn = oEnt.getSource(i);
					String oSIn =  oIn.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
//						System.out.println("input = "+oSIn);
					mInputNames.add(oSEntity+" "+oSIn);
				}
				for (int i = 0; i < oEnt.getNumberOfDestinations(); i++)
				{
					MIDIEndpoint oIn = oEnt.getDestination(i);
					String oSOut =  oIn.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
//						System.out.println("output = "+oSOut);
					mOutputNames.add(oSEntity+" "+oSOut);
				}
			}
		}
	}

	// **************  interface MIDICompletionProc *****************************

	/**
	 * Called on end of sysex sending
	 */
	public void execute(MIDISysexSendRequest request)
	{
//		System.out.println("SYSEX sent IN");
//		System.out.println("SYSEX sent OUT");
	}

        /** This method should return true, if this wrapper is
         * supported on the actual platform (a wrapper for
         * MacOSX should return true only on Mac's with OSX
         * etc.)
         * @return true, if wrapper is supported, false if wrapper is not supported at this
         * platform.
         *
         */
        public boolean isSupported() throws Exception {
        // TODO: Implement real functionality here
		return true;
        }

	// **************  interface MIDIReadProc *****************************

	/**
	 * Read method
	 */
	class ReadProcImpl implements MIDIReadProc
	{
		/**
			Sysex are received in several parts. I put them in a list
			read by readMessage.
		*/
		private boolean inSysex = false; // true during receiving divided Sysex Message
		public void execute(MIDIInputPort port, MIDIEndpoint srcEndPoint, MIDIPacketList list)
		{
			try
			{
				List oList = (List)mReceivedDataMap.get(port);
				if (oList == null)
				{
					// Vector for sync need
					oList = new Vector();
					mReceivedDataMap.put(port, oList);
				}
				int oNumPackets = list.numPackets();
				for (int i = 0; i < oNumPackets; i++)
				{
					MIDIPacket oPkt = list.getPacket(i);
					MIDIData oData = oPkt.getData();
					inSysex = addToList(oList, oData, inSysex);
					/*
					byte[] oArray = new byte[oData.getMIDIDataLength()];
					oData.copyToArray(0,oArray, 0, oData.getMIDIDataLength());
                                      	// Rib Rdb (ribrdb@yahoo.com)
 					// Filter out realtime messages
 					ByteArrayOutputStream os =
 					  new ByteArrayOutputStream(256);
 					for (int q=0; q<oArray.length; q++)
 					  if ( (oArray[q] & 0x80) != 0x80 ||
 					       (oArray[q] & 0x7F) < 0x78 )
 					    os.write(oArray[q]);
 					oArray = os.toByteArray();
					// End ribrdb@yahoo.com
					oList.add(oArray);
					*/
				}
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}

		/**
		 * convert a MIDIData objects into MidiMessage objects
		 * and add them to the input list, <code>oList</code>.
		 *
		 * @author Hiroo Hayashi
		 * @See javax.sound.midi.SystemMessage
		 */
		private boolean addToList (List oList, MIDIData oData, boolean inSysex) {
			int status = (int) (oData.getByteAt(0) & 0xff);
			int len = oData.getMIDIDataLength();
			if (status == SysexMessage.SYSTEM_EXCLUSIVE || inSysex) {
				byte[] d = new byte[len];
				oData.copyToArray(0, d, 0, len);
				ErrorMsg.reportStatus("Sysex: " + MidiUtil.hexDump(d, 0, -1, 16));
				SysexMessage msg = new SysexMessage();
				try {
					if (status == SysexMessage.SYSTEM_EXCLUSIVE) {
						msg.setMessage(d, len);
					} else {
						msg.setMessage(SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE, d, len);
					}
				} catch (InvalidMidiDataException e) {
					// let getMessage cause InvalidMidiDataException
					msg = null;
				}
				oList.add(msg);
				// return true if not terminated by EOX
				return ((int) (d[len - 1] & 0xff)) != ShortMessage.END_OF_EXCLUSIVE;
			} else {
				// for debugging
				byte[] d = new byte[len];
				oData.copyToArray(0, d, 0, len);
				ErrorMsg.reportStatus("Short: " + MidiUtil.hexDump(d, 0, -1, 16));
				// Single Messages
				for (int i = 0; i < len; i++) {
					status = (int) (oData.getByteAt(i) & 0xff);
					// ignore System Real Time Message and data byte
					if ((status & 0xf8) == 0xf8 || (status & 0x80) == 0)
						continue;

					ShortMessage msg = new ShortMessage();
					try {
						int data1, data2;

						switch (status < 0xf0 ? status & 0xf0 : status) {
						case ShortMessage.NOTE_OFF:		// 0x8n
						case ShortMessage.NOTE_ON:		// 0x9n
						case ShortMessage.POLY_PRESSURE:	// 0xAn
						case ShortMessage.CONTROL_CHANGE:	// 0xBn
						case ShortMessage.PITCH_BEND:		// 0xEn
						case ShortMessage.SONG_POSITION_POINTER: // 0xf2
							data1 = (int) (oData.getByteAt(++i) & 0xff);
							data2 = (int) (oData.getByteAt(++i) & 0xff);
							msg.setMessage(status, data1, data2);
							break;

						case ShortMessage.PROGRAM_CHANGE:	// 0xCn
						case ShortMessage.CHANNEL_PRESSURE:	// 0xDn
						case ShortMessage.MIDI_TIME_CODE:	// 0xf1
						case ShortMessage.SONG_SELECT:		// 0xf3
							data1 = (int) (oData.getByteAt(++i) & 0xff);
							msg.setMessage(status, data1, 0);
							break;

						case ShortMessage.TUNE_REQUEST:		// 0xf6
							msg.setMessage(status);
							break;

						case 0xf4: // undefined
						case 0xf5: // undefined
						case ShortMessage.END_OF_EXCLUSIVE:	// 0xf7
							continue; // ignore
						default:
							msg = null;
							ErrorMsg.reportStatus("Illegal Status:" + status
									      + ", i=" + i);
						}
					} catch (InvalidMidiDataException e) {
						// let getMessage cause InvalidMidiDataException
						msg = null;
						ErrorMsg.reportStatus("InvalidMidiData:" + status
								      + ", i=" + i);
					} catch (Exception e) {
						// What does getByteAt() throw for out of range?
						msg = null;
						ErrorMsg.reportStatus("???:" + status + ", i=" + i
								      + ": " + e);
					}
					oList.add(msg);
				}
				return false;
			}
		}
	}
}
/*
Here's what the api says:
struct MIDIPacket {
  MIDITimeStamp  timeStamp;
  UInt16         length;
  Byte           data[256];
};
One or more MIDI events occuring at a particular time.

Fields

timeStamp

The time at which the events occurred, if receiving MIDI, or, if
sending MIDI, the time at which the events are to be played. Zero
means "now." The time stamp applies to the first MIDI byte in the
packet.

length

The number of valid MIDI bytes which follow, in data. (It may be
larger than 256 bytes if the packet is dynamically allocated.)

data

A variable-length stream of MIDI messages. Running status is not
allowed. In the case of system-exclusive messages, a packet may only
contain a single message, or portion of one, with no other MIDI
events. The MIDI messages in the packet must always be complete,
except for system-exclusive. Single-byte MIDI realtime messages may
not occur between the status and data bytes of other messages, except
in system-exclusive.
*/
//(setq c-basic-offset 8)
