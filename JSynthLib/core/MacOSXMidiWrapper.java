
package core;

import java.io.*;
import javax.swing.*;
import java.util.*;

import com.apple.component.*;
import com.apple.audio.midi.*;
import com.apple.audio.hardware.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;
import com.apple.audio.*;



/**
 * Midi wrapper for MacOS X
 * @author Denis Queffeulou dqueffeulou@free.fr
 */
public class MacOSXMidiWrapper extends MidiWrapper 
	implements MIDICompletionProc
{
	private static List mInputNames = new ArrayList();
	private static List mOutputNames = new ArrayList();

	private static int mNumInputs;
	private static int mNumOutputs;

	private MIDIClient mClient;
	
	/** device output */
	private MIDIOutputPort mOutput;
	
	/** all inputs */
	private MIDIInputPort mInputs[];

	/** contains List of MIDIData by port (MIDIInputPort) */
	private Map mReceivedDataMap = new Hashtable();
	
	/** thread used to call CA api method */
	private ActionExecutor mActionExecutor;

	/** when init done... */		
	private boolean mInitDone = false;	
	
	private static boolean mLoadDone = false;
	
	/** object to wait on until current sysex send completes */
	private Object mLockSysexSend = new Object();
	
	/** sysex request counter */
	private int mSysexCount = 0;

	/** MIDI paquet for channel messages , it seems important for 
		handling the controller device data smoothy to create only one and reuse it
		because allocate takes too much time 
		*/	
	private MIDIPacketList mShortMessagePaquetList;
	
	
	
	public MacOSXMidiWrapper(int inport, int outport) throws Exception 
	{
		// creates executor thread
		mActionExecutor = new ActionExecutor();
		mActionExecutor.start();
		// initialisation
		mActionExecutor.setAction(new Init(inport, outport));
	}

	public MacOSXMidiWrapper() throws Exception {
		this(0,0);
	}
	
	
	/**
	 * Initialization method (in a separate thread)
	 */
	class Init implements Runnable
	{
		private int mInportNumber;
		private int mOutportNumber;
		Init(int inport, int outport)
		{
			mInportNumber = inport;
			mOutportNumber = outport;
		}
		public void run()
		{
			try
			{
				mClient = new MIDIClient(new CAFString("JSynthLib"), null);
				if (!mLoadDone)
				{
					loadDevicesNames();
					mLoadDone = true;
				}
				mInputs = new MIDIInputPort[mNumInputs];
				
				// device input
				mInputs[mInportNumber] = mClient.inputPortCreate(new CAFString(getInputDeviceName(mInportNumber)), new ReadProcImpl());
				MIDIEndpoint oIn = MIDISetup.getSource(mInportNumber);
				mInputs[mInportNumber].connectSource(oIn);
				
				// device output
				mOutput = mClient.outputPortCreate(new CAFString(getInputDeviceName(mOutportNumber)));

				mInitDone = true;
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}
	}

	/**
		Init for input read proc. Puts MIDIInputPort in mInputs.
	*/
	class InputInit implements Runnable
	{
		private int mInportNumber;
		InputInit(int inport)
		{
			mInportNumber = inport;
		}
		public void run()
		{
			try
			{
				// controller device
				mInputs[mInportNumber] = mClient.inputPortCreate(new CAFString(getInputDeviceName(mInportNumber)), new ReadProcImpl());
				MIDIEndpoint oInContr = MIDISetup.getSource(mInportNumber);
				mInputs[mInportNumber].connectSource(oInContr);			
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}
	}
	


	//FIXME: Never call this even though its public, I need to call it from prefsDialog
	//to work around a JavaMIDI bug though.
	public void setInputDeviceNum (int port) throws Exception
	{
//		System.out.println("setInputDeviceNum port = "+port);
	}

	protected void setOutputDeviceNum (int port) throws Exception
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
			synchronized(mLockSysexSend)
			{
				try {
					// wait until last sysex completes
//					System.out.println("mSysexCount = "+mSysexCount);
					while(mSysexCount > 0)
					{
//						System.out.println("wait for write");
						mLockSysexSend.wait(10/*100*/);
					}
					mSysexCount++;
					mActionExecutor.setAction(new WriteLongMessage(port, sysex, length));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * Separate thread is due to bug in Java CoreAudio that throw a IllegalMonitorStateException
	 */
	class WriteLongMessage implements Runnable
	{
		private int mPort;
		private byte[] mSysex;
		private int mLength;

		/**
			Send sysex message
		*/
		WriteLongMessage(int aport,byte []asysex,int alength)
		{
			mPort = aport;
			mSysex = asysex;
			mLength = alength;
		}
		public void run() 
		{
			try
			{
				// send sysex
				MIDIEndpoint oOut = MIDISetup.getDestination(mPort);
//				MIDIData oData = MIDIData.newMIDIPacketData(length); 
				MIDIData oData = MIDIData.newMIDIRawData(mLength); 
				
/* la fonction a l'air de marcher mais l'objet cree ne doit pas etre correct
				oData.copyFromArray(4, mSysex, 0, mLength);
*/				
				int oTab[] = new int[mLength];
				for (int i = 0; i < mLength; i++)
				{
					oTab[i] = (int)mSysex[i];
				}
				oData.addRawData(oTab);
				MIDISysexSendRequest oSysex = new MIDISysexSendRequest(oOut, oData);
				oSysex.send(MacOSXMidiWrapper.this);
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}
	}

	public  void writeLongMessage (int port,byte []sysex)throws Exception
	{
		writeLongMessage(port, sysex, sysex.length);
	}

	
	public void writeShortMessage (int port, byte b1, byte b2) throws Exception
	{
		mActionExecutor.setAction(new WriteShortMessage(port, b1, b2, (byte)0));
	}

	class WriteShortMessage implements Runnable
	{
		private int mPort;
		private int mData[] = new int[3];
		WriteShortMessage (int port,byte b1, byte b2,byte b3)
		{
			mPort = port;
			mData[0] = (int)b1;
			mData[1] = (int)b2;
			mData[2] = (int)b3;
		}
		public void run()
		{
			try
			{
				if (mShortMessagePaquetList == null)
				{
					mShortMessagePaquetList = new MIDIPacketList();
				}
				else
				{	// reuse the paquet
					mShortMessagePaquetList.init();
				}
				MIDIData oData = MIDIData.newMIDIChannelMessage(mData[0], mData[1], mData[2]);
				mShortMessagePaquetList.add(0, oData);
				MIDIEndpoint oOut = MIDISetup.getDestination(mPort);
				mOutput.send(oOut, mShortMessagePaquetList);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception
	{
//		System.out.println("writeShortMessage port = "+port+" b1="+b1+" b2="+b2+" b3= "+b3);
		mActionExecutor.setAction(new WriteShortMessage(port, b1, b2, b3));
	}
	
	public int getNumInputDevices () throws Exception
	{
		if (!mInitDone)
		{
			mActionExecutor.setAction(new Runnable()
			{	public void run() {
				try {
					mNumInputs = MIDISetup.getNumberOfSources();
				} catch(Exception e) {
					e.printStackTrace();
			}}
			});
		}
		return mNumInputs;
	}

	public int getNumOutputDevices ()throws Exception
	{
		if (!mInitDone)
		{
			mActionExecutor.setAction(new Runnable()
			{	public void run() {
				try {
					mNumInputs = MIDISetup.getNumberOfDestinations();
				} catch(Exception e) {
					e.printStackTrace();
			}}
			});
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
			mActionExecutor.setAction(new InputInit(port));
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

	public void close() 
	{
//		System.out.println("close");
		mActionExecutor.kill();
	}

	/**
		Load devices names into static variables
	*/
	private static void loadDevicesNames()
	{
		int oEntNum = 0;
		try {
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
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// **************  interface MIDICompletionProc *****************************
	
	/**
	 * Called on end of sysex sending
	 */
	public void execute(MIDISysexSendRequest request) 
	{
//		System.out.println("SYSEX sent IN");
		synchronized(mLockSysexSend)
		{
			mSysexCount--;
			mLockSysexSend.notifyAll();
		}
//		System.out.println("SYSEX sent OUT");
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
					oList.add(oArray);
				}
			}
			catch(Exception cae)
			{
				cae.printStackTrace();
			}
		}
	}

	/**
		Thread used to execute action with CA calls 
	*/
	class ActionExecutor extends Thread 
	{
		private List mAction = new Vector();
		private boolean mStop = false;
		/** execute the Runnable within the thread */
		synchronized void setAction(Runnable aAction)
		{
//			System.out.println("setAction "+aAction);
			mAction.add(aAction);
			notify();
		}
		/** stops the thread */
		synchronized void kill()
		{
			mStop = true;
			notify();
		}
		/**
			loop until action set, then run it.
		*/
		public void run()
		{
//			System.out.println("ActionExecutor start");
			while(true)
			{
				try
				{
					synchronized(this)
					{
//						System.out.println("Wait for action "+mAction.size());
						if (mAction.size() == 0)
						{
							wait();
						}
						if (mStop)
						{
							break;
						}
						while(mAction.size() > 0)
						{
							Runnable oRun = (Runnable)mAction.remove(0);
//							System.out.println("Run "+oRun);
							oRun.run();
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
//			System.out.println("ActionExecutor exit");
		}
	}	
}

