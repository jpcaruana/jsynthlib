package synthdrivers.KorgX3;
import core.*;
import javax.swing.*;
import java.io.*;

/**
 * This class is a single driver for Korg X3 -synthesizer to be used in
 * JSynthLib-program. Might work directly with Korg X2 as well.
 * Making drivers for N-series (N264, N364) should be an easy
 * task if one has the original reference guide.
 *
 * Known issues: Midi channel is partly fixed to 0. This is
 * set in the third byte to be send, actually it's four
 * lower bits is exacty the channel to be used
 *
 * @author Juha Tukkinen
 * @version $Id$
 */
public class KorgX3SingleDriver extends Driver
{
  // contains unneeded data: F0 42 30 35 23 f7 f0 42 30 35 40  
  public static final int EXTRA_HEADER=23;

  /**
   * Default constructor. Initialize default values for
   * class variables.
   */
  public KorgX3SingleDriver()
  {
    super ("Single","Juha Tukkinen");
    sysexID="F042**35";

    // program parameter dump request:
    sysexRequestDump=new SysexHandler("F0 42 30 35 10 F7");
    // ##TODO## note that                    30: 0 is midi channel
    // size after conversion:
    patchSize=164+EXTRA_HEADER; // patch size is 164 bytes
    patchNameStart=EXTRA_HEADER; 
    patchNameSize=10; //OK
    deviceIDoffset=2; //OK
    //    checksumStart=8;
    //checksumEnd=137;
    //checksumOffset=138;
    bankNumbers = new String[]
      {"Bank A","Bank B"};
    patchNumbers=new String[]
      {"00-","01-","02-","03-","04-","05-","06-","07-",
       "08-","09-","10-","11-","12-","13-","14-","15-",
       "16-","17-","18-","19-","20-","21-","22-","23-",
       "24-","25-","26-","27-","28-","29-","30-","31-",
       "32-","33-","34-","35-","36-","37-","38-","39-",
       "40-","41-","42-","43-","44-","45-","46-","47-",
       "48-","49-","50-","51-","52-","53-","54-","55-",
       "56-","57-","58-","59-","60-","61-","62-","63-",
       "64-","65-","66-","67-","68-","69-","70-","71-",
       "72-","73-","74-","75-","76-","77-","78-","79-",
       "80-","81-","82-","83-","84-","85-","86-","87-",
       "88-","89-","90-","91-","92-","93-","94-","95-",
       "96-","97-","98-","99-"};

  }

  /**
   * Overrided setPatchNum. Sets the appropriate active patchnumber 
   *
   * @param patchNum Patch number
   */
  public void setPatchNum (int patchNum)
  {
    try
      {
	byte[] programMode = {(byte)0x4E, (byte)0x02, (byte)0x00, (byte)0xF7};
	byte[] programEditMode = {(byte)0x4E, (byte)0x03, (byte)0x00, (byte)0xF7};
	// go to PROGRAM mode
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)0xF0,(byte)0x42);
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)(0x30+(getChannel()-1)),(byte)0x35);
	PatchEdit.MidiOut.writeLongMessage(getPort(), programMode);
	try {Thread.sleep(100); } catch (Exception e){}   
	// set patch
	PatchEdit.MidiOut.writeShortMessage (getPort(),(byte)(0xC0+(getChannel()-1)),(byte)patchNum,(byte)0xF7);
	try {Thread.sleep(100); } catch (Exception e){}   
	// go to PROGRAM EDIT mode
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)0xF0,(byte)0x42);
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)(0x30+(getChannel()-1)),(byte)0x35);
	PatchEdit.MidiOut.writeLongMessage(getPort(), programEditMode);
	try {Thread.sleep(100); } catch (Exception e){}   
      } catch (Exception e) {};
  }
  
  /**
   * Overrided setBankNum. Sets the appropriate active bank
   *
   * @param bankNum Bank number
   */
  public void setBankNum(int bankNum)
  {
    try
      {
	byte[] programMode = {(byte)0x4E, (byte)0x02, (byte)0x00, (byte)0xF7};
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)0xF0,(byte)0x42);
	PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)(0x30+(getChannel()-1)),(byte)0x35);
	PatchEdit.MidiOut.writeLongMessage(getPort(), programMode);
	
	// change bank
	try {Thread.sleep(100); } catch (Exception e){} 
	PatchEdit.MidiOut.writeShortMessage (getPort(),(byte)(0xB0+(getChannel()-1)),(byte)0x00,(byte)(0x00)); //MSB
	PatchEdit.MidiOut.writeShortMessage (getPort(),(byte)(0xB0+(getChannel()-1)),(byte)0x20,(byte)(0x00+bankNum)); //LSB
	try {Thread.sleep(100); } catch (Exception e){} 
      } catch (Exception e) {};
  }

  /**
   * Overrided storePatch. Sends a patch to the current edit buffer
   * on the synthesizer and saves the patch.
   *
   * @param p Patch to be sent
   * @param bankNum Bank number
   * @param patchNum Patch number
   */
  public void storePatch (Patch p, int bankNum,int patchNum)
  {   
    setBankNum(bankNum);
    setPatchNum(patchNum);
    try {Thread.sleep(100); } catch (Exception e){}
    sendPatchWorker(p);
    try {Thread.sleep(100); } catch (Exception e){}   

    byte[] programWriteRequest = {(byte)0x11, (byte)bankNum, (byte)patchNum, (byte)0xF7};
    try {
      PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)0xF0,(byte)0x42);
      PatchEdit.MidiOut.writeShortMessage(getPort(),(byte)(0x30+(getChannel()-1)),(byte)0x35);
      PatchEdit.MidiOut.writeLongMessage(getPort(), programWriteRequest);                     
    } catch (Exception e) {
      ErrorMsg.reportError("Error", "Error with patch storing",e);
    };
  }
  
  /**
   * Overrided sendPatch. Sends the patch to currenty open edit buffer.
   * 
   * @param p Patch to be sent
   */
  /*
  public void sendPatch (Patch p)
  { 
    sendPatchWorker(p);
  }
  */
  /**
   * Overrided sendPatchWorker. Actually sends the patch to the synthesizer.
   *
   * @param p Patch to be sent
   */
  //protected void sendPatchWorker (Patch p)
  protected void sendPatch (Patch p)
  {
    // ##TODO## first go to program edit mode so that the edit buffer will be active.
    // then write PROGRAM PARAMETER DUMP, F0423g3540[data]F7
    // [data] is 164 bytes converted to 188 bytes

    byte[] pd = new byte[188+6];
    pd[0] = (byte)0xF0;
    pd[1] = (byte)0x42;
    pd[2] = (byte)((byte)0x30+(byte)(getChannel()-1));
    pd[3] = (byte)0x35;
    pd[4] = (byte)0x40; // program parameter dump
    pd[193] = (byte)0xF7;
    // p.sysex[EXTRA_HEADER] has the first byte
    int j = 0; // destination sysex position counter
    for(int i = 0; i < 164; ) {
      byte b7 = (byte)0x00;
      for(int k = 0; k < 7; k++) {
	if(i + k < 164) {
	  b7 += (p.sysex[i+k+EXTRA_HEADER]&128)>>(7-k);
	  pd[j+k+1+5] = (byte)(p.sysex[i+k+EXTRA_HEADER] & (byte)0x7F);
	} 
      }
      pd[j+5] = b7;
      j+=8;
      i+=7;
    }
    
    try {
      PatchEdit.MidiOut.writeLongMessage (getPort(),pd);
    } catch (Exception e) { 
      ErrorMsg.reportStatus (e); 
    }
  }

  /**
   * Creates a new empty patch with name 'Init'.
   *
   * @return A new empty patch
   */
  public Patch createNewPatch ()
  {
    byte[] sysex = new byte[187];
    sysex[0]=(byte)0xF0; sysex[1]=(byte)0x42;sysex[2]=(byte)0x30;sysex[3]=(byte)0x35;
    sysex[53]=(byte)0x32; sysex[60]=(byte)0x02;sysex[65]=(byte)0x32;sysex[73]=(byte)0x63;
    sysex[74]=(byte)0x3C; sysex[88]=(byte)0x32;sysex[89]=(byte)0x3C;sysex[95]=(byte)0x63;
    sysex[97]=(byte)0x63; sysex[99]=(byte)0x63;sysex[105]=(byte)0x99;sysex[109]=(byte)0x0f;
    sysex[112]=(byte)0x32;sysex[120]=(byte)0x63;sysex[121]=(byte)0x3C;sysex[135]=(byte)0x32; 
    sysex[136]=(byte)0x3C;sysex[142]=(byte)0x63;sysex[144]=(byte)0x63;sysex[146]=(byte)0x63; 
    sysex[152]=(byte)0x99;sysex[156]=(byte)0x0F;sysex[164]=(byte)0x65;sysex[165]=(byte)0x01;
    for(int i=23;i<=32;i++) {
      sysex[i]=(byte)0x20;
    }
    
    Patch p = new Patch(sysex, this);

    setPatchName(p,"Init");
  
    return p;
  }

  /**
   * Overrided editPatch. Returns an editor window for this patch.
   *
   * @param p Patch to be edited
   * @return Editor window
   */
  public JSLFrame editPatch(Patch p)
  {
    return new KorgX3SingleEditor(p);
  }

}

