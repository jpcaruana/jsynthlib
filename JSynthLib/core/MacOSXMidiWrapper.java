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
	void inputInit(int inport) throws Exception
	{
		// controller device
		mInputs[inport] = mClient.inputPortCreate(new CAFString(getInputDeviceName(inport)), new ReadProcImpl());
		MIDIEndpoint oInContr = MIDISetup.getSource(inport);
		mInputs[inport].connectSource(oInContr);
	}


	public void setInputDeviceNum (int port) throws Exception
	{
//		System.out.println("setInputDeviceNum port = "+port);
	}

	public void setOutputDeviceNum (int port) throws Exception
	{
//		System.out.println("setOutputDeviceNum port = "+port);
	}

	/**
		Send a sysex but can be called with note data !!?
		so it wraps writeShortMessage.
	*/
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

/* la fonction a l'air de marcher mais l'objet cree ne doit pas etre correct
			oData.copyFromArray(4, mSysex, 0, mLength);
*/
			int oTab[] = new int[length];
			for (int i = 0; i < length; i++)
			{
				oTab[i] = (int) (sysex[i] & 0xFF);
			}
			oData.addRawData(oTab);
			MIDISysexSendRequest oSysex = new MIDISysexSendRequest(oOut, oData);
			oSysex.send(MacOSXMidiWrapper.this);
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

	public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception
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
		logMidi(port, true, msg);
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
				}
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}
	}

}
//(setq c-basic-offset 8)
