package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.io.*;

/** Driver for Kawai K4 Singles's
 *
 * @version $Id$
 */
public class KawaiK4SingleDriver extends Driver {
    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 *bankNum* *patchNum* F7");

    public KawaiK4SingleDriver() {
	super("Single", "Brian Klock");
	sysexID = "F040**2*0004";

	patchSize = 140;
	patchNameStart = 8;
	patchNameSize = 10;
	deviceIDoffset = 2;
	checksumStart = 8;
	checksumEnd = 137;
	checksumOffset = 138;
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
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
	setBankNum(bankNum);
	setPatchNum(patchNum);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	p.sysex[3] = (byte) 0x20;

	p.sysex[6] = (byte) (bankNum << 1);
	p.sysex[7] = (byte) (patchNum);
	sendPatchWorker(p);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	setPatchNum(patchNum);
    }

    public void sendPatch(Patch p) {
	p.sysex[3] = (byte) 0x23;
	p.sysex[7] = (byte) 0x00;
	sendPatchWorker(p);
    }

    public void calculateChecksum(Patch p, int start, int end, int ofs) {
	int i;
	int sum = 0;

	for (i = start; i <= end; i++)
	    sum += p.sysex[i];
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    }

    public Patch createNewPatch() {
	byte[] sysex = new byte[140];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x23; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x0; sysex[139] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new KawaiK4SingleEditor(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
	clearMidiInBuffer();
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new NameValue("bankNum", bankNum << 1),
				    new NameValue("patchNum", patchNum)));
    }
}
