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

import org.jsynthlib.core.Device;
import org.jsynthlib.core.Driver;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80SystemSetupDriver extends Driver {

    final static SysexHandler SYSEX_SETSYSTEMCOMMONJV880 = new SysexHandler(
    /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
    /*0000*/ "f0 41 @@ 46 12 00 00 00 00 01 40 34 00 01 01 00 " +
    /*0010*/ "00 03 07 00 03 0b 03 01 00 01 01 01 01 01 01 01 " +
    /*0020*/ "01 01 01 01 01 01 01 00 10 0f 00 00 01 40 40 40 " +
    /*0030*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0040*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0050*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0060*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0070*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0080*/ "40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 " +
    /*0090*/ "40 40 40 40 40 40 40 40 40 ** f7");
    
    final static SysexHandler SYSEX_SETSYSTEMCOMMONJV80 = new SysexHandler(
    /*        00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F */
    /*0000*/ "f0 41 @@ 46 12 00 00 00 00 01 40 34 00 01 01 00 " +
    /*0010*/ "00 03 07 00 03 0b 03 01 00 01 01 01 01 01 01 01 " +
    /*0020*/ "01 01 01 01 01 01 01 00 10 0f ** f7");

    final static int SYSEX_OVERHEAD = 11;
    final static int SYSTEMSETUPJV80_LEN = 44;
    final static int SYSTEMSETUPJV880_LEN = 155;

    SysexHandler setSystemCommon;
    boolean isJV80;
    
    public RolandJV80SystemSetupDriver() {
        super("System Setup", "Sander Brandenburg");
        
        bankNumbers    = null;
        patchNumbers   = JV80Constants.NOPATCHES;
        
        checksumStart  = JV80Constants.CHECKSUM_START;
        deviceIDoffset = JV80Constants.DEVICEIDOFFSET;
        sysexID        = "F041**461200000000";        
    }
    
    void setup(String model) {
        this.isJV80 = model.equals(RolandJV80Device.MODEL_JV80);
        
        if (isJV80) {
            patchSize  = SYSTEMSETUPJV80_LEN;
            setSystemCommon = SYSEX_SETSYSTEMCOMMONJV80;
        } else {
            patchSize  = SYSTEMSETUPJV880_LEN;
            setSystemCommon = SYSEX_SETSYSTEMCOMMONJV880;
        }
        checksumEnd    = patchSize - 3;
        checksumOffset = patchSize - 2;
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
	    Device dev = getDevice();
	    byte[] se = JV80Constants.sysexRequestDump.toByteArray(dev.getDeviceID() - 1, 0);
	    se[JV80Constants.ADDR1_IDX] = 0x00;
	    se[JV80Constants.ADDR2_IDX] = 0x00;
	    se[JV80Constants.ADDR3_IDX] = 0x00;
	    se[JV80Constants.ADDR4_IDX] = 0x00;
	    JV80Constants.setRequestLength(se, patchSize - SYSEX_OVERHEAD);
	    JV80Constants.sendRequestSysex(dev, se);
    }
    
    protected Patch createNewPatch() {
        byte[] sysex = setSystemCommon.toByteArray(getDeviceID() - 1, 0);
        Patch patch = new Patch(sysex, this);
        patch.setDate(new Date().toString());
        return patch; 
    }
    
    protected JSLFrame editPatch(Patch p) {
        return new RolandJV80SystemSetupEditor(p);
    }
}
