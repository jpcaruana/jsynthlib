package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.io.*;

public class KawaiK4MultiDriver extends Driver
{
  final static SysexHandler internalMultiSysexRequestDump = new 
   SysexHandler("F0 40 @@ 00 00 04 00 *patchNum* F7");

    public KawaiK4MultiDriver ()
   {
   manufacturer="Kawai";
   model="K4/K4r";
   patchType="Multi";
   id="K4";
   sysexID="F040**200004";
 //  inquiryID="F07E**06024000000400000000000f7";

   patchSize=77+9;
   patchNameStart=8;
   patchNameSize=10;
   deviceIDoffset=2;
   checksumStart=8;
   checksumEnd=77+6;
   checksumOffset=77+7;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"A-1","A-2","A-3","A-4","A-5","A-6","A-7","A-8",
                              "A-9","A-10","A-11","A-12","A-13","A-14","A-15","A-16",   
                              "B-1","B-2","B-3","B-4","B-5","B-6","B-7","B-8",
                              "B-9","B-10","B-11","B-12","B-13","B-14","B-15","B-16",   
                              "C-1","C-2","C-3","C-4","C-5","C-6","C-7","C-8",
                              "C-9","C-10","C-11","C-12","C-13","C-14","C-15","C-16",   
                              "D-1","D-2","D-3","D-4","D-5","D-6","D-7","D-8",
                              "D-9","D-10","D-11","D-12","D-13","D-14","D-15","D-16"};  

  }
public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   setPatchNum(patchNum);
   try {Thread.sleep(100); } catch (Exception e){}
   p.sysex[3]=(byte)0x20;
   p.sysex[7]=(byte)(patchNum+0x40);
   sendPatchWorker(p);
   try {Thread.sleep(100); } catch (Exception e){}   
   setPatchNum(patchNum);
  }
public void sendPatch (Patch p)
  { 
   p.sysex[3]=(byte)0x23;
   p.sysex[7]=(byte)0x40;
   sendPatchWorker(p);
  }
  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
    int i;
    int sum=0;
  
    for (i=start;i<=end;i++)
      sum+=p.sysex[i];
    sum+=0xA5;
    p.sysex[ofs]=(byte)(sum % 128);
  }

  public Patch createNewPatch()
 {
	 byte [] sysex = new byte[77+9];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x20;sysex[4]=(byte)0x00;
         sysex[5]=(byte)0x04; sysex[6]=(byte)0x0;sysex[7]=0x40;sysex[77+8]=(byte)0xF7;
         for (int i=0;i<8;i++)
         {
             sysex[12+6+8+ i*8]=24;
         sysex[12+7+8+ i*8]=50;
         }
         Patch p = new Patch(sysex);
	 p.ChooseDriver();
	 setPatchName(p,"New Patch");
	 calculateChecksum(p);	 
	 return p;
 }
 
public JInternalFrame editPatch(Patch p)
 {
     return new KawaiK4MultiEditor(p);
 }
 

  public void requestPatchDump(int bankNum, int patchNum) {
    byte[] sysex = internalMultiSysexRequestDump.toByteArray((byte)channel, patchNum+0x40);
   
    SysexHandler.send(port, sysex);
  }

}

