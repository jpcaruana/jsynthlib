/*
 * @version $Id$
 */
package synthdrivers.EmuProteusMPS;
import core.*;

import java.io.*;
import javax.swing.*;

public class EmuProteusMPSSingleDriver extends Driver
{
final static SysexHandler SysexRequestDump = new SysexHandler("F0 18 08 00 00 *bankNum* *patchNum* F7");

  public EmuProteusMPSSingleDriver()
   {
   super ("Single","Brian Klock");
   sysexID= "F01808**01";
   //inquiryID="F07E**06021804040800*********F7";
   patchSize=319;
   patchNameStart=7;
   patchNameSize=12;
   deviceIDoffset=3;
   checksumStart=7;
   checksumEnd=316;
   checksumOffset=317;
   bankNumbers =new String[] {"0-ROM Bank","1-RAM Bank","2-Card Bank",
                              "3-ROM Bank","4-ROM Bank"};
   patchNumbers = generateNumbers(0,99,"00-");
   
   }
                     
   public String getPatchName(Patch p) {
         if (patchNameSize==0) return ("-");
         try {
           StringBuffer s= new StringBuffer(new String(p.sysex,patchNameStart,
             patchNameSize*2-1,"US-ASCII"));
           for (int i=1;i<s.length();i++) s.deleteCharAt(i);
         return s.toString();
         } catch (UnsupportedEncodingException ex) {return "-";}
   }

  public void setPatchName(Patch p, String name)
  {
    if (patchNameSize==0)
       {JOptionPane.showMessageDialog(null,
        "The Driver for this patch does not support Patch Name Editing.","Error", JOptionPane.ERROR_MESSAGE);
         return;
       }
    if (name.length()<patchNameSize) name=name+"            ";
    byte [] namebytes = new byte [64];
    try {
         namebytes=name.getBytes("US-ASCII");
         for (int i=0;i<patchNameSize;i++)
           p.sysex[patchNameStart+(i*2)]=namebytes[i];

        } catch (UnsupportedEncodingException ex) {return;}
    calculateChecksum(p);
  }
    
   public void storePatch (Patch p,int bankNum,int patchNum)
   {
     p.sysex[5]=(byte)((bankNum*100+patchNum)%128);
     p.sysex[6]=(byte)((bankNum*100+patchNum)/128);
     setBankNum(bankNum);
     setPatchNum(patchNum);
     sendPatchWorker (p);
   }
   public void choosePatch (Patch p)
   {
     
     Integer patchNum;
     String input=JOptionPane.showInputDialog(null,"Send to Which Patch Location (0-499)",
                  "Emu Proteus MPS",JOptionPane.QUESTION_MESSAGE);
     if (input==null) return;
     try {patchNum=new Integer(input);
     if (patchNum.intValue()<0 || patchNum.intValue()>499) {JOptionPane.showMessageDialog(null, "Invalid Patch Number Entered!","Error", JOptionPane.ERROR_MESSAGE); return;}
     storePatch(p,patchNum.intValue()/100,patchNum.intValue()%100);
         }catch (Exception e) {JOptionPane.showMessageDialog(null, "Invalid Patch Number Entered!","Error", JOptionPane.ERROR_MESSAGE);}
   }
   public void sendPatch (Patch p)
   {
     
     Integer patchNum=new Integer(100);
     p.sysex[5]=(byte)(patchNum.intValue()%128);
     p.sysex[6]=(byte)(patchNum.intValue()/128);
     setBankNum(patchNum.intValue()/100);
     setPatchNum(patchNum.intValue()%100);
     sendPatchWorker (p);
   }

  public void calculateChecksum(Patch p)
  {
    int i;
    int sum=0;
   
    for (i=7;i<=316;i++)
      sum+=p.sysex[i];
    p.sysex[checksumOffset]=(byte)(sum % 128);


  }
 public Patch createNewPatch()
 {
	 byte [] sysex = new byte[319];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x18;sysex[2]=(byte)0x08;sysex[3]=(byte)0x00;sysex[4]=(byte)0x01;
	 sysex[318]=(byte)0xF7;
         Patch p = new Patch(sysex, this);
	 setPatchName(p,"New Patch");
	 calculateChecksum(p);	 
	 return p;
 }
public JSLFrame editPatch(Patch p)
 {
     return new EmuProteusMPSSingleEditor(p);
 }
  public void requestPatchDump(int bankNum, int patchNum) {
   SysexRequestDump.send(
        getPort(), (byte)getChannel(),
        new NameValue("bankNum", (bankNum*100+patchNum)%128),
        new NameValue("patchNum", (bankNum*100+patchNum)/128)
      );
   
  }

}
