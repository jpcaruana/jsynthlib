package synthdrivers.KawaiK4;

import javax.swing.JOptionPane;
import core.BankDriver;
import core.DriverUtil;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;

/**
 * Bank driver for KAWAI K4/K4r effect patch.
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KawaiK4EffectBankDriver extends BankDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 35;
    /** the number of single patches in a bank patch. */
    private static final int NS = 32;

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 00 F7");

    public KawaiK4EffectBankDriver () {
	super ("EffectBank", "Gerrit Gehnen", NS, 2);

        sysexID = "F040**2100040100";
        deviceIDoffset = 2;
        bankNumbers = new String[] {
	    "0-Internal", "1-External"
	};
        patchNumbers = DriverUtil.generateNumbers(1, 32, "00");

	singleSysexID = "F040**2*0004";
	singleSize = HSIZE + SSIZE + 1;
	// To distinguish from the Effect bank, which has the same sysexID
	patchSize = HSIZE + SSIZE * NS + 1;
    }

    public int getPatchStart(int patchNum) {
	return HSIZE + (SSIZE * patchNum);
    }

    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 0; //offset of name in patch data
	//System.out.println("Patch Num "+patchNum+ "Name Start:"+nameStart);
	String s = "Effect Type " + (((Patch)p).sysex[nameStart] + 1);
	return s;
    }
    protected void setPatchName(Patch bank, int patchNum, String name) {
        // do nothing
    }

    protected static void calculateChecksum(Patch p, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++)
            sum += p.sysex[i];
        sum += 0xA5;
        p.sysex[ofs] = (byte) (sum % 128);
    }

    public void calculateChecksum(Patch p) {
        for (int i = 0; i < NS; i++)
            calculateChecksum(p, HSIZE + (i * SSIZE),
			      HSIZE + (i * SSIZE) + SSIZE - 2,
			      HSIZE + (i * SSIZE) + SSIZE - 1);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
	    JOptionPane.showMessageDialog
		(null,
		 "This type of patch does not fit in to this type of bank.",
		 "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

        System.arraycopy(((Patch)p).sysex, HSIZE, ((Patch)bank).sysex, getPatchStart(patchNum), SSIZE);
        calculateChecksum(bank);
    }

    /** Extract a K4-effect patch from a K4-effect bank
     * @param bank The patch containing an entire bank
     * @param patchNum The index of the patch to extract
     * @return A single effect patch
     */
    public Patch getPatch(Patch bank, int patchNum) {
	byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x20; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x01; sysex[7] = (byte) (patchNum);
	sysex[HSIZE + SSIZE] = (byte) 0xF7;
	System.arraycopy(((Patch)bank).sysex, getPatchStart(patchNum), sysex, HSIZE, SSIZE);
        try {
            Patch p = new Patch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
	    ErrorMsg.reportError("Error", "Error in K4 EffectBank Driver", e);
	    return null;
	}
    }

    /** Creates a new Effect Bank patch, with a predefined setting of the pan
     * to the center of all patches
     * @return The new created patch
     */
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE * NS + 1];
        sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x21; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x01; sysex[7] = 0x00;

        for (int i = 0; i < NS; i++) {
	    sysex[i * SSIZE + 18] = 0x07;
	    sysex[i * SSIZE + 21] = 0x07;
	    sysex[i * SSIZE + 24] = 0x07;
	    sysex[i * SSIZE + 27] = 0x07;
	    sysex[i * SSIZE + 30] = 0x07;
	    sysex[i * SSIZE + 33] = 0x07;
	    sysex[i * SSIZE + 36] = 0x07;
	    sysex[i * SSIZE + 39] = 0x07;
        }

        sysex[HSIZE + SSIZE * NS] = (byte) 0xF7;
        Patch p = new Patch(sysex, this);
        calculateChecksum(p);
        return p;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new SysexHandler.NameValue("bankNum", (bankNum << 1) + 1)));
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
        ((Patch)p).sysex[3] = (byte) 0x21;
        ((Patch)p).sysex[6] = (byte) ((bankNum << 1) + 1);
        ((Patch)p).sysex[7] = (byte) 0x0;
        sendPatchWorker(p);
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
    }
}
