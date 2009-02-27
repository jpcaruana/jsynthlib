/*
 * Copyright 2004 Sander Brandenburg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
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
package synthdrivers.RolandJV80;

import java.util.Date;

import core.Device;
import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80PatchDriver extends Driver {
    final static SysexHandler SYSEX_SETPATCHCOMMONJV880 = new SysexHandler(
    /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
    /*0000*/ "f0 41 @@ 46 12 ** ** ** 00 49 4e 49 54 49 41 4c " +
    /*0010*/ "20 44 41 54 41 00 03 64 3c 00 00 3c 50 3c 00 00 " +
    /*0020*/ "00 7f 40 3e 02 00 00 00 01 00 32 ** f7 ");
    
    final static SysexHandler SYSEX_SETPATCHCOMMONJV80 = 
        SYSEX_SETPATCHCOMMONJV880;
    
    final static SysexHandler SYSEX_SETPATCHTONEJV880 = new SysexHandler(
        /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
        /*0000*/ "f0 41 @@ 46 12 ** ** ** 00 00 00 00 01 00 00 00 " +
        /*0010*/ "7f 01 01 00 40 00 40 00 40 00 40 00 40 00 40 00 " +
        /*0020*/ "40 00 40 00 40 00 40 00 40 00 40 00 02 01 3c 00 " +
        /*0030*/ "00 00 00 40 40 40 00 02 01 3c 00 00 00 00 40 40 " +
        /*0040*/ "40 40 40 00 0c 40 07 07 07 40 00 40 00 40 00 40 " +
        /*0050*/ "00 40 01 7f 00 00 05 00 40 07 07 07 40 00 00 00 " +
        /*0060*/ "00 00 00 00 00 7f 07 04 00 07 00 00 00 00 60 07 " +
        /*0070*/ "07 07 00 7f 00 7f 00 7f 32 7f 7f 7f 00 ** f7 ");

    
    final static SysexHandler SYSEX_SETPATCHTONEJV80 = new SysexHandler(
        /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
        /*0000*/ "f0 41 @@ 46 12 ** ** ** 00 00 00 00 01 00 00 00 " +
        /*0010*/ "7f 01 01 00 40 00 40 00 40 00 40 00 40 00 40 00 " +
        /*0020*/ "40 00 40 00 40 00 40 00 40 00 40 00 02 01 3c 00 " +
        /*0030*/ "00 00 00 40 40 40 00 02 01 3c 00 00 00 00 40 40 " +
        /*0040*/ "40 40 40 00 0c 40 07 07 07 40 00 40 00 40 00 40 " +
        /*0050*/ "00 40 01 7f 00 00 05 00 40 07 07 07 40 00 00 00 " +
        /*0060*/ "00 00 00 00 00 7f 07 04 00 07 00 00 00 00 60 07 " +
        /*0070*/ "07 07 00 7f 00 7f 00 7f 32 7f 7f 7f ** f7 ");

    public final static int SYSEX_OVERHEAD = 11;
    
    int patchToneLength;
    int patchToneOffsets[] = new int[4];
    int patchCommonLength;
    SysexHandler setPatchTone;
    SysexHandler setPatchCommon;
    boolean isJV80;
    
    public RolandJV80PatchDriver() {
		super("Patch", "Sander Brandenburg");

		bankNumbers	    = JV80Constants.BANKS;
		deviceIDoffset	= JV80Constants.DEVICEIDOFFSET;
		patchNameSize	= JV80Constants.PATCH_NAME_SIZE;
		patchNameStart	= JV80Constants.PATCH_NAME_START;
		patchNumbers    = JV80Constants.PATCHNUMBERS;
		sysexID         = JV80Constants.SYSEXID;
    }
    
    void setup(String model) {
		this.isJV80 = model.equals(RolandJV80Device.MODEL_JV80);
		if (isJV80) {
		    patchToneLength = 0x73 + 11;
		    patchCommonLength = 0x22 + 11;
		    setPatchTone = SYSEX_SETPATCHTONEJV80;
		    setPatchCommon = SYSEX_SETPATCHCOMMONJV80;
		} else {
		    patchToneLength = 0x74 + 11;
		    patchCommonLength = 0x22 + 11;
		    setPatchTone = SYSEX_SETPATCHTONEJV880;
		    setPatchCommon = SYSEX_SETPATCHCOMMONJV880;		    
		}
		for (int i = 0; i < 4; i++)
		    patchToneOffsets[i] = patchCommonLength + i * patchToneLength;
		
		patchSize = patchCommonLength + 4 * patchToneLength;
    }

    //Sends a patch to a set location on a synth.
    public void storePatch(Patch p, int bankNum, int patchNum) {
		setBankNum(bankNum);
		setPatchNum(patchNum);

		setPatchNum(p.sysex, bankNum, patchNum);
		calculateChecksum(p);
		sendPatchWorker(p);
		
		setPatchNum(patchNum);
    }

    //Sends a patch to the synth's edit buffer.
    public void sendPatch(Patch p) {
        setPatchNum(p.sysex, 0, -1, -1);

        calculateChecksum(p);
        sendPatchWorker(p);
    }

    public void calculateChecksum(Patch p) {
        calculateChecksum(p, 0);
    }

    public void calculateChecksum(Patch p, int offset) {
        JV80Constants.calculateChecksum(p.sysex, offset, patchCommonLength - SYSEX_OVERHEAD);
        for (int i = 0; i < 4; i++) {
            JV80Constants.calculateChecksum(p.sysex, offset + patchToneOffsets[i], patchToneLength - SYSEX_OVERHEAD);
        }
    }
    
    public void setPatchNum(byte[] sysex, int bankNum, int patchNum) {
        setPatchNum(sysex, 0, bankNum, patchNum);
    }
    
    public void setPatchNum(byte[] sysex, int offset, int bankNum, int patchNum) {
        JV80Constants.setPatchNum(sysex, offset, bankNum, patchNum, -1);
        for (int i = 0; i < 4; i++) {
            JV80Constants.setPatchNum(sysex, offset + patchToneOffsets[i], bankNum, patchNum, i);
        }
    }
    
    public Patch createNewPatch() {
        byte sysex[] = new byte[patchSize];
        
        byte sysexcommon[] = setPatchCommon.toByteArray(getDeviceID(), 00);
        System.arraycopy(sysexcommon, 0, sysex, 0, sysexcommon.length);
        byte sysextone[] = setPatchTone.toByteArray(getDeviceID(), 00);
        for (int i = 0; i < 4; i++) {
            System.arraycopy(sysextone, 0, sysex, patchToneOffsets[i], sysextone.length);
        }
        
        Patch p = new Patch(sysex, this);
        p.setDate(new Date().toString());
        return p;
    }

    public JSLFrame editPatch(Patch p) {
        return new RolandJV80PatchEditor(p);
    }
    
	public void requestPatchDump(int bankNum, int patchNum) {
	    Device dev = getDevice();
	    byte[] se = JV80Constants.sysexRequestDump.toByteArray(dev.getDeviceID() - 1, 0);
	    JV80Constants.setPatchNum(se, 0, bankNum, patchNum, -1);
	    JV80Constants.setRequestLength(se, patchCommonLength - SYSEX_OVERHEAD);
	    JV80Constants.sendRequestSysex(dev, se);
	    
	    for (int i = 0; i < 4; i++) {
	        JV80Constants.setPatchNum(se, 0, bankNum, patchNum, i);
	        JV80Constants.setRequestLength(se, patchToneLength - SYSEX_OVERHEAD);
	        JV80Constants.sendRequestSysex(dev, se);
	    }
	}
}
