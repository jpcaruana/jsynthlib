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
import javax.swing.*;

/**
 * DataSystem Driver for Roland VG88
 */

 public final class RolandVG88SysDatDriver extends Driver {
    /** Format: dir1, dir2, dir3, dir4, len **/	
    static final int[] DISPLAY_CONTRAST = {0x00, 0x00, 0x00, 0x00, 0x01};
    static final int[] GK_FUNK          = {0x01, 0x00, 0x00, 0x00, 0x02};
    static final int[] GLOBAL           = {0x02, 0x00, 0x00, 0x00, 0x08};
    static final int[] TUNER            = {0x03, 0x00, 0x00, 0x00, 0x02};
    static final int[] OUTPUT_SELECT    = {0x04, 0x00, 0x00, 0x00, 0x01};
    static final int[] DRIVER           = {0x05, 0x00, 0x00, 0x00, 0x90};
    static final int[] PEDAL            = {0x06, 0x00, 0x00, 0x00, 0x08};
    static final int[] DIAL             = {0x07, 0x00, 0x00, 0x00, 0x01};
    static final int[] MIDI             = {0x09, 0x00, 0x00, 0x01, 0x0f};
    static final int[] PRG_MAP_BANK0    = {0x09, 0x00, 0x02, 0x00, 0x100};
    static final int[] PRG_MAP_BANK1    = {0x09, 0x00, 0x04, 0x00, 0x100};
    static final int[] PRG_MAP_BANK2    = {0x09, 0x00, 0x06, 0x00, 0x100};

    /** Size of a single patch */
    static final int SINGLE_SIZE = 1142;

    /** Number of patches. */
    static final int NUM_PATCH = 1;

    /** Offset of patch name. */
    static final int NAME_OFFSET =0;

    /** Size of patch name. */
    static final int NAME_SIZE = 0;

    static final SysexHandler SYS_REQ = new SysexHandler
    ("F0 41 @@ 00 27 11 *dir1* *dir2* *dir3* *dir4* 00 00 *len1* *len2* *checkSum* F7");

    static final String SYSEX_ID = "F041**002712";

    public RolandVG88SysDatDriver(RolandVG88SingleDriver singleDriver) {
	super("Sys-Data", "Nacho Alonso");

	patchSize	    = SINGLE_SIZE;

	sysexID		    = SYSEX_ID;
	deviceIDoffset	= 2;

	patchNameStart	= NAME_OFFSET;
	patchNameSize	= NAME_SIZE;

	bankNumbers	= new String[] {"System Zone"};
	patchNumbers = new String[] {"System Zone"};

    }


    /**
     * 	Get Patch Name (not soported, nameSize for bank is 0)
     */
    public String getPatchName(Patch p) {
  		return "System Data";
    }


    /**
     * 	Set Patch Name (not soported, nameSize for bank is 0)
     */
    public void setPatchName(Patch p, String name) {
        ErrorMsg  warning = new ErrorMsg();
        warning.reportWarning("Advice:",
            "If you want to assign a name to this System Data patch, use 'Field1' or 'Filed2' or 'Comment' fields");
    }

    
    /**
     * Store a patch 
     */
    public void storePatch (Patch p, int bankNum, int patchNum) {
        adviceForOverwrite(p);
    }


    /**
     * Send a Patch 
     */
    public void sendPatch(Patch p) {
        adviceForOverwrite(p);
    }


    /**
     * Send to a Patch 
     */
    public void sendToPatch(Patch p) {
        adviceForOverwrite(p);
    }

    
    /**
     * Advice before overwrite system area 
     */
    public void adviceForOverwrite(Patch p) {
		int n = JOptionPane.showConfirmDialog(
				    (JFrame) null,
					"This command will overwrite your current System Area in the VG88. CONTINUE ?",
					"Atention! Confirm this action:",
					JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			super.sendPatch(p);
		}
    }


    /**
     * Request System Data to MIDI device.
     */
    public void requestPatchDump(int bankNum, int patchNum) {
        requestPack(DISPLAY_CONTRAST);
        requestPack(GK_FUNK          );
        requestPack(GLOBAL           );
        requestPack(TUNER            );
        requestPack(OUTPUT_SELECT    );
        requestPack(DRIVER           );
        requestPack(PEDAL            );
        requestPack(DIAL             );
        requestPack(MIDI             );
        requestPack(PRG_MAP_BANK0    );
        requestPack(PRG_MAP_BANK1    );
        requestPack(PRG_MAP_BANK2    );
    }


    /**
     * Request a data-pack to MIDI device.
     */
    public void requestPack(int[] pack) {
        int dir1 = pack[0];
        int dir2 = pack[1];
        int dir3 = pack[2];
        int dir4 = pack[3];
        int len1 = (int) (pack[4] / 0x80);
        int len2 = (int) (pack[4] % 0x80);
        int checkSum = -(dir1 + dir2 + dir3 + dir4 + len1 + len2) & 0x7f;
        SysexHandler.NameValue nv[]=new SysexHandler.NameValue[7];
        nv[0]=new SysexHandler.NameValue("dir1", dir1);
        nv[1]=new SysexHandler.NameValue("dir2", dir2);
        nv[2]=new SysexHandler.NameValue("dir3", dir3);
        nv[3]=new SysexHandler.NameValue("dir4", dir4);
        nv[4]=new SysexHandler.NameValue("len1", len1);
        nv[5]=new SysexHandler.NameValue("len2", len2);
        nv[6]=new SysexHandler.NameValue("checkSum",checkSum);
	  send(SYS_REQ.toSysexMessage(getDeviceID(), nv));
        try {
		Thread.sleep(300);	// wait .
	  } catch (Exception e) {
		ErrorMsg.reportStatus(e);
	  }
   }


    protected void playPatch(Patch p) {
	}
}
