package synthdrivers.EnsoniqESQ1;
import core.*;
import java.io.*;
import javax.swing.*;
public class EnsoniqESQ1BankDriver extends BankDriver
{

   public EnsoniqESQ1BankDriver()

   {

   manufacturer="Ensoniq";
   model="ESQ-1";
   patchType="Bank";
   id="ESQ1";
   sysexID="F00F02**02";
//   inquiryID="F07E**06020F0200*************F7";
   deviceIDoffset=3;
   
   singleSysexID="F00F02**01";
   singleSize=0;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"01","02","03","04","05","06","07","08",
                              "09","10","11","12","13","14","15","16",   
                              "17","18","19","20","21","22","23","24",
                              "25","26","27","28","29","30","31","32",   
                              "33","34","35","36","37","38","39","40"};

   numPatches=40;
   numColumns=4;
  }

  public int getPatchStart(int patchNum)
   {
     int start=(204*patchNum);
     start+=5;  //sysex header
   return start;
   }
  public String getPatchName(Patch p,int patchNum) {
     int nameStart=getPatchStart(patchNum);
     nameStart+=0; //offset of name in patch data

          try {
            byte []b = new byte[6];
            b[0]=((byte)(p.sysex[nameStart]+p.sysex[nameStart+1]*16));
            b[1]=((byte)(p.sysex[nameStart+2]+p.sysex[nameStart+3]*16));
            b[2]=((byte)(p.sysex[nameStart+4]+p.sysex[nameStart+5]*16));
            b[3]=((byte)(p.sysex[nameStart+6]+p.sysex[nameStart+7]*16));
            b[4]=((byte)(p.sysex[nameStart+8]+p.sysex[nameStart+9]*16));
            b[5]=((byte)(p.sysex[nameStart+10]+p.sysex[nameStart+11]*16));
            StringBuffer s= new StringBuffer(new String(b,0,6,"US-ASCII"));
           return s.toString();
         } catch (Exception ex) {return "-";}
     
  }

  public void setPatchName(Patch p,int patchNum, String name)
  {
	byte [] namebytes = new byte[32];
        int nameStart=getPatchStart(patchNum);
	try{
        if (name.length()<6) name=name+"        ";
        namebytes=name.getBytes("US-ASCII");
        p.sysex[nameStart]=((byte)(namebytes[0]%16));
        p.sysex[nameStart+1]=((byte)(namebytes[0]/16));
        p.sysex[nameStart+2]=((byte)(namebytes[1]%16));
        p.sysex[nameStart+3]=((byte)(namebytes[1]/16));
        p.sysex[nameStart+4]=((byte)(namebytes[2]%16));
        p.sysex[nameStart+5]=((byte)(namebytes[2]/16));
        p.sysex[nameStart+6]=((byte)(namebytes[3]%16));
        p.sysex[nameStart+7]=((byte)(namebytes[3]/16));
        p.sysex[nameStart+8]=((byte)(namebytes[4]%16));
        p.sysex[nameStart+9]=((byte)(namebytes[4]/16));
        p.sysex[nameStart+10]=((byte)(namebytes[5]%16));
        p.sysex[nameStart+11]=((byte)(namebytes[5]/16));
	}catch (Exception e) {}

  }
 

  public void calculateChecksum(Patch p,int start,int end,int ofs)
  {


  }


  public void calculateChecksum (Patch p)
   {
   }                                     

  public void putPatch(Patch bank,Patch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
                        
   System.arraycopy(p.sysex,5,bank.sysex,getPatchStart(patchNum),204);
   calculateChecksum(bank);
   }
  public Patch getPatch(Patch bank, int patchNum)
   {
  try{
     byte [] sysex=new byte[210];
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x0F;sysex[02]=(byte)0x02;
     sysex[03]=(byte)0x00;sysex[04]=(byte)0x01;
     sysex[209]=(byte)0xF7;     
     System.arraycopy(bank.sysex,getPatchStart(patchNum),sysex,5,204);
     Patch p = new Patch(sysex);
     p.ChooseDriver();
     PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);   
    return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in ESQ1 Bank Driver",e);return null;}
   }
public Patch createNewPatch()
 {
	 byte [] sysex = new byte[15123];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x21;sysex[4]=(byte)0x00;
	  sysex[5]=(byte)0x04; sysex[6]=(byte)0x0;sysex[15122]=(byte)0xF7;
         Patch p = new Patch(sysex);
	 p.ChooseDriver();
	 for (int i=0;i<64;i++)
           setPatchName(p,i,"NEWSND");
	 calculateChecksum(p);	 
	 return p;
 }


}
