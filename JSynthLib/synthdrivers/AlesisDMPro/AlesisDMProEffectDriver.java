package synthdrivers.AlesisDMPro;

import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

public class AlesisDMProEffectDriver extends Driver
{
    
   
   public AlesisDMProEffectDriver()
   {
   super("Effect", "Peter Hageus (peter.hageus@comhem.se)");
   sysexID="F000000E1906*";
   sysexRequestDump=new SysexHandler("F0 00 00 0E 19 07 *patchNum* F7");

   patchSize=36;    
   patchNameStart=0;
   patchNameSize=0;

   bankNumbers =new String[] {"Internal bank"};
   patchNumbers=new String[64]; 
   
   for (int i=0;i < 64;i++) {
       patchNumbers[i] = "Effect/Kit " + i;
   }

  }
public void storePatch (Patch p, int bankNum,int patchNum)
  {   
   setBankNum(bankNum);
   setPatchNum(patchNum);
   p.sysex[6] = (byte) patchNum;
   sendPatchWorker(p);
   setPatchNum(patchNum);
  }
public void sendPatch (Patch p)
  { 
   //DM Pro editbuffer is named 64
   p.sysex[6] = 64;
   sendPatchWorker(p);
  }
  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {
  }
  public Patch createNewPatch ()
 {
     
	 byte [] sysex = new byte[36];
         sysex[0] = (byte) 0xF0;
         sysex[1] = (byte) 0x00;
         sysex[2] = (byte) 0x00;
         sysex[3] = (byte) 0x0E;
         sysex[4] = (byte) 0x19;
         sysex[5] = (byte) 0x06;
         sysex[6] = (byte) 0x64;
         
         for (int i=7;i<34;i++) 
             sysex[i] = 0;
         
         sysex[35] = (byte) 0xF7;
         

	 Patch p = new Patch(sysex, this);
	 //setPatchName(p,"New Effect");
	 //calculateChecksum(p);	 
	 return p;
 }
public JSLFrame editPatch(Patch p)
 {
     return new AlesisDMProEffectEditor(p);
 }

}
