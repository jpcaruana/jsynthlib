package synthdrivers.KorgWavestation;
import core.*;
import javax.swing.*;
import java.io.*;

/** Driver for Korg Wavestation Micro Tuning Scales
 *
 * Be carefull: Untested, because I only have access to
 * a file containing some WS patches....
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgWavestationMicroTuneScaleDriver extends Driver {
    
    public KorgWavestationMicroTuneScaleDriver() {
        authors="Gerrit Gehnen";
        manufacturer="Korg";
        model="Wavestation";
        patchType="Micro Tune Scale";
        id="Wavestation";
        sysexID="F0423*285A";
        sysexRequestDump=new SysexHandler("F0 42 @@ 28 08 F7");
        
        trimSize=297;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=0;
        checksumStart=5;
        checksumEnd=294;
        checksumOffset=295;
    }
    
    public void storePatch(Patch p, int bankNum,int patchNum) {
        
        try
        {Thread.sleep(100); } catch (Exception e)
        {}
        
        p.sysex[2]=(byte)(0x30 + channel - 1);
        try {
            PatchEdit.MidiOut.writeLongMessage(port,p.sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus(e);}
        
    }
    
    public void sendPatch(Patch p) {
        p.sysex[2]=(byte)(0x30 + channel - 1); // the only thing to do is to set the byte to 3n (n = channel)
        
        try {
            PatchEdit.MidiOut.writeLongMessage(port,p.sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus(e);}
    }
    
    public Patch createNewPatch() {
        byte [] sysex=new byte[297];
        sysex[00]=(byte)0xF0;sysex[01]=(byte)0x42;
        sysex[2]=(byte)(0x30+channel-1);
        sysex[03]=(byte)0x28;sysex[04]=(byte)0x5A;
        
        /*sysex[295]=checksum;*/
        sysex[296]=(byte)0xF7;
        
        Patch p = new Patch(sysex);
        p.ChooseDriver();
        setPatchName(p,"New Patch");
        calculateChecksum(p);
        return p;
    }
    
    public void calculateChecksum(Patch p,int start,int end,int ofs) {
        int i;
        int sum=0;
        
        //System.out.println("Checksum was" + p.sysex[ofs]);
        for (i=start;i<=end;i++) {
            sum+=p.sysex[i];
        }
        p.sysex[ofs]=(byte)(sum % 128);
        //System.out.println("Checksum new is" + p.sysex[ofs]);
        
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        byte[] sysex = sysexRequestDump.toByteArray((byte)channel, patchNum+0x30);
        
        SysexHandler.send(port, sysex);
    }
}