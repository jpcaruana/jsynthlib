package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.io.*;

public class KawaiK4DrumsetDriver extends Driver
{
    public KawaiK4DrumsetDriver ()
    {
        manufacturer="Kawai";
        model="K4/K4r";
        patchType="Drumset";
        id="K4";
        sysexID="F040**2*0004**20";
        sysexRequestDump=new SysexHandler("F0 40 @@ 00 00 04 01 20 F7");
        patchSize=682+9;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=2;
        
        bankNumbers =new String[]
        {"0-Internal"};
        patchNumbers= new String [1];
        patchNumbers[0]="Drumset";
        
    }
    
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        try
        {Thread.sleep (100);}catch (Exception e)
        { }
        p.sysex[3]=(byte)0x20;
        p.sysex[7]=(byte)0x20;
        sendPatchWorker (p);
        try
        {Thread.sleep (100); } catch (Exception e)
        {}

    }

    public void sendPatch (Patch p)
    {
        p.sysex[3]=(byte)0x23;
        p.sysex[7]=(byte)0x20;
        sendPatchWorker (p);
    }
    
    public void calculateChecksum (Patch p,int start,int end,int ofs)
    {
        int i=0,j=0;
        int sum=0;

        for (i=8;i<681+8;i+=11) // a litte strange this, but there is a checksum for each key!
        {
            for(j=i;j<i+10;j++)
            {
            sum+=p.sysex[j];
            }
        sum+=0xA5;
        p.sysex[i+10]=(byte)(sum % 128);
        sum=0;
        }
    }
    
    public Patch createNewPatch ()
    {
        byte [] sysex = new byte[682+9];
        sysex[0]=(byte)0xF0; sysex[1]=(byte)0x40;sysex[2]=(byte)0x00;sysex[3]=(byte)0x23;sysex[4]=(byte)0x00;
        sysex[5]=(byte)0x04; sysex[6]=(byte)0x01;sysex[7]=0x20;
         
        for (int i=0;i<61;i++)
        {
            sysex[8+11+6+i*11]=50;
            sysex[8+11+7+i*11]=50;
        }
        
         sysex[682+8]=(byte)0xF7;
         Patch p = new Patch (sysex);
         p.ChooseDriver ();

         calculateChecksum (p);
         return p;
        }
    
 public JInternalFrame editPatch (Patch p)
    {
        return new KawaiK4DrumsetEditor (p);
    }
    

 public String getPatchName (Patch p)
    {
    return "Drumset";
    }

}
    
