package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.io.*;

public class KawaiK4EffectDriver extends Driver
{    
    public KawaiK4EffectDriver ()
    {
        manufacturer="Kawai";
        model="K4/K4r";
        patchType="Effect";
        id="K4";
        sysexID="F040**2*0004";
             authors="Gerrit Gehnen";
  
        inquiryID="F07E**06024000000400000000000f7";
	sysexRequestDump=new SysexHandler("F0 40 @@ 00 00 04 01 *patchNum* F7");
        patchSize=44;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=2;
        checksumStart=8;
        checksumEnd=41;
        checksumOffset=42;
        bankNumbers =new String[]
        {"0-Internal"};
        patchNumbers= new String [31];
        for (int i=0;i<31;i++)
            patchNumbers[i]=String.valueOf (i+1);
    }
    
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        setBankNum (bankNum);
        setPatchNum (patchNum);
        try
        {
            Thread.sleep (100);
        }
        catch (Exception e)
        {
        }
        p.sysex[3]=(byte)0x20;
        p.sysex[7]=(byte)(patchNum);
        sendPatchWorker (p);
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
        setPatchNum (patchNum);
    }
    public void sendPatch (Patch p)
    {
        p.sysex[3]=(byte)0x23;
        p.sysex[7]=(byte)0x00;
        sendPatchWorker (p);
    }
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        int i;
        int sum=0;
        
        for (i=start;i<=end;i++)
        {
            sum+=p.sysex[i];
        }
        sum+=0xA5;
        p.sysex[ofs]=(byte)(sum % 128);
    }

    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[44];
        sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x23;sysex[4]=(byte)0x00;
        sysex[5]=(byte)0x04; sysex[6]=(byte)0x01;
        
        sysex[18]=0x07;
        sysex[21]=0x07;
        sysex[24]=0x07;
        sysex[27]=0x07;
        sysex[30]=0x07;
        sysex[33]=0x07;
        sysex[36]=0x07;
        sysex[39]=0x07;
        
        sysex[43]=(byte)0xF7;
        Patch p = new Patch (sysex);
        p.ChooseDriver ();
        //setPatchName(p,"New Effect");
        calculateChecksum (p);
        return p;
    }
    public JInternalFrame editPatch (Patch p)
    {
        return new KawaiK4EffectEditor (p);
    }
    
    public String getPatchName (Patch p)
    {
        String s="Effect Type "+(p.sysex[8]+1);
        return s;
    }
}

