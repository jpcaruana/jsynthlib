/** Driver for Kawai K4 effect banks,
 * nothing special, only the required adaptions
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

package synthdrivers.KawaiK4;
import core.*;
import java.io.*;
import javax.swing.*;

public class KawaiK4EffectBankDriver extends BankDriver
{
    
    public KawaiK4EffectBankDriver ()
    {
	super ("EffectBank","Gerrit Gehnen",32,2);
        sysexID="F040**2100040100";
   	sysexRequestDump=new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 00 F7");

        deviceIDoffset=2;
        bankNumbers =new String[]
        {"0-Internal","1-External"};
        patchNumbers=new String[]
        {"1","2","3","4","5","6","7","8",
         "9","10","11","12","13","14","15","16",
         "17","18","19","20","21","22","23","24",
         "25","26","27","28","29","30","31","32"};
                 
         singleSysexID="F040**2*0004";
         singleSize=35+9;
         patchSize=32*35+9; // To distinguish from the Effect bank, which has the same sysexID
        }
    
        /**
         * @param patchNum
         * @return  */        
    public int getPatchStart (int patchNum)
    {
        int start=(35*patchNum);
        start+=8;  //sysex header
        return start;
    }
    /**
     * @param p
     * @param patchNum
     * @return  */    
    public String getPatchName (Patch p,int patchNum)
    {
        int nameStart=getPatchStart (patchNum);
        nameStart+=0; //offset of name in patch data
           //System.out.println("Patch Num "+patchNum+ "Name Start:"+nameStart);
         String s="Effect Type "+(p.sysex[nameStart]+1);
             return s;    
    }

    /**
     * @param p
     * @param start
     * @param end
     * @param ofs  */    
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        int i;
        int sum=0;

        for (i=start;i<=end;i++)
            sum+=p.sysex[i];
        sum+=0xA5; 
        p.sysex[ofs]=(byte)(sum % 128); 
       
    }
  
    
    /**
     * @param p  */    
    public void calculateChecksum (Patch p)
    {
        for (int i=0;i<32;i++)
            calculateChecksum (p,8+(i*35),8+(i*35)+33,8+(i*35)+34);
    }
    
    /**
     * @param bank
     * @param p
     * @param patchNum  */    
    public void putPatch (Patch bank,Patch p,int patchNum)
    {
        if (!canHoldPatch (p))
        {JOptionPane.showMessageDialog (null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
        
        System.arraycopy (p.sysex,8,bank.sysex,getPatchStart (patchNum),35);
        calculateChecksum (bank);
    }
    
    /** Extract a K4-effect patch from a K4-effect bank
     * @param bank The patch containing an entire bank
     * @param patchNum The index of the patch to extract
     * @return A single effect patch
     */    
    public Patch getPatch (Patch bank, int patchNum)
    {
        try
        {
            byte [] sysex=new byte[35+9];
            sysex[00]=(byte)0xF0;sysex[01]=(byte)0x40;sysex[02]=(byte)0x00;
            sysex[03]=(byte)0x20;sysex[04]=(byte)0x00;sysex[05]=(byte)0x04;
            sysex[06]=(byte)0x01;sysex[07]=(byte)(patchNum);
            sysex[35+8]=(byte)0xF7;
            System.arraycopy (bank.sysex,getPatchStart (patchNum),sysex,8,35);
            Patch p = new Patch (sysex, getDevice());
            p.getDriver().calculateChecksum (p);
            return p;
        }catch (Exception e)
        {ErrorMsg.reportError ("Error","Error in K4 EffectBank Driver",e);return null;}
    }
 
    /** Creates a new Effect Bank patch, with a predefined setting of the pan
     * to the center of all patches
     * @return The new created patch
     */    
    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[35*32+9];
        sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x21;sysex[4]=(byte)0x00;
        sysex[5]=(byte)0x04; sysex[6]=(byte)0x01;sysex[7]=0x00;
        
        for (int i=0;i<32;i++)
        {
        sysex[i*35+18]=0x07;
        sysex[i*35+21]=0x07;
        sysex[i*35+24]=0x07;
        sysex[i*35+27]=0x07;
        sysex[i*35+30]=0x07;
        sysex[i*35+33]=0x07;
        sysex[i*35+36]=0x07;
        sysex[i*35+39]=0x07;
        }
        
        sysex[35*32+8]=(byte)0xF7;
        Patch p = new Patch (sysex, this);
        
        calculateChecksum (p);
        return p;
    }

  public void requestPatchDump(int bankNum, int patchNum) {
        NameValue nv[]=new NameValue[1];
        nv[0]=new NameValue("bankNum",(bankNum<<1)+1);
                
        byte[] sysex = sysexRequestDump.toByteArray((byte)getChannel(),nv);
        
        SysexHandler.send(getPort(), sysex);
    }
  
       public void storePatch (Patch p, int bankNum,int patchNum)
    {
        try
        {Thread.sleep (100);}catch (Exception e)
        { }
        p.sysex[3]=(byte)0x21;
        p.sysex[6]=(byte)((bankNum<<1)+1);
        p.sysex[7]=(byte)0x0;
        sendPatchWorker (p);
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
    }

}
