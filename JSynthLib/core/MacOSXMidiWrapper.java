
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
	
	private MIDIInputPort mInput;
	private MIDIOutputPort mOutput;

	/** contains List of MIDIData by port name */
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
	
	
	public MacOSXMidiWrapper(int inport, int outport) throws Exception 
	{
//		System.out.println("new MacOSXMidiWrapper inport = "+inport+" outport = "+outport);
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
				// device input
				mInput = mClient.inputPortCreate(new CAFString(getInputDeviceName(mInportNumber)), new ReadProcImpl());
				MIDIEndpoint oIn = MIDISetup.getSource(mInportNumber);
				mInput.connectSource(oIn);
				
				// device output
				mOutput = mClient.outputPortCreate(new CAFString(getInputDeviceName(mOutportNumber)));

				// controller device
				MIDIInputPort oController = mClient.inputPortCreate(new CAFString(getInputDeviceName(0)), new ReadProcImpl());
				MIDIEndpoint oInContr = MIDISetup.getSource(0);
				oController.connectSource(oInContr);				
				
				mInitDone = true;
//				System.out.println("Init Done");
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
						mLockSysexSend.wait(100);
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
		private int port;
		private byte[] sysex;
		private int length;
		/**
			Send sysex message
		*/
		WriteLongMessage(int aport,byte []asysex,int alength)
		{
			port = aport;
			sysex = asysex;
			length = alength;
		}
		public void run() 
		{
//			System.out.println("WriteLongMessage");
			try
			{
				// send sysex
				MIDIEndpoint oOut = MIDISetup.getDestination(port);
				MIDIData oData = MIDIData.newMIDIPacketData(length); 
				int oTab[] = new int[length];
				for (int i = 0; i < length; i++)
				{
					oTab[i] = (int)sysex[i];
//					System.out.print(" "+oTab[i]);
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
				MIDIPacketList oPaquetList = new MIDIPacketList();
				MIDIData oData = MIDIData.newMIDIChannelMessage(mData[0], mData[1], mData[2]);
				oPaquetList.add(0, oData);
				send(mPort, oPaquetList);
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
	
	/** Send method for short messages */
	private void send(int port, MIDIPacketList aPktList) throws Exception
	{
		MIDIEndpoint oOut = MIDISetup.getDestination(port);
		mOutput.send(oOut, aPktList);
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
		List oList = (List)mReceivedDataMap.get(getInputDeviceName (port));
		if (oList != null)
		{
			if (oList.size() > 0)
			{
//		System.out.println("messagesWaiting port = "+port+" len = "+oList.size());
				return oList.size();
			}
		}
		return 0;
	}

	/** get the older data packet received */ 
	public int readMessage (int port,byte []sysex,int maxSize) throws Exception
	{
		List oList = (List)mReceivedDataMap.get(getInputDeviceName(port));
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
				String oPortName = port.getStringProperty(MIDIConstants.kMIDIPropertyName).asString();
//				System.out.println("ReadProcImpl "+oPortName+" nb pkt = "+list.numPackets());
				for (int i = 0; i < list.numPackets(); i++)
				{
					MIDIPacket oPkt = list.getPacket(i);
					MIDIData oData = oPkt.getData();
					List oList = (List)mReceivedDataMap.get(oPortName);
					if (oList == null)
					{
						// Vector for sync need
						oList = new Vector();
						mReceivedDataMap.put(oPortName, oList);
					}
//					System.out.println("add DATA len = "+oData.getMIDIDataLength());
					byte[] oArray = new byte[oData.getMIDIDataLength()];
					oData.copyToArray(0,oArray, 0, oData.getMIDIDataLength());
					oList.add(oArray);
				}
			}
			catch(CAException cae)
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

