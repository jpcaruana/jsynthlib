package synthdrivers.BossDR660;
import core.*;
import java.io.*;
import javax.swing.*;

public class BossDR660DrumkitDriver extends Driver
{
  int [] xvrt;
   public BossDR660DrumkitDriver()
   {
   manufacturer="Boss";
   model="DR660";
   patchType="Drumkit";
   id="DR660";
   sysexID="F041**5212";
   patchNameStart=1378;
   patchNameSize=7;
   deviceIDoffset=2;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"0","1","2","3","4","5","6","7",
                              "8","9","10","11","12","13","14","15",
                              "16","17","18","19","20","21","22","23",
                              "24","25","26","27","28","29","30","31", 
			      "32","33","34","35","36","37","38"};   
   xvrt = new int[] {0,8,16,24,25,32,40,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,
                               85,86,87,88,89,90,91,92,93,94,95};
   }
 public void setPatchNum(int patchNum)
  {
    try {       
         
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0xC0+(channel-1)),(byte)xvrt[patchNum]);
	Thread.sleep(150);
    } catch (Exception e) {};    
  }			      
   
 public void calculateChecksum (Patch p)
   { for (int i=0;i<55;i++) 
	   {calculateChecksum(p,23*i+5,23*i+20,23*i+21);
	    p.sysex[i*23+2]=((byte)(channel-1));
	   }
     calculateChecksum(p,1265+5,1265+63,1265+64);
     p.sysex[1265+2]=((byte)(channel-1));
     calculateChecksum(p,1331+5,1331+21,1331+22);
     p.sysex[1331+2]=((byte)(channel-1));
     calculateChecksum(p,1355+5,1355+11,1355+12);
     p.sysex[1355+2]=((byte)(channel-1));
     calculateChecksum(p,1369+5,1369+15,1369+16);
     p.sysex[1369+2]=((byte)(channel-1));
     
   }                  

  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
    int i;
    int sum=0;
    for (i=start;i<=end;i++)
      sum+=p.sysex[i];
    p.sysex[ofs]=(byte)(sum % 128);
    p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
    p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    p.sysex[ofs]=(byte)(p.sysex[ofs]%128);
   
  }
   public void sendPatch (Patch p)
   { setPatchNum(0);
     sendPatchWorker (p);
     try {Thread.sleep(25);}catch (Exception e){};
 //    setPatchNum(0);
   } 

 public void storePatch (Patch p, int bankNum,int patchNum)
  {
//   setBankNum(bankNum);
   setPatchNum(patchNum);
   sendPatchWorker(p);
   try {Thread.sleep(25);} catch(Exception e) {};
   setPatchNum(patchNum);
 
  }
 public void setBankNum(int bankNum)
  {
  }
 public void playPatch(Patch p)
  {
     try {

        sendPatch(p);
        Thread.sleep(100); 
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x90+(channel-1)),(byte)36,(byte)127);
        Thread.sleep(100); 
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x80+(channel-1)),(byte)36,(byte)0);
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x90+(channel-1)),(byte)42,(byte)127);
        Thread.sleep(100); 
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x80+(channel-1)),(byte)42,(byte)0);
       PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x90+(channel-1)),(byte)38,(byte)127);
        Thread.sleep(100); 
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x80+(channel-1)),(byte)38,(byte)0);
       PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x90+(channel-1)),(byte)46,(byte)127);
        Thread.sleep(100); 
        PatchEdit.MidiOut.writeShortMessage(port,(byte)(0x80+(channel-1)),(byte)46,(byte)0); 
     } catch (Exception e){ErrorMsg.reportError("Error","Unable to Play Drums",e);}       
  }
public Patch createNewPatch()
  { 
  try {
    FileInputStream fileIn= new FileInputStream (new File("synthdrivers/BossDR660/BossDR660Drumkit.new"));
    byte [] buffer =new byte [1387];
    fileIn.read(buffer);
    fileIn.close();
    Patch p=new Patch(buffer);
    p.ChooseDriver();
    return p;
  }catch (Exception e) {ErrorMsg.reportError("Error","Unable to find Defaults",e);return null;}
  }
public JInternalFrame editPatch(Patch p)
 {
     return new BossDR660DrumkitEditor(p);
 }

}


