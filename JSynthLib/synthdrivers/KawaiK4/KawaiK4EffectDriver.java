package synthdrivers.KawaiK4;
import core.Driver;
import core.JSLFrame;
import core.NameValue;
import core.Patch;
import core.SysexHandler;

/**
 * Single Effect Patch Driver for Kawai K4.
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class KawaiK4EffectDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 35;

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 01 *patchNum* F7");

    public KawaiK4EffectDriver() {
	super("Effect", "Gerrit Gehnen");
        sysexID = "F040**2*0004";

	patchSize	= HSIZE + SSIZE + 1;
        patchNameStart	= 0;
        patchNameSize	= 0;
        deviceIDoffset	= 2;
        checksumStart	= HSIZE;
	checksumEnd	= HSIZE + SSIZE - 2;
	checksumOffset	= HSIZE + SSIZE - 1;
        bankNumbers = new String[] {
	    "0-Internal", "1-External"
	};
	// Is this correct? BulkConverter has 32 patches.
	/*
        patchNumbers = new String[31];
        for (int i = 0; i < 31; i++)
            patchNumbers[i] = String.valueOf(i + 1);
	*/
        patchNumbers = generateNumbers(1, 31, "00");
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        ((Patch)p).sysex[3] = (byte) 0x20;
        ((Patch)p).sysex[6] = (byte) ((bankNum << 1)  + 1);
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
            sum  += p.sysex[i];
        }
        sum += 0xA5;
        p.sysex[ofs] = (byte) (sum % 128);
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
        sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x23; sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x04; sysex[6] = (byte) 0x01;

        sysex[18] = 0x07;
        sysex[21] = 0x07;
        sysex[24] = 0x07;
        sysex[27] = 0x07;
        sysex[30] = 0x07;
        sysex[33] = 0x07;
        sysex[36] = 0x07;
        sysex[39] = 0x07;

        sysex[HSIZE + SSIZE] = (byte) 0xF7;
        Patch p = new Patch(sysex, this);
        //setPatchName(p,"New Effect");
        calculateChecksum(p);
        return p;
    }

    public JSLFrame editPatch(Patch p) {
        return new KawaiK4EffectEditor((Patch)p);
    }

    public String getPatchName(Patch ip) {
        String s  = "Effect Type "  + (((Patch)ip).sysex[HSIZE] + 1);
        return s;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new NameValue("bankNum", (bankNum << 1) + 1),
				    new NameValue("patchNum", patchNum)));
    }
}
