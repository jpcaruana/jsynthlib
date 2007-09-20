/*
 * Copyright 2006 Nacho Alonso
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.RolandVG88;

import core.*;


/**
 * Single Driver for Roland VG88
 */

 public final class RolandVG88SingleDriver extends Driver {

    /** Size of a single patch */
    static final int SINGLE_SIZE = 140 * 5 + 76;

    /** Number of patches. */
    static final int NUM_PATCH = 100;

    /** Offset of patch name. */
    static final int NAME_OFFSET = SINGLE_SIZE - 42;

    /** Size of patch name. */
    static final int NAME_SIZE = 8;

    /** Patch must be sent in 6 packets (140bytes * 5) +76 bytes */
    static final int[] PKT_SIZE = {140,140,140,140,140,76};

    /** Packets in a single patch. */
    static final int NUM_PKT = PKT_SIZE.length;

    static final SysexHandler SYS_REQ = new SysexHandler
    ("F0 41 @@ 00 27 11 0C *patchNum* 00 00 00 00 05 40 *checkSum* F7");

    static final String SYSEX_ID = "F041**002712";

    static final String[] BANK_NUMBERS = new String[] {"User Zone"};
    
    static final String[]PATCH_NUMBERS = new String[] {
	    " 1-1", " 1-2", " 1-3", " 1-4",
	    " 2-1", " 2-2", " 2-3", " 2-4",
	    " 3-1", " 3-2", " 3-3", " 3-4",
	    " 4-1", " 4-2", " 4-3", " 4-4",
	    " 5-1", " 5-2", " 5-3", " 5-4",
	    " 6-1", " 6-2", " 6-3", " 6-4",
	    " 7-1", " 7-2", " 7-3", " 7-4",
	    " 8-1", " 8-2", " 8-3", " 8-4",
	    " 9-1", " 9-2", " 9-3", " 9-4",
	    "10-1", "10-2", "10-3", "10-4",
	    "11-1", "11-2", "11-3", "11-4",
	    "12-1", "12-2", "12-3", "12-4",
	    "13-1", "13-2", "13-3", "13-4",
	    "14-1", "14-2", "14-3", "14-4",
	    "15-1", "15-2", "15-3", "15-4",
	    "16-1", "16-2", "16-3", "16-4",
	    "17-1", "17-2", "17-3", "17-4",
	    "18-1", "18-2", "18-3", "18-4",
	    "19-1", "19-2", "19-3", "19-4",
	    "10-1", "10-2", "10-3", "10-4",
	    "21-1", "21-2", "21-3", "21-4",
	    "22-1", "22-2", "22-3", "22-4",
	    "23-1", "23-2", "23-3", "23-4",
	    "24-1", "24-2", "24-3", "24-4",
	    "25-1", "25-2", "25-3", "25-4"
    };
  
    /** patch file name for createNewPatch() */
    private static final String patchDefFileName = "RolandVG88DefaultPatch.syx";

    public RolandVG88SingleDriver() {
	super("Patch", "Nacho Alonso");

	patchSize	    = SINGLE_SIZE;

	sysexID		    = SYSEX_ID;
	deviceIDoffset	= 2;

	patchNameStart	= NAME_OFFSET;
	patchNameSize	= NAME_SIZE;

	bankNumbers	    = BANK_NUMBERS;
	patchNumbers    = PATCH_NUMBERS;
    }


    /**
     * Store a patch 
     */
    public void storePatch (Patch p, int bankNum, int patchNum) {
		storePatchVG88(p, patchNum);
    }


    /**
     * Send a patch in multiple sysex
     */
    void storePatchVG88(Patch p, int patchNum) {
		arrangePatchVG88(p, patchNum);
		int size;
		int offset = 0;
		for (int i = 0; i < NUM_PKT; i++, offset += size) {
			// create a Patch data for each packet
			size = PKT_SIZE[i];
			byte [] tmpSysex = new byte [size];
			System.arraycopy(p.sysex, offset, tmpSysex, 0, size);
			try {
				send(tmpSysex);
			} catch (Exception e) {
				ErrorMsg.reportStatus(e);
			}
			try {
				Thread.sleep(50);	// wait at least 50 milliseconds.
			} catch (Exception e) {
				ErrorMsg.reportStatus(e);
			}
		}
    }


    /**
     * Update data and checksums for each pack in a Patch 
     */
    public void arrangePatchVG88(Patch p, int patchNum) {
		int size;
		int offset = 0;
		for (int i = 0; i < NUM_PKT; i++, offset += size) {
			// calculate data for each packet
			size = PKT_SIZE[i];
			p.sysex[offset + 2] = (byte) (getDeviceID() - 1);
	    	p.sysex[offset + 6] = (byte) 0x0c;
			p.sysex[offset + 7] = (byte) patchNum;
			p.sysex[offset + 8] = (byte) i;
			calculateChecksum(p, offset + 6, offset + size - 3, offset + size - 2);
		}
    }


    /**
     * Send a Patch to an edit buffer of MIDI device.
     * Use last user-patch 25-4 (100) as an edit buffer.
     */
    public void sendPatch(Patch p) {
        storePatch (p, 0, NUM_PATCH - 1);
    }


    /**
     * Calculate and update checksum of a Patch.
     */
    void calculateChecksum(Patch p, int offset) {
        int size;
        for (int i = 0; i < NUM_PKT; i++, offset += size) {
            size = PKT_SIZE[i];
            int chkSumIdx = offset + size - 2;
            calculateChecksum(p, offset + 6, chkSumIdx - 1, chkSumIdx);
        }
    }


    /**
     * Calculate and update checksum of a Patch.
     */
    public void calculateChecksum(Patch p) {
	calculateChecksum(p, 0);
    }


    /**
     * Create new patch using a patch file
     */
    public Patch createNewPatch() {
        return (Patch) DriverUtil.createNewPatch(this, patchDefFileName, SINGLE_SIZE);
    }


    /**
     * Request a Patch to MIDI device.
     */
    public void requestPatchDump(int bankNum, int patchNum) {
	int checkSum = -(0x0c + patchNum + 0x05 +0x40) & 0x7f;
	send(SYS_REQ.toSysexMessage(getDeviceID(),
				    new SysexHandler.NameValue("patchNum", patchNum),
				    new SysexHandler.NameValue("checkSum", checkSum)));
   }


    /**
     * Get the patch name.
     */
    public String getPatchName(Patch p) {
        try {
	    int k = 0;
            char c[] = new char[patchNameSize];
            for (int i = 0; i < (patchNameSize); i++) {
                c[i] = (char) p.sysex[k + patchNameStart];
                c[i] = (char)((c[i] * 16) + p.sysex[k + 1 + patchNameStart]);
	        k = k + 2;
	    }
            return new String(c);
        }
        catch (Exception ex)
        {
            return "-";
        }
    }


    /**
     * set the patch name.
     */
    public void setPatchName(Patch p, String name) {
	byte[] namebytes = name.getBytes();
	byte c;
	int k = 0;
	for (int i = 0; i < (patchNameSize); i++) {
	    if (i < name.length()) {
		c = namebytes[i];
		if ((c < 1) || (c > 127))
		    c = 0x20;  // convert invalid character to space
	    }
	    else 
		c = 0x20;  // pad with spaces
	    p.sysex[k + patchNameStart] = (byte) (c / 16);
	    p.sysex[k + 1 + patchNameStart] = (byte) (c % 16);
	    k = k +2;
	}
    }
 
    protected void playPatch(Patch p) {
        ErrorMsg  warning = new ErrorMsg();
            ErrorMsg.reportWarning("Advice: ",
            "VG88 can't play anything by itself. You can test the sound with your guitar.");
	}

	/**
     * Invoke Single Editor.
     */
	//    public JSLFrame editPatch(Patch p) {
	// 	ErrorMsg.reportStatus("editPatch: " + device);
	//	return new RolandVG88SingleEditor(p);
	//    }

}
