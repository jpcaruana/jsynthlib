/*This handles Midi I/O under Linux-like Operating Systems. Basically it sends all output to a file (which should really
be a device pipe, and reads from a file (device pipe) to get input.*/
package core;
import java.io.*;
import javax.swing.*;
import java.util.*;
public class LinuxMidiWrapper extends MidiWrapper
{
    int faderInPort;
    volatile int currentInPort;
    volatile int currentOutPort;
    byte[] midiBuffer;
    int writePos;
    int readPos;
    static OutputStream []outStream;
    static InputStream []inStream;
    static ArrayList midiDevList = new ArrayList();
    Thread thread;
    byte lastStatus;
    boolean sysEx=false;
    final int bufferSize=1024;
    InputThread keyboardThread;
    
    
    static {
        loadDeviceNames();
        outStream = new OutputStream[midiDevList.size()];
        inStream = new InputStream[midiDevList.size()];
      System.out.println("LinuxMidiWrapper:static:Number of devices:"+midiDevList.size());
        for (int i=0;i<midiDevList.size();i++) {
            File file = new java.io.File( (String) midiDevList.get(i));
            try{
            RandomAccessFile pipe = new java.io.RandomAccessFile(file , "rw");            
            inStream[i] = new java.io.FileInputStream( pipe.getFD() );            
            outStream[i] = new java.io.FileOutputStream( pipe.getFD() );
            outStream[i] = new BufferedOutputStream(outStream[i] ,4100);
            }
            catch (Exception e) {e.printStackTrace ();}
        }
        
    }
    public  LinuxMidiWrapper(int inport, int outport) throws Exception {
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
        thread = new Thread(keyboardThread);
        thread.start();
    }
    public  LinuxMidiWrapper() throws Exception {
        this(0,0);
        System.out.println("Debugging: LinuxMidiWrapper Constructor called w/ no params -- shouldn't happen");
        
    }
    
    private static void loadDeviceNames() {
        String line;
        try {
            FileReader fileReader = new FileReader("linuxdevices.conf");
            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
            while ((line = lineNumberReader.readLine())!=null)
            { midiDevList.add(line);}
        } catch (Exception e)
        {JOptionPane.showMessageDialog (null, "Unable to read from 'linuxdevices.conf'!.","Error", JOptionPane.ERROR_MESSAGE);};
    }
    
    
    //FIXME: Never call this even though its public, I need to call it from prefsDialog
    //to work around a JavaMIDI bug though.
    public  void setInputDeviceNum (int port) throws Exception
    {
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
        thread.stop();
        readPos=writePos;
        thread = new Thread(keyboardThread);
        thread.start();
    }
    protected  void setOutputDeviceNum (int port) throws Exception
    {
        if (port == currentOutPort) return;
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
    public  void writeLongMessage (int port,byte []sysex,int length) throws Exception
    {
        setOutputDeviceNum (port);
        if (outStream[port]!=null)
        {
        outStream[port].write (sysex,0,length);
        outStream[port].flush ();
        logMidi(port,false,sysex,length);
        }
    }
    public void writeShortMessage (int port, byte b1, byte b2) throws Exception
    {
        setOutputDeviceNum (port);
        outStream[port].write (b1);
        outStream[port].write (b2);
        outStream[port].flush ();
    }
    public void writeShortMessage (int port,byte b1, byte b2,byte b3) throws Exception
    {
        setOutputDeviceNum (port);
        outStream[port].write (b1);
        outStream[port].write (b2);
        outStream[port].write (b3);
        outStream[port].flush ();
    }
    
    public  int getNumInputDevices () throws Exception
    {return midiDevList.size ();}
    public  int getNumOutputDevices ()throws Exception
    {return midiDevList.size ();}
    public  String getInputDeviceName (int port)throws Exception
    {return (String)midiDevList.get (port);}
    public  String getOutputDeviceName (int port)throws Exception
    {return (String)midiDevList.get (port);}
    
    public  int messagesWaiting (int port)throws Exception
    {
        setInputDeviceNum (port);
        if (readPos!=writePos) return 1; else return 0;
    }
    public  int readMessage (int port,byte []sysex,int maxSize) throws Exception
    {
        byte statusByte;
        int msgLen=0;
        int i=0;int temp=writePos;
        if (sysEx==true)
        {
            while ((midiBuffer[readPos]!=-9) && (readPos!=temp) &&
            (i<maxSize-1))
            {
                sysex[i++]=midiBuffer[readPos++];
                if (midiBuffer[readPos-1]==-16) i--; //kludge to kill extraneus 0xF0's
                readPos%=bufferSize;
                
            }
            if ((midiBuffer[readPos]==-9) && (readPos!=temp))
            {
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
        if (statusByte>=0)
        {statusByte=lastStatus;readPos--;}
        switch (statusByte&0xF0)
        {
            case 0x80:case 0x90:case 0xA0:case 0xB0: case 0xE0: msgLen=3; break;
            case 0xC0:case 0xD0: msgLen=2;break;
            case 0xF0: sysEx=true;msgLen=1;break;
        }
        lastStatus=statusByte;
        sysex[0]=statusByte;
        temp=writePos;
        if (temp<readPos) temp+=bufferSize-1;
        temp-=readPos;
        
        while (temp<msgLen-1)
        {Thread.yield ();
         temp=writePos; if (temp<readPos) temp+=bufferSize; temp-=readPos;
        }  //if msg not complete-- get more
        for (i=1;i<msgLen;i++) sysex[i]=midiBuffer[(readPos++)%bufferSize];
        readPos%=bufferSize;
        logMidi(port,true,sysex,msgLen);        
        return msgLen;
    }
    
    public void close() {
        super.close();
        thread.stop();
        try {
           // outStream[currentOutPort].close();
            //inStream[currentInPort].close();
        }
        catch (Exception e)
        {
        };
        
    }
    
   public  void writeLongMessage (int port,byte []sysex)throws Exception
  {writeLongMessage (port,sysex,sysex.length);}   
    
    private class InputThread implements Runnable
    {
        public void run ()
        {
            byte i;
            while (true)
            {
                try
                {
                    if (inStream[currentInPort]==null)
                        break;
                    while (true)
                    {
                        i=(byte)inStream[currentInPort].read ();
                        if ((i!=-1)&&(i!=-2)&&(i!=0xFE) && (i!=0xF8))     //Ignore Active Sensing & Timing
                        {midiBuffer[writePos]=i;writePos++;
                         writePos%=bufferSize;}}
                } catch (Exception ex)
                {System.out.println ("Error");
                };
            }
        }
    }
}

