package org.jsynthlib.drivers.korg.wavestation;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/** Driver for Korg Wavestation MultiMode Setup.
 *
 * Be carefull: This driver is untested, because I
 * only have acces to a file containing WS patches....
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgWavestationMultiModeSetupDriver extends Driver {

    public KorgWavestationMultiModeSetupDriver() {
	super ("Multi Mode Setup","Gerrit Gehnen");
        sysexID="F0423*2855";
        sysexRequestDump=new SysexHandler("F0 42 @@ 28 06 F7");

        trimSize=2761;
        patchNameStart=0;
        patchNameSize=0;
        deviceIDoffset=0;
        checksumStart=5;
        checksumEnd=2758;
        checksumOffset=2759;

    }

    /**
     */    
    public void storePatch(Patch p, int bankNum,int patchNum) {
        //        int patchValue = patchNum;
        //int bankValue  = 0;
               calculateChecksum(p);

        try
        {Thread.sleep(100); } catch (Exception e)
        {}
        
        ((Patch)p).sysex[2]=(byte)(0x30 + getChannel() - 1);
        try {
            send(((Patch)p).sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus(e);}
    /*
        try
        {Thread.sleep (100); } catch (Exception e)
        {}
     */
    }
    
    public void sendPatch(Patch p) {
        ((Patch)p).sysex[2]=(byte)(0x30 + getChannel() - 1); // the only thing to do is to set the byte to 3n (n = channel)
        
        try {
            send(((Patch)p).sysex);
        }catch (Exception e)
        {ErrorMsg.reportStatus(e);}
    }
    
    public Patch createNewPatch() {
        byte [] sysex=new byte[2761];
        sysex[00]=(byte)0xF0;sysex[01]=(byte)0x42;
        sysex[2]=(byte)(0x30+getChannel()-1);
        sysex[03]=(byte)0x28;sysex[04]=(byte)0x55;

        /*sysex[2759]=checksum;*/
        sysex[2760]=(byte)0xF7;
        
        Patch p = new Patch(sysex, this);
        setPatchName(p,"New Patch");
        calculateChecksum(p);
        return p;
    }
    
    protected void calculateChecksum(Patch p,int start,int end,int ofs) {
        int i;
        int sum=0;

//        System.out.println("Checksum was" + p.sysex[ofs]);
        for (i=start;i<=end;i++) {
            sum+=p.sysex[i];
        }
        p.sysex[ofs]=(byte)(sum % 128);
//        System.out.println("Checksum new is" + p.sysex[ofs]);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(getChannel(),0));
    }
}

