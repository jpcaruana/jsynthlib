/*
 * @version $Id$
 */
package synthdrivers.YamahaTG33;
import core.Driver;
import core.ErrorMsg;
import core.IPatch;
import core.Patch;

public class YamahaTG33SingleDriver extends Driver
{

   public YamahaTG33SingleDriver()
   {
   super ("Single","Brian Klock");
   sysexID="F043**7E****4C4D2020303031325645";
   patchNameStart=28;
   patchNameSize=8;
   deviceIDoffset=2;
   checksumStart=6;
   checksumEnd=602;
   checksumOffset=603;
   bankNumbers =new String[] {"0-Internal","1-Card1","2-Preset1","3-Unused",
                              "4-Card2","5-Preset2"};
   patchNumbers=new String[] {"11","12","13","14","15","16","17","18",
                              "21","22","23","24","25","26","27","28",
                              "31","32","33","34","35","36","37","38",
                              "41","42","43","44","45","46","47","48", 
                              "51","52","53","54","55","56","57","58",
                              "61","62","63","64","65","66","67","68",
                              "71","72","73","74","75","76","77","78",
                              "81","82","83","84","85","86","87","88"};
   
  }
public void storePatch (IPatch p, int bankNum,int patchNum)
  {   

   setBankNum(bankNum);
   setPatchNum(patchNum);
   sendPatch(p);
   try{

   Thread.sleep(100);
   send(new byte[] {(byte)0xF0,(byte)0x43,(byte)0x16,(byte)0x26,(byte)0x07,(byte)0x09,(byte)0xF7});
   Thread.sleep(100);
   send(new byte[] {(byte)0xF0,(byte)0x43,(byte)0x16,(byte)0x26,(byte)0x07,(byte)0x03,(byte)0xF7});
   Thread.sleep(100);
   send(new byte[] {(byte)0xF0,(byte)0x43,(byte)0x16,(byte)0x26,(byte)0x07,(byte)0x04,(byte)0xF7});
   }catch (Exception e){ErrorMsg.reportError("Error","Unable to Store Patch",e);}
  }
public IPatch createNewPatch()
 {
	 byte [] sysex = new byte[605];
      sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
     sysex[03]=(byte)0x7E;sysex[04]=(byte)0x04;sysex[05]=(byte)0x55;
     sysex[06]=(byte)0x4C;sysex[07]=(byte)0x4D;sysex[8]=(byte)0x20;
     sysex[9]=(byte)0x20;sysex[10]=(byte)0x30;sysex[11]=(byte)0x30;
     sysex[12]=(byte)0x31;sysex[13]=(byte)0x32;sysex[14]=(byte)0x56;
     sysex[15]=(byte)0x45;  sysex[604]=(byte)0xF7;     
	IPatch p = new Patch(sysex, this);
	   setPatchName(p,"NewPatch");
	 calculateChecksum(p);	 
	 return p;
 }
}

