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
import core.DriverUtil;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80PerformanceDriver extends Driver {

    final static SysexHandler SYSEX_SETPERFORMANCECOMMONJV880 = new SysexHandler(
            /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
            /*0000*/ "f0 41 @@ 46 12 ** ** ** ** 49 4e 49 54 49 41 4c " +
            /*0010*/ "20 44 41 54 41 02 02 64 50 00 00 3c 50 3c 00 00 " +
    		/*0020*/ "00 00 00 00 00 00 00 00 2b f7");
 
    final static SysexHandler SYSEX_SETPERFORMANCECOMMONJV80 = SYSEX_SETPERFORMANCECOMMONJV880;
 
    final static SysexHandler SYSEX_SETPERFORMANCEPARTJV880 = new SysexHandler(
            /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
            /*0000*/ "f0 41 @@ 46 12 ** ** ** ** 01 00 08 00 08 00 08 " +
            /*0010*/ "00 24 60 40 60 7f 00 01 24 60 40 60 7f 00 01 00 " +
    		/*0020*/ "00 00 7f 40 40 40 01 01 01 01 01 00 ** f7");
 
    final static SysexHandler SYSEX_SETPERFORMANCEPARTJV80 = new SysexHandler(
         	/*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
         	/*0000*/ "f0 41 @@ 46 12 ** ** ** ** 01 00 08 00 08 00 08 " +
         	/*0010*/ "00 24 60 40 60 7f 00 01 24 60 40 60 7f 00 01 00 " +
 			/*0020*/ "00 00 7f 40 40 40 01 01 01 01 01 ** f7");

    final static int PERFORMANCES_PER_BANK = 16;
    final static String PERFORMANCENUMBERS[] = DriverUtil.generateNumbers(1, PERFORMANCES_PER_BANK, "Performance ##");
    final static int PERFORMANCE_PATCHES = 8;
    final static int SYSEX_OVERHEAD = 11;
    
	SysexHandler setPerformanceCommon;
	int performanceCommonLength;
	SysexHandler setPerformancePart;
	int performancePartLength;
	int performancePartOffsets[] = new int[PERFORMANCE_PATCHES];
 
	boolean isJV80;
	
    public RolandJV80PerformanceDriver() {
        super("Performance", "Sander Brandenburg");
        
        bankNumbers     = JV80Constants.BANKS;
        patchNumbers    = PERFORMANCENUMBERS;
		deviceIDoffset	= JV80Constants.DEVICEIDOFFSET;
		patchNameSize	= JV80Constants.PATCH_NAME_SIZE;
		patchNameStart	= JV80Constants.PATCH_NAME_START;
		sysexID         = JV80Constants.SYSEXID;
    }
    
    void setup(String model) {
        this.isJV80 = model.equals(RolandJV80Device.MODEL_JV80);
        if (isJV80) {
            setPerformanceCommon    = SYSEX_SETPERFORMANCECOMMONJV80;
        	performanceCommonLength = 0x1F + SYSEX_OVERHEAD;
        	setPerformancePart      = SYSEX_SETPERFORMANCEPARTJV80;
        	performancePartLength   = 0x22 + SYSEX_OVERHEAD;
        	patchSize               = performanceCommonLength + PERFORMANCE_PATCHES * performancePartLength;
        } else {
            setPerformanceCommon    = SYSEX_SETPERFORMANCECOMMONJV880;
        	performanceCommonLength = 0x1F + SYSEX_OVERHEAD;
        	setPerformancePart      = SYSEX_SETPERFORMANCEPARTJV880;
        	performancePartLength   = 0x23 + SYSEX_OVERHEAD;
        	patchSize               = performanceCommonLength + PERFORMANCE_PATCHES * performancePartLength;
        }
        for (int i = 0; i < PERFORMANCE_PATCHES; i++)
            performancePartOffsets[i] = performanceCommonLength + i * performancePartLength;
    }
    
    public void storePatch(Patch p, int bankNum, int performanceNum) {
		setPerformanceNum(p.sysex, bankNum, performanceNum);
		calculateChecksum(p);
		sendPatchWorker(p);
    }

    //Sends a patch to the synth's edit buffer.
    public void sendPatch(Patch p) {
        setPerformanceNum(p.sysex, -1, 0);
        calculateChecksum(p);
        sendPatchWorker(p);
    }

    public void calculateChecksum(Patch p) {
        calculateChecksum(p, 0);
    }

    public void calculateChecksum(Patch p, int offset) {
        JV80Constants.calculateChecksum(p.sysex, offset, performanceCommonLength - SYSEX_OVERHEAD);
        for (int i = 0; i < 8; i++) {
            JV80Constants.calculateChecksum(p.sysex, offset + performancePartOffsets[i], performancePartLength - SYSEX_OVERHEAD);
        }
    }
    
    public void setPerformanceNum(byte[] sysex, int bankNum, int performanceNum) {
        setPerformanceNum(sysex, 0, bankNum, performanceNum);
    }
    
    // set temporary performance with banknum -1 and patchnum 0
    public void setPerformanceNum(byte[] sysex, int offset, int bankNum, int performanceNum) {
        sysex[offset + JV80Constants.ADDR1_IDX] = (byte) (0x01 + bankNum);
        sysex[offset + JV80Constants.ADDR2_IDX] = (byte) (performanceNum);
        sysex[offset + JV80Constants.ADDR3_IDX] = 0x10;
        sysex[offset + JV80Constants.ADDR4_IDX] = 0x00;
        for (int i = 0; i < 8; i++) {
            sysex[offset + performancePartOffsets[i] + JV80Constants.ADDR1_IDX] = (byte) (0x01 + bankNum);
            sysex[offset + performancePartOffsets[i] + JV80Constants.ADDR2_IDX] = (byte) (performanceNum);
            sysex[offset + performancePartOffsets[i] + JV80Constants.ADDR3_IDX] = (byte) (0x18 + i);
            sysex[offset + performancePartOffsets[i] + JV80Constants.ADDR4_IDX] = 0x00;
        }
    }
    
    public Patch createNewPatch() {
        byte sysex[] = new byte[patchSize];
        
        byte sysexcommon[] = setPerformanceCommon.toByteArray(getDeviceID(), 00);
        System.arraycopy(sysexcommon, 0, sysex, 0, sysexcommon.length);
        byte sysexpart[] = setPerformancePart.toByteArray(getDeviceID(), 00);
        for (int i = 0; i < 8; i++) {
            if (i == 7) {
                // channel
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x16] = (byte) 9;
	            // patch number
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x18] = (byte) 0;
	            // reverb and chorus switch off
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x1D] = (byte) 0;
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x1E] = (byte) 0;
            } else {
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x16] = (byte) i;
	            sysexpart[JV80Constants.ADDR4_IDX + 1 + 0x18] = (byte) i;
            }
            System.arraycopy(sysexpart, 0, sysex, performancePartOffsets[i], performancePartLength);
        }

        Patch patch = new Patch(sysex, this);
        patch.setDate(new Date().toString());
        return patch;
    }

    public JSLFrame editPatch(Patch p) {
        return new RolandJV80PerformanceEditor(p);
    }
    
	public void requestPatchDump(int bankNum, int patchNum) {
	    Device dev = getDevice();
	    byte[] sysex = JV80Constants.sysexRequestDump.toByteArray(dev.getDeviceID() - 1, 0);
        sysex[JV80Constants.ADDR1_IDX] = (byte) (0x01 + bankNum);
        sysex[JV80Constants.ADDR2_IDX] = (byte) (patchNum);
        sysex[JV80Constants.ADDR3_IDX] = 0x10;
        sysex[JV80Constants.ADDR4_IDX] = 0x00;
	    JV80Constants.setRequestLength(sysex, performanceCommonLength - SYSEX_OVERHEAD);
	    JV80Constants.sendRequestSysex(dev, sysex);
	    
	    for (int i = 0; i < 8; i++) {
	        sysex[JV80Constants.ADDR3_IDX] = (byte) (0x18 + i);
	        JV80Constants.setRequestLength(sysex, performancePartLength - SYSEX_OVERHEAD);
	        JV80Constants.sendRequestSysex(dev, sysex);
	    }
	}
}
