/*
 * @version $Id$
 */
package synthdrivers.YamahaTG33;
import core.*;
import java.io.*;
import javax.swing.*;
public class YamahaTG33BankDriver extends BankDriver
{
    
    public YamahaTG33BankDriver ()
    {
	super ("Bank","Brian Klock",64,4);
        sysexID="F043**7E****4C4D2020303031325643";
        deviceIDoffset=2;
        bankNumbers =new String[]
        {"0-Internal","1-Card1","2-Preset1","3-Unused",
         "4-Card2","5-Preset2"};
         patchNumbers=new String[]
         {"11","12","13","14","15","16","17","18",
          "21","22","23","24","25","26","27","28",
          "31","32","33","34","35","36","37","38",
          "41","42","43","44","45","46","47","48",
          "51","52","53","54","55","56","57","58",
          "61","62","63","64","65","66","67","68",
          "71","72","73","74","75","76","77","78",
          "81","82","83","84","85","86","87","88"};
          
          singleSize=605;
          singleSysexID="F043**7E****4C4D2020303031325645";
          
    }
    
    public int getPatchStart (int patchNum)
    {
        int start=(587*patchNum);
        start+=16;  //sysex header
        start+=3*((patchNum/4)); //checksum / block header
        return start;
    }
    public String getPatchName (Patch p,int patchNum)
    {
        int nameStart=getPatchStart (patchNum);
        nameStart+=12; //offset of name in patch data
        try
        {
            StringBuffer s= new StringBuffer (new String (p.sysex,nameStart,
            8,"US-ASCII"));
            return s.toString ();
        } catch (UnsupportedEncodingException ex)
        {return "-";}
        
    }
    
    public void setPatchName (Patch p,int patchNum, String name)
    {
        patchNameSize=8;
        patchNameStart=getPatchStart (patchNum)+12;
        
        if (name.length ()<patchNameSize) name=name+"            ";
        byte [] namebytes = new byte [64];
        try
        {
            namebytes=name.getBytes ("US-ASCII");
            for (int i=0;i<patchNameSize;i++)
                p.sysex[patchNameStart+i]=namebytes[i];
            
        } catch (UnsupportedEncodingException ex)
        {return;}
    }
    
    
    
    public void calculateChecksum (Patch p)
    {calculateChecksum (p,6,2363,2364);
     calculateChecksum (p,2367,4714,4715);
     calculateChecksum (p,2367+2351,4714+2351,4715+2351);
     calculateChecksum (p,2367+2351*2,4714+2351*2,4715+2351*2);
     calculateChecksum (p,2367+2351*3,4714+2351*3,4715+2351*3);
     calculateChecksum (p,2367+2351*4,4714+2351*4,4715+2351*4);
     calculateChecksum (p,2367+2351*5,4714+2351*5,4715+2351*5);
     calculateChecksum (p,2367+2351*6,4714+2351*6,4715+2351*6);
     calculateChecksum (p,2367+2351*7,4714+2351*7,4715+2351*7);
     calculateChecksum (p,2367+2351*8,4714+2351*8,4715+2351*8);
     calculateChecksum (p,2367+2351*9,4714+2351*9,4715+2351*9);
     calculateChecksum (p,2367+2351*10,4714+2351*10,4715+2351*10);
     calculateChecksum (p,2367+2351*11,4714+2351*11,4715+2351*11);
     calculateChecksum (p,2367+2351*12,4714+2351*12,4715+2351*12);
     calculateChecksum (p,2367+2351*13,4714+2351*13,4715+2351*13);
     calculateChecksum (p,2367+2351*14,4714+2351*14,4715+2351*14);
    }
    
    public void putPatch (Patch bank,Patch p,int patchNum)
    {
        if (!canHoldPatch (p))
        {JOptionPane.showMessageDialog (null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
        
        System.arraycopy (p.sysex,16,bank.sysex,getPatchStart (patchNum),587);
        calculateChecksum (bank);
    }
    public Patch getPatch (Patch bank, int patchNum)
    {
        try
        {
            byte [] sysex=new byte[605];
            sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
            sysex[03]=(byte)0x7E;sysex[04]=(byte)0x04;sysex[05]=(byte)0x55;
            sysex[06]=(byte)0x4C;sysex[07]=(byte)0x4D;sysex[8]=(byte)0x20;
            sysex[9]=(byte)0x20;sysex[10]=(byte)0x30;sysex[11]=(byte)0x30;
            sysex[12]=(byte)0x31;sysex[13]=(byte)0x32;sysex[14]=(byte)0x56;
            sysex[15]=(byte)0x45;  sysex[604]=(byte)0xF7;
            System.arraycopy (bank.sysex,getPatchStart (patchNum),sysex,16,587);
            Patch p = new Patch (sysex, getDevice());
            p.getDriver().calculateChecksum (p);
            return p;
        }catch (Exception e)
        {ErrorMsg.reportError ("Error","Error in TG33 Bank Driver",e);return null;}
    }
    
    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[37631];
        sysex[00]=(byte)0xF0;sysex[01]=(byte)0x43;sysex[02]=(byte)0x00;
        sysex[03]=(byte)0x7E;sysex[04]=(byte)0x12;sysex[05]=(byte)0x36;
        sysex[06]=(byte)0x4C;sysex[07]=(byte)0x4D;sysex[8]=(byte)0x20;
        sysex[9]=(byte)0x20;sysex[10]=(byte)0x30;sysex[11]=(byte)0x30;
        sysex[12]=(byte)0x31;sysex[13]=(byte)0x32;sysex[14]=(byte)0x56;
        sysex[15]=(byte)0x43;  sysex[37630]=(byte)0xF7;
        Patch p = new Patch (sysex, this);
        for (int i=4;i<64;i+=4)
        {sysex[getPatchStart (i)-2]=0x12;sysex[getPatchStart (i)-1]=0x2C;};
        
        for (int i=0;i<64;i++)
        {
            setPatchName (p,i,"NewPatch");
        }
        calculateChecksum (p);
        return p;
    }
    
}
