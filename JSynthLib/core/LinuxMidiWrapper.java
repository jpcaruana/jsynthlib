/*This handles Midi I/O under Linux-like Operating Systems. Basically it sends all output to a file (which should really
be a device pipe, and reads from a file (device pipe) to get input.*/
package core; //TODO org.jsynthlib.midi;

import java.io.*;
import javax.swing.*;
import java.util.*;

public class LinuxMidiWrapper extends MidiWrapper {
	int faderInPort;
	volatile int currentInPort;
	volatile int currentOutPort;
	byte[] midiBuffer;
	int writePos;
	int readPos;
	/*static*/ OutputStream []outStream;
	/*static*/ InputStream []inStream;
	/*static*/ ArrayList midiDevList = new ArrayList();
	// Thread thread;
	byte lastStatus;
	boolean sysEx=false;
	final int bufferSize=1024;
	InputThread keyboardThread;
    
	// This used to be the unnamed "static" method that got run when the overall
	// application ran. Now, it's the default constructor... so it only gets run
	// (and only complains about not finding linuxdevices.conf) when actually running
	// on a Linux system - emenaker 2003.03.12    
	public LinuxMidiWrapper() throws Exception {
		loadDeviceNames();
		outStream = new OutputStream[midiDevList.size()];
		inStream = new InputStream[midiDevList.size()];
		System.out.println("LinuxMidiWrapper:static:Number of devices:"+midiDevList.size());
		for (int i=0;i<midiDevList.size();i++) {
			File file = new java.io.File( (String) midiDevList.get(i));
			try {
				RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");            
				inStream[i] = new java.io.FileInputStream( pipe.getFD() );            
				outStream[i] = new java.io.FileOutputStream( pipe.getFD() );
				outStream[i] = new BufferedOutputStream(outStream[i] ,4100);
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
	}
	
	// This used to be the constructor with (int,int) params
	// I needed to change the (int,int) constructor to some non-constructor thing, because I neede to
	// have an instance of every eligible midi wrapper ahead of time. - emenaker 2003.03.12
	public void init(int inport, int outport) throws Exception {
        // loadDeviceNames ();
		currentOutPort=outport;
		currentInPort=inport;
/*
        outStream = new OutputStream[getNumInputDevices ()];
        inStream = new InputStream[getNumInputDevices ()];
        File file = new java.io.File ( (String) midiDevList.get (outport));
        RandomAccessFile pipe = new java.io.RandomAccessFile (file , "rw");
 
        outStream[outport] = new java.io.FileOutputStream ( pipe.getFD () );
        inStream[inport] = new java.io.FileInputStream ( pipe.getFD () );
        outStream[outport] = new BufferedOutputStream (outStream[outport] ,4100);
 */
		System.out.println("LinuxMidiWrapper Ctor inport "+inport+" outport "+outport);
		midiBuffer = new byte[bufferSize];
		writePos=0;
		readPos=0;
		keyboardThread = new InputThread();
        // thread = new Thread(keyboardThread);
		keyboardThread.start();
	}

/*   commented out by emenaker 3/12/2003
	public  LinuxMidiWrapper() throws Exception {
        // this(0,0);
        // System.out.println("Debugging: LinuxMidiWrapper Constructor called w/ no params -- shouldn't happen");
    }
*/
	private /*static*/ void loadDeviceNames() throws Exception {
		String line;
		try {
			FileReader fileReader = new FileReader("linuxdevices.conf");
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
			while ((line = lineNumberReader.readLine())!=null) {
				midiDevList.add(line);
			}
		} catch (Exception e) {
			throw new Exception("Unable to read from 'linuxdevices.conf'");
		}
	}
    
    //FIXME: Never call this even though its public, I need to call it from prefsDialog
    //to work around a JavaMIDI bug though.
	public  void setInputDeviceNum (int port) throws Exception {
		if (port == currentInPort) return;
		currentInPort=port;
        /*
        if (inStream[port]==null)
        { File file = new java.io.File( (String) midiDevList.get(port));
          RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");
          outStream[port] = new java.io.FileOutputStream( pipe.getFD() );
          outStream[port] = new BufferedOutputStream( outStream[port] ,4100);
          inStream[port] = new java.io.FileInputStream( pipe.getFD() );
        }
         */
		 // thread.stop() is deprecated. I switched this to thread.stopRunning
		keyboardThread.stopRunning();
		readPos=writePos;
        //thread = new Thread(keyboardThread);
		keyboardThread.restartRunning();
	}
    
	//FIXME Made public so that PrefsDialog can call it until we straighten this mess out - emenaker 3/12/2003
	public void setOutputDeviceNum (int port) throws Exception {
		if (port == currentOutPort)
			return;
		currentOutPort=port;
        //  outStream.close();
        /*
        if (outStream[port]!=null) return;
        File file = new java.io.File( (String) midiDevList.get(port));
        RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");
        outStream[port] = new java.io.FileOutputStream( pipe.getFD() );
        outStream[port] = new BufferedOutputStream( outStream[port] ,4100);
        inStream[port]= new java.io.FileInputStream(pipe.getFD());
         */
	}

	public  void writeLongMessage (int port,byte []sysex,int length) throws Exception {
		setOutputDeviceNum (port);
		if (outStream[port]!=null) {
			outStream[port].write (sysex,0,length);
			outStream[port].flush ();
			logMidi(port,false,sysex,length);
		}
	}

	public void writeShortMessage (int port, byte b1, byte b2) throws Exception {
		setOutputDeviceNum (port);
		outStream[port].write (b1);
		outStream[port].write (b2);
		outStream[port].flush ();
    }

	public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception {
		setOutputDeviceNum (port);
		outStream[port].write (b1);
		outStream[port].write (b2);
		outStream[port].write (b3);
		outStream[port].flush ();
    }
    
    public  int getNumInputDevices () throws Exception {
    	return midiDevList.size ();
    }
    
	public  int getNumOutputDevices ()throws Exception {
		return midiDevList.size ();
	}

	public  String getInputDeviceName (int port)throws Exception {
    	return (String)midiDevList.get (port);
    }

	public  String getOutputDeviceName (int port)throws Exception {
		return (String)midiDevList.get (port);
	}
    
	public  int messagesWaiting (int port)throws Exception {
		setInputDeviceNum (port);
		if (readPos!=writePos) return 1; else return 0;
	}
    
	public  int readMessage (int port,byte []sysex,int maxSize) throws Exception {
		byte statusByte;
		int msgLen=0;
		int i=0;
		int temp=writePos;
		if (sysEx==true) {
			while ((midiBuffer[readPos]!=-9) && (readPos!=temp) && (i<maxSize-1)) {
				sysex[i++]=midiBuffer[readPos++];
				if (midiBuffer[readPos-1]==-16) i--; //kludge to kill extraneus 0xF0's
				readPos%=bufferSize;
            }
			if ((midiBuffer[readPos]==-9) && (readPos!=temp)) {
				sysex[i++]=midiBuffer[readPos++];
				readPos%=bufferSize;
				sysEx=false;
				logMidi(port,true,sysex,i);
				return i;
			}
			logMidi(port,true,sysex,i);
			return i;
		}
        
		statusByte=midiBuffer[readPos++];
		if (statusByte>=0) {
			statusByte=lastStatus;readPos--;
		}
		switch (statusByte&0xF0) {
			case 0x80:case 0x90:case 0xA0:case 0xB0: case 0xE0: msgLen=3; break;
			case 0xC0:case 0xD0: msgLen=2;break;
			case 0xF0: sysEx=true;msgLen=1;break;
		}
		lastStatus=statusByte;
		sysex[0]=statusByte;
		temp=writePos;
		if (temp<readPos)
			temp+=bufferSize-1;
		temp-=readPos;
        
		while (temp<msgLen-1) {
			keyboardThread.yield ();
         	temp=writePos;
         	if (temp<readPos)
         		temp+=bufferSize;
       		temp-=readPos;
		}  //if msg not complete-- get more
		for (i=1;i<msgLen;i++)
			sysex[i]=midiBuffer[(readPos++)%bufferSize];
		readPos%=bufferSize;
		logMidi(port,true,sysex,msgLen);        
		return msgLen;
	}
    
	public void close() {
		keyboardThread.stopRunning();
		try {
			// outStream[currentOutPort].close();
			//inStream[currentInPort].close();
		} catch (Exception e) {
		}
    }
    
	public  void writeLongMessage (int port,byte []sysex)throws Exception {
		writeLongMessage (port,sysex,sysex.length);
	}

	public String getWrapperName() {
		return("GNU/Linux");
	}
    
    // This runs on Linux, and nothing else
	public static boolean supportsPlatform(String platform) {
		if(platform.length()==0 || platform.equals("Linux")) {
			return(true);
		}
		return(false);
	}

	// This inner class used to implement Runnable but I changed it so that it
	// extends Thread so that we can avoid having to do "new Thread(new InputThread())"
	// in the LinuxMidiWrapper class. Besides, the option for implementing Runnable
	// was created for cases where you were already extending another class and
	// couldn't extend Thread - emenaker 2003.03.12
	private class InputThread extends Thread {
		boolean running;

		public void run () {
			running = true;
			byte i;
			while (running) {
				try {
					if (inStream[currentInPort]==null)
						break;
					while (running) {
						/* TODO I imagine that this might be chewing up a lot of CPU time if it's
						 * contantly polling. We might want to add something here where that does
						 * a short sleep() if this loop cycles some number of times without seeing
						 * any input. - emenaker 2003.03.12
						 */
						i=(byte)inStream[currentInPort].read ();
						if ((i!=-1)&&(i!=-2)&&(i!=0xFE) && (i!=0xF8)) {    //Ignore Active Sensing & Timing
							midiBuffer[writePos]=i;writePos++;
							writePos%=bufferSize;
						}
					}
				} catch (Exception ex) {
					System.out.println ("Error");
				}
			}
		}
		/**
		* Tells the InputThread to stop polling the midi input port
		* We need this if we are to stop using thread.stop(), which is deprecated  - emenaker 2003.03.12
		*/
		void stopRunning() {
			running = false;
		}
		/**
		* Tells the InputThread to restart polling the midi input port
		* We need this if we are to restart using thread.start(), which isn't working furthermore - ttittmann 05 january 2004
		*/
		void restartRunning() {
			running = true;
		}

	}
}

