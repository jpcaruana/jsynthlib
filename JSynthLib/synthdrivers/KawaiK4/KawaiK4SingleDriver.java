package synthdrivers.KawaiK4;
import core.Driver;
import core.DriverUtil;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * Single Voice Patch Driver for Kawai K4.
 *
 * @version $Id$
 */
public class KawaiK4SingleDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 131;

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 *bankNum* *patchNum* F7");

    public KawaiK4SingleDriver() {
	super("Single", "Brian Klock");
	sysexID = "F040**2*0004";

	patchSize	= HSIZE + SSIZE + 1;
	patchNameStart	= HSIZE;
	patchNameSize	= 10;
	deviceIDoffset	= 2;
	checksumStart	= HSIZE;
	checksumEnd	= HSIZE + SSIZE - 2;
	checksumOffset	= HSIZE + SSIZE - 1;
	bankNumbers	= new String[] {
	    "0-Internal", "1-External"
	};
	/*
	patchNumbers = new String[] {
	    "A-1", "A-2", "A-3", "A-4", "A-5", "A-6", "A-7", "A-8",
	    "A-9", "A-10", "A-11", "A-12", "A-13", "A-14", "A-15", "A-16",
	    "B-1", "B-2", "B-3", "B-4", "B-5", "B-6", "B-7", "B-8",
	    "B-9", "B-10", "B-11", "B-12", "B-13", "B-14", "B-15", "B-16",
	    "C-1", "C-2", "C-3", "C-4", "C-5", "C-6", "C-7", "C-8",
	    "C-9", "C-10", "C-11", "C-12", "C-13", "C-14", "C-15", "C-16",
	    "D-1", "D-2", "D-3", "D-4", "D-5", "D-6", "D-7", "D-8",
	    "D-9", "D-10", "D-11", "D-12", "D-13", "D-14", "D-15", "D-16"
	};
	*/
	patchNumbers = new String[16 * 4];
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "A-##"), 0, patchNumbers,  0, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "B-##"), 0, patchNumbers, 16, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "C-##"), 0, patchNumbers, 32, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "D-##"), 0, patchNumbers, 48, 16);
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
	setBankNum(bankNum);
	setPatchNum(patchNum);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	((Patch)p).sysex[3] = (byte) 0x20;
	((Patch)p).sysex[6] = (byte) (bankNum << 1);
	((Patch)p).sysex[7] = (byte) (patchNum);
	sendPatchWorker(p);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	setPatchNum(patchNum);
    }

    public void sendPatch(Patch p) {
	((Patch)p).sysex[3] = (byte) 0x23;
	((Patch)p).sysex[7] = (byte) 0x00;
	sendPatchWorker(p);
    }

    protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
    	int sum = 0;
	for (int i = start; i <= end; i++) {
	    sum += p.sysex[i];
	}
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x23; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x0; sysex[HSIZE + SSIZE] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new KawaiK4SingleEditor((Patch)p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new SysexHandler.NameValue("bankNum", bankNum << 1),
				    new SysexHandler.NameValue("patchNum", patchNum)));
    }
}
