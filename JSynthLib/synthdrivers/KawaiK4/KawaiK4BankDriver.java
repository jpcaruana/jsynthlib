package synthdrivers.KawaiK4;
import core.*;
import java.io.*;
import javax.swing.*;

/**
 * @version $Id$
 */

public class KawaiK4BankDriver extends BankDriver {
    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 01 00 04 *bankNum* 00 F7");

    public KawaiK4BankDriver() {
	super("Bank", "Brian Klock", 64, 4);

	sysexID = "F040**210004**00";
	deviceIDoffset = 2;
	bankNumbers = new String[] {
	    "0-Internal", "1-External"
	};
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

	singleSysexID = "F040**2*0004";
	singleSize = 140;
	// To distinguish from the Effect bank, which has the same sysexID
	patchSize = 64 * 131 + 9;
    }

    public int getPatchStart(int patchNum) {
	int start = (131 * patchNum);
	start += 8;  //sysex header
	return start;
    }

    public String getPatchName(Patch p, int patchNum) {
	int nameStart = getPatchStart(patchNum);
	nameStart += 0; //offset of name in patch data
	try {
	    StringBuffer s = new StringBuffer(new String(p.sysex, nameStart,
							 10, "US-ASCII"));
	    return s.toString();
	} catch (UnsupportedEncodingException ex) {
	    return "-";
	}
    }

    public void setPatchName(Patch p, int patchNum, String name) {
	patchNameSize = 10;
	patchNameStart = getPatchStart(patchNum);

	if (name.length() < patchNameSize)
	    name = name + "            ";
	byte[] namebytes = new byte[64];
	try {
	    namebytes = name.getBytes("US-ASCII");
	    for (int i = 0; i < patchNameSize; i++)
		p.sysex[patchNameStart + i] = namebytes[i];

        } catch (UnsupportedEncodingException ex) {
	    return;
	}
    }

    public void calculateChecksum(Patch p, int start, int end, int ofs)  {
	int i;
	int sum = 0;

	for (i = start; i <= end; i++)
	    sum += p.sysex[i];
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    }

    public void calculateChecksum(Patch p) {
	for (int i = 0; i < 64; i++)
	    calculateChecksum(p, 8 + (i * 131),
			      8 + (i * 131) + 129, 8 + (i * 131) + 130);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
	if (!canHoldPatch(p)) {
	    JOptionPane.showMessageDialog
		(null,
		 "This type of patch does not fit in to this type of bank.",
		 "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	System.arraycopy(p.sysex, 8, bank.sysex, getPatchStart(patchNum), 131);
	calculateChecksum(bank);
    }

    public Patch getPatch(Patch bank, int patchNum) {
	byte[] sysex = new byte[140];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x20; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x00; sysex[7] = /*(byte)0x00+*/(byte) patchNum;
	sysex[139] = (byte) 0xF7;
	try {
	    System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, 8, 131);
	    Patch p = new Patch(sysex, getDevice());
	    p.getDriver().calculateChecksum(p);
	    return p;
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Error in K4 Bank Driver", e);
	    return null;
	}
    }

    public Patch createNewPatch() {
	byte[] sysex = new byte[64 * 131 + 9];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x21; sysex[4] = (byte) 0x00;	sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x0;  sysex[7] = 0;
	sysex[64 * 131 + 8] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	for (int i = 0; i < 64; i++)
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
        p.sysex[3] = (byte) 0x21;
        p.sysex[6] = (byte) ((bankNum << 1));
        p.sysex[7] = (byte) 0x0;
        sendPatchWorker(p);
        try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
    }
}
