package synthdrivers.KorgWavestation;
import core.*;

import java.io.*;
import javax.swing.*;
/** Driver for Korg Wavestation Banks of Performances
 *
 * Be carefull: Untested, because I only have access to
 * a file containing some WS patches....
 *
 * @version $Id$
 * @author Gerrit Gehnen
 */
public class KorgWavestationBankPerformanceDriver extends BankDriver {
    
    public KorgWavestationBankPerformanceDriver() {
	super ("Performance Bank","Gerrit Gehnen",50,1);
        sysexID="F042**284D";
        trimSize=50*362+8;
        sysexRequestDump=new SysexHandler("F0 42 @@ 28 1D *bankNum* F7");
        
        deviceIDoffset=0; // need own handling of device ID
        
        bankNumbers =new String[] {"RAM1","RAM2","ROM1","CARD","RAM3"};
        patchNumbers=new String[] {"01-","02-","03-","04-","05-","06-","07-",
        "08-","09-","10-","11-","12-","13-","14-","15-",
        "16-","17-","18-","19-","20-","21-","22-","23-",
        "24-","25-","26-","27-","28-","29-","30-","31-",
        "32-","33-","34-","35-","36-","37-","38-","39-",
        "40-","41-","42-","43-","44-","45-","46-","47-",
        "48-","49-","50-"};
        
        singleSysexID="F040**2*0004";
        singleSize=362;
    }
    
    public int getPatchStart(int patchNum) {
        int start=(singleSize*patchNum);
        start+=6;  //sysex header
        
        return start;
    }
    
    public String getPatchName(Patch p,int patchNum) {
        int nameStart=getPatchStart(patchNum);
        
        nameStart+=0; //offset of name in patch data
        try {
            byte[] byteBuffer=new byte[16];
            
            for (int i=0;i<16;i++) {
                byteBuffer[i]=(byte)(((Patch)p).sysex[nameStart+i*2]+((0x10)*((Patch)p).sysex[nameStart+i*2+1]));
                if (byteBuffer[i]==0) byteBuffer[i]=0x20;
            }
            
            StringBuffer s= new StringBuffer(new String(byteBuffer,"US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {return "-";}
        
    }
    
    public void setPatchName(Patch p,int patchNum, String name) {
        patchNameSize=16;
        patchNameStart=getPatchStart(patchNum);
        
        if (name.length()<patchNameSize) name=name+"                     ";
        byte [] namebytes = new byte [64];
        try {
            namebytes=name.getBytes("US-ASCII");
            for (int i=0;i<patchNameSize;i++) {
                ((Patch)p).sysex[patchNameStart+i*2]=(byte)(namebytes[i]&0x0f);
                ((Patch)p).sysex[patchNameStart+i*2+1]=(byte)(namebytes[i]/0x10);
            }
        } catch (UnsupportedEncodingException ex) {return;}
    }
    
    public void storePatch(Patch p, int bankNum,int patchNum) {
        ((Patch)p).sysex[2]=(byte)(0x30+getChannel()-1);
        ((Patch)p).sysex[5]=(byte)bankNum;
        sendPatchWorker(p);
    }
    
    protected static void calculateChecksum(Patch p,int start,int end,int ofs) {
        int i;
        int sum=0;
        
        System.out.println("Checksum was" + p.sysex[ofs]);
        for (i=start;i<=end;i++) {
            sum+=p.sysex[i];
        }
        p.sysex[ofs]=(byte)(sum % 128);
        System.out.println("Checksum new is" + p.sysex[ofs]);
    }
    
    
    public void calculateChecksum(Patch p) {
        calculateChecksum(p,6,6+(362*50)-1,6+(362*50));
    }
    
    public void putPatch(Patch bank,Patch p,int patchNum) {
        if (!canHoldPatch(p))
        {JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); return;}
        
        System.arraycopy(((Patch)p).sysex,8,((Patch)bank).sysex,getPatchStart(patchNum),131);
        calculateChecksum(bank);
    }
    
    public Patch getPatch(Patch bank, int patchNum) {
        try{
            byte [] sysex=new byte[362+9];
            sysex[00]=(byte)0xF0;sysex[01]=(byte)0x42;
            sysex[2]=(byte)(0x30+getChannel()-1);
            sysex[03]=(byte)0x28;sysex[04]=(byte)0x40;sysex[05]=(byte)0x00/*bankNum*/;
            sysex[06]=(byte)patchNum;
            
            /*sysex[852+7]=checksum;*/
            sysex[362+8]=(byte)0xF7;
            System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,7,362);
            Patch p = new Patch(sysex, getDevice());
            p.getDriver().calculateChecksum(p);
            return p;
        }catch (Exception e) {ErrorMsg.reportError("Error","Error in Wavestation Bank Driver",e);return null;}
    }
    
    public Patch createNewPatch() {
        byte [] sysex = new byte[50*362+8];
        sysex[0]=(byte)0xF0; sysex[1]=(byte)0x42;
        sysex[2]=(byte)(0x30+getChannel()-1);
        sysex[3]=(byte)0x28;sysex[4]=(byte)0x4D;
        sysex[5]=(byte)0x00/*bankNum*/;
        
        /*sysex[50*362+6]=checksum;*/
        sysex[50*362+7]=(byte)0xF7;
        Patch p = new Patch(sysex, this);
        for (int i=0;i<35;i++)
            setPatchName(p,i,"New Patch");
        calculateChecksum(p);
        return p;
    }
}
