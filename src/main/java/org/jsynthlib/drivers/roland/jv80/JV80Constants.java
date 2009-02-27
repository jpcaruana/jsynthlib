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

package org.jsynthlib.drivers.roland.jv80;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.Device;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.SysexHandler;


/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class JV80Constants {
    
    final static int PATCHCOMMONLEN = 45;
    final static int PATCHTONELEN = 127;
    final static int TONEOFFS[] = {
        PATCHCOMMONLEN, 
        PATCHCOMMONLEN + PATCHTONELEN, 
        PATCHCOMMONLEN + 2 * PATCHTONELEN,
        PATCHCOMMONLEN + 3 * PATCHTONELEN,
    };
    
    final static int PATCHOFFS[] = {
        0,
        PATCHCOMMONLEN, 
        PATCHCOMMONLEN + PATCHTONELEN, 
        PATCHCOMMONLEN + 2 * PATCHTONELEN,
        PATCHCOMMONLEN + 3 * PATCHTONELEN,
    };
       
    final static int NUM_TONES = 4;
    final static int PATCH_SIZE = PATCHCOMMONLEN + NUM_TONES * PATCHTONELEN;
    final static int DEVICEIDOFFSET = 2;
    final static int BANKIDX = 5;
    final static int PATCHIDX = 6;
    final static int TONEIDX = 7;
    final static int SIZELSB = 12;
    
    final static int PATCH_NAME_START = 9;
    final static int PATCH_NAME_SIZE = 12;
    
    final static int CHECKSUM_START = 5;
    final static int CHECKSUM_OFFSET_END = -1; // offset from EOX
    
    final static int ADDR1_IDX = 5;
    final static int ADDR2_IDX = 6;
    final static int ADDR3_IDX = 7;
    final static int ADDR4_IDX = 8;
    final static int SIZEL_IDX = 12;
    
    final static int PATCHCOMMON_DATA_LEN = 0x22;
    final static int PATCHTONE_DATA_LEN = 0x74;
    final static String SYSEXID = "F041**4612";
    
    final static int SYSREQDATALEN = 4; 
    final static SysexHandler sysexRequestDump = new SysexHandler(
    	"F0 41 @@ 46 11 *addr1* *addr2* *addr3* 00 00 00 00 *sizeL* *checksum F7");
    
    final static String NOPATCHES[] = new String[] { };
	final static String BANKS[] = new String[] { "Internal", "Card" };
	final static String PATCHNUMBERS[] = DriverUtil.generateNumbers(1, RolandJV80BankDriver.PATCHES_PER_BANK, "Patch ##");
	
	static void postSendWait() {
        try {
            Thread.sleep(20);
        } catch (Exception ignore) {}
    }

    static void calculateChecksum(byte sysex[], int datalen) {
        calculateChecksum(sysex, 0, datalen);
    }

    // offset points to the start of the message, not CHECKSUM_START!
    static void calculateChecksum(byte sysex[], int offset, int datalen) {
        DriverUtil.calculateChecksum(sysex, offset + CHECKSUM_START, offset + CHECKSUM_START + datalen + 3, offset + CHECKSUM_START + datalen + 4);
    }

    // banknum == -1: patchNum -1: patch mode temp patch, 0..7 = perf mode temp patch
    // tone -1 == patch common, tone 0..3 is tones for patch 
	static void setPatchNum(byte[] sysex, int offset, int bankNum, int patchNum, int toneNum) {
	    sysex[offset + ADDR1_IDX] = (byte) (0x01 + bankNum);
        if (patchNum == -1) {
            sysex[offset + ADDR2_IDX] = 0x08;
        } else {
            sysex[offset + ADDR2_IDX] = (byte) (0x40 + patchNum);
        }
        if (toneNum == -1) {
            sysex[offset + ADDR3_IDX] = 0x20;
        } else {
            sysex[offset + ADDR3_IDX] = (byte) (0x28 + toneNum);
        }
	}
	
	static void setRequestLength(byte[] sysex, int size) {
	    sysex[SIZEL_IDX-1] = (byte) (size >> 7);
	    sysex[SIZEL_IDX] = (byte) (size & 0x7F);
	}
	
	static void sendRequestSysex(Device dev, byte[] sysex) {
	    sysex[DEVICEIDOFFSET] = (byte) (dev.getDeviceID() - 1);
	    calculateChecksum(sysex, SYSREQDATALEN);
	    
	    try {
		    SysexMessage sm = new SysexMessage();
	        sm.setMessage(sysex, sysex.length);
		    dev.send(sm);
		    postSendWait();
	    } catch (InvalidMidiDataException imde) {
	        ErrorMsg.reportError("program eror", "Tried to send invalid midi data", imde);
	    }
	}
}
