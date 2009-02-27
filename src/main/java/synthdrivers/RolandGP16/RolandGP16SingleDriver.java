package synthdrivers.RolandGP16;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;
/**
 * Single Voice Patch Driver for Roland GP16.
 *
 * @version $Id$
 */
public class RolandGP16SingleDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 5;
    /** Single Patch size */
    private static final int SSIZE = 121;
    /** The sysex message sent when requesting a patch. */
    private static final SysexHandler SYS_REQ = 
    	new SysexHandler("F0 41 @@ 2A 11 09 *patchnumber* 00 00 00 75 *checksum* F7");
    /** Time to sleep when doing sysex data transfers. */
    private static final int sleepTime = 100;

/** The constructor. */    
    public RolandGP16SingleDriver() {
	super("Single", "Mikael Kurula");
	sysexID = "F041**2A";

	patchSize	= HSIZE + SSIZE + 1;
	patchNameStart	= 108;
	patchNameSize	= 16;
	deviceIDoffset	= 2;
	checksumStart	= HSIZE;
	checksumEnd	= HSIZE + SSIZE - 2;
	checksumOffset	= HSIZE + SSIZE - 1;
	
	bankNumbers = new String[8 * 2];
	System.arraycopy(DriverUtil.generateNumbers(1, 8, "Group A - Bank ##"), 0, bankNumbers,  0, 8);
	System.arraycopy(DriverUtil.generateNumbers(1, 8, "Group B - Bank ##"), 0, bankNumbers,  8, 8);
 	patchNumbers = new String[8 * 1];
	System.arraycopy(DriverUtil.generateNumbers(1, 8, "Patch ##"), 0, patchNumbers,  0, 8);   
    }

/** Request single patch dump. GP-16 requires correct checksum. */
    public void requestPatchDump(int bankNum, int patchNum){
	try {Thread.sleep(sleepTime); } catch (Exception e){}
  	SysexHandler.NameValue nVs[] = new SysexHandler.NameValue[2];
	nVs[0] = new SysexHandler.NameValue("patchnumber", bankNum*8+patchNum);
	nVs[1] = new SysexHandler.NameValue("checksum", 0);
	Patch p = new Patch(SYS_REQ.toByteArray(getChannel(),nVs));
	calculateChecksum(p,5,10,11);	// the gp-16 requires correct checksum when requesting a patch
	send(p.sysex);
	try {Thread.sleep(sleepTime); } catch (Exception e){}
    }
    
/** Store patch in a specified location, the GP-16 way.*/
    public void storePatch (Patch p, int bankNum,int patchNum){   
   	((Patch)p).sysex[5]=(byte)0x09;
   	((Patch)p).sysex[6]=(byte)(bankNum*8+patchNum);
   	((Patch)p).sysex[7]=(byte)0x00;
   	sendPatchWorker(p);
   	try {Thread.sleep(sleepTime); } catch (Exception e){}   
   }

/** Send patch to the temporary edit memory of the GP-16. */
    public void sendPatch (Patch p){ 
   	((Patch)p).sysex[5]=(byte)0x08;
   	((Patch)p).sysex[6]=(byte)0x00;
   	((Patch)p).sysex[7]=(byte)0x00;
   	try {Thread.sleep(sleepTime); } catch (Exception e){}
   
   	sendPatchWorker(p);
   }
  
/** Create an empty patch in acceptable GP-16 format. */
    public Patch createNewPatch (){
	 byte [] sysex = new byte[HSIZE + SSIZE + 1];
	 sysex[0]=(byte)0xF0; sysex[1]=(byte)0x41; sysex[3]=(byte)0x2A; sysex[4]=(byte)0x12;
	 sysex[9]=(byte)0x01; sysex[10]=(byte)0x02; sysex[11]=(byte)0x03; sysex[12]=(byte)0x04; 
	 sysex[13]=(byte)0x05; sysex[14]=(byte)0x06; sysex[15]=(byte)0x07; sysex[16]=(byte)0x08;
	 sysex[17]=(byte)0x09; sysex[18]=(byte)0x0A; sysex[19]=(byte)0x0B; sysex[HSIZE + SSIZE]=(byte)0xF7;
         Patch p = new Patch(sysex,this);
	 setPatchName(p,"New Patch");
	 calculateChecksum(p);	 
	 return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new RolandGP16SingleEditor((Patch)p);
    }

    /** for Bank Driver, etc. */
    void calcChecksum(Patch p) {
        calculateChecksum(p);
    }
}
