package synthdrivers.KawaiK4;
import core.*;

import java.io.*;
import javax.swing.*;

/** Driver for Kawai K4 Multi Bank
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class KawaiK4MultiBankDriver extends BankDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 77;
    /** the number of single patches in a bank patch. */
    private static final int NS = 64;

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 40 F7");

    public KawaiK4MultiBankDriver() {
	super("MultiBank", "Gerrit Gehnen", NS, 4);

        sysexID = "F040**210004**40";
        deviceIDoffset = 2;
        bankNumbers = new String[] {
	    "0-Internal", "1-External"
	};
	patchNumbers = new String[16 * 4];
	System.arraycopy(generateNumbers(1, 16, "A-##"), 0, patchNumbers,  0, 16);
	System.arraycopy(generateNumbers(1, 16, "B-##"), 0, patchNumbers, 16, 16);
	System.arraycopy(generateNumbers(1, 16, "C-##"), 0, patchNumbers, 32, 16);
	System.arraycopy(generateNumbers(1, 16, "D-##"), 0, patchNumbers, 48, 16);

	singleSysexID = "F040**2*0004";
	singleSize = HSIZE + SSIZE + 1;
	// Why this is not defined?
	//patchSize = HSIZE + SSIZE * NS + 1;
    }

    public int getPatchStart(int patchNum) {
	return HSIZE + (SSIZE * patchNum);
    }

    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 0; //offset of name in patch data
        try {
            StringBuffer s = new StringBuffer(new String(((Patch)p).sysex, nameStart,
							 10, "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

    public void setPatchName(Patch p, int patchNum, String name) {
        patchNameSize = 10;
        patchNameStart =  getPatchStart(patchNum);

        if (name.length() < patchNameSize)
	    name = name + "            ";
        byte[] namebytes = new byte[64];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++)
                ((Patch)p).sysex[patchNameStart + i] = namebytes[i];

        } catch (UnsupportedEncodingException ex) {
	    return;
	}
    }

    public void calculateChecksum(Patch ip, int start, int end, int ofs) {
    		Patch p = (Patch)ip;
    		int sum = 0;
        for (int i = start; i <= end; i++)
            sum += p.sysex[i];
        sum += 0xA5;
        p.sysex[ofs] = (byte) (sum % 128);
        // p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
        // p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
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

    public Patch getPatch(Patch bank, int patchNum) {
	byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x20; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x00; sysex[7] = (byte) (0x40 + patchNum);
	sysex[HSIZE + SSIZE] = (byte) 0xF7;
	System.arraycopy(((Patch)bank).sysex, getPatchStart(patchNum), sysex, HSIZE, SSIZE);
        try {
            Patch p = new Patch(sysex, getDevice());
            p.getDriver().calculateChecksum(p);
            return p;
        } catch (Exception e) {
	    ErrorMsg.reportError("Error", "Error in K4 MultiBank Driver", e);
	    return null;
	}
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE * NS + 1];
        sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x21; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x00; sysex[7] = 0x40;
	sysex[HSIZE + SSIZE * NS] = (byte) 0xF7;
        Patch p = new Patch(sysex, this);
        for (int i = 0; i < NS; i++)
            setPatchName(p, i, "New Patch");
        calculateChecksum(p);
        return p;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new NameValue("bankNum", bankNum << 1)));
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
        ((Patch)p).sysex[3] = (byte) 0x21;
        ((Patch)p).sysex[6] = (byte) (bankNum << 1);
        ((Patch)p).sysex[7] = (byte) 0x40;
        sendPatchWorker(p);
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
    }
}
