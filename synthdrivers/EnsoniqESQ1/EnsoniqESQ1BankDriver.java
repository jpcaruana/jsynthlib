/*
 * @version $Id$
 */
package synthdrivers.EnsoniqESQ1;
import javax.swing.JOptionPane;

import core.BankDriver;
import core.ErrorMsg;
import core.Patch;
public class EnsoniqESQ1BankDriver extends BankDriver
{

   public EnsoniqESQ1BankDriver()

   {
   super ("Bank","Brian Klock",40,4);
   sysexID="F00F02**02";
   deviceIDoffset=3;
   
   singleSysexID="F00F02**01";
   singleSize=0;
   bankNumbers =new String[] {"0-Internal"};
   patchNumbers=new String[] {"01","02","03","04","05","06","07","08",
                              "09","10","11","12","13","14","15","16",   
                              "17","18","19","20","21","22","23","24",
                              "25","26","27","28","29","30","31","32",   
                              "33","34","35","36","37","38","39","40"};

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
            b[0]=((byte)(((Patch)p).sysex[nameStart]+((Patch)p).sysex[nameStart+1]*16));
            b[1]=((byte)(((Patch)p).sysex[nameStart+2]+((Patch)p).sysex[nameStart+3]*16));
            b[2]=((byte)(((Patch)p).sysex[nameStart+4]+((Patch)p).sysex[nameStart+5]*16));
            b[3]=((byte)(((Patch)p).sysex[nameStart+6]+((Patch)p).sysex[nameStart+7]*16));
            b[4]=((byte)(((Patch)p).sysex[nameStart+8]+((Patch)p).sysex[nameStart+9]*16));
            b[5]=((byte)(((Patch)p).sysex[nameStart+10]+((Patch)p).sysex[nameStart+11]*16));
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
        ((Patch)p).sysex[nameStart]=((byte)(namebytes[0]%16));
        ((Patch)p).sysex[nameStart+1]=((byte)(namebytes[0]/16));
        ((Patch)p).sysex[nameStart+2]=((byte)(namebytes[1]%16));
        ((Patch)p).sysex[nameStart+3]=((byte)(namebytes[1]/16));
        ((Patch)p).sysex[nameStart+4]=((byte)(namebytes[2]%16));
        ((Patch)p).sysex[nameStart+5]=((byte)(namebytes[2]/16));
        ((Patch)p).sysex[nameStart+6]=((byte)(namebytes[3]%16));
        ((Patch)p).sysex[nameStart+7]=((byte)(namebytes[3]/16));
        ((Patch)p).sysex[nameStart+8]=((byte)(namebytes[4]%16));
        ((Patch)p).sysex[nameStart+9]=((byte)(namebytes[4]/16));
        ((Patch)p).sysex[nameStart+10]=((byte)(namebytes[5]%16));
        ((Patch)p).sysex[nameStart+11]=((byte)(namebytes[5]/16));
	}catch (Exception e) {}

  }
 

//  protected static void calculateChecksum(Patch p,int start,int end,int ofs)
//  {
//  }


  public void calculateChecksum (Patch p)
   {
   }                                     

  public void putPatch(Patch bank,Patch p,int patchNum)
   { 
   if (!canHoldPatch(p))
       {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
                        
   System.arraycopy(((Patch)p).sysex,5,((Patch)bank).sysex,getPatchStart(patchNum),204);
   calculateChecksum(bank);
   }
  public Patch getPatch(Patch bank, int patchNum)
   {
  try{
     byte [] sysex=new byte[210];
     sysex[00]=(byte)0xF0;sysex[01]=(byte)0x0F;sysex[02]=(byte)0x02;
     sysex[03]=(byte)0x00;sysex[04]=(byte)0x01;
     sysex[209]=(byte)0xF7;     
     System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,5,204);
     Patch p = new Patch(sysex, getDevice());
     p.calculateChecksum();   
     return p;
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in ESQ1 Bank Driver",e);return null;}
   }
public Patch createNewPatch()
 {
	 byte [] sysex = new byte[15123];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x21;sysex[4]=(byte)0x00;
	  sysex[5]=(byte)0x04; sysex[6]=(byte)0x0;sysex[15122]=(byte)0xF7;
	 Patch p = new Patch(sysex, this);
	 for (int i=0;i<64;i++)
           setPatchName(p,i,"NEWSND");
	 calculateChecksum(p);	 
	 return p;
 }


}
