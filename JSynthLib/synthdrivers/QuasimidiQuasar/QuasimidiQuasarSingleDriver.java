/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.QuasimidiQuasar;

import core.*;
import javax.swing.*;
import java.io.*;

/** Driver for Quasimidi Quasar Singles Performance's
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarSingleDriver extends Driver {

	public final static byte SYSEX_START_BYTE		= (byte) 0xF0;
	public final static byte SYSEX_END_BYTE			= (byte) 0xF7;
	public final static int SYSEX_HEADER_OFFSET		= 8;

	public final static int QUASAR_PATCH_SIZE   = 223;

	public final static int QUASAR_SYSEX_PERFORMANCE_OFFSET   = 0x05;
	// Offset for the temporary parameters
	public final static int QUASAR_SYSEX_TEMPORARY_OFFSET   = 0x01;

	public final static String[] QUASAR_SYSEX_PERFORMANCE_REQUEST   = {
		"F0 3F @@ 20 52 *perfNumber* 00 00 00 41 F7 ", // Request performance common parameter
		"F0 3F @@ 20 52 *perfNumber* 01 00 00 18 F7 ", // Request performance part 1 parameter
		"F0 3F @@ 20 52 *perfNumber* 02 00 00 18 F7 ", // Request performance part 2 parameter
		"F0 3F @@ 20 52 *perfNumber* 03 00 00 18 F7 ", // Request performance part 3 parameter
		"F0 3F @@ 20 52 *perfNumber* 04 00 00 18 F7 ", // Request performance part 4 parameter
		"F0 3F @@ 20 52 *perfNumber* 05 00 00 08 F7 "  // Request performance name
	};


	/*
	* The Quasar sends only all 100 RAM performances (22300 Bytes)
	* or all temporary parameters and the system parameters (644 Bytes)
	*
	* The pure performance needs only 223 Bytes
	*/
	public QuasimidiQuasarSingleDriver() {
		this.authors = "Joachim Backhaus";
		this.manufacturer = "Quasimidi";
		this.model = "Quasar";
		this.patchType = "Single Performance";
		this.id = "Quasar";

		this.sysexID = "F03F**2044";

		// This one is never really used, just a dummy to prevent the "this synth doesn't support patch request" dialog
		this.sysexRequestDump = new SysexHandler( QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_REQUEST[0] );

		this.patchNameStart = 214;
		this.patchNameSize = 8;
		this.deviceIDoffset = 2;

		// "Temporary" is a fake bank! Otherwise the selection of patch numbers doesn't work
		this.bankNumbers = new String[] { "RAM", "Temporary" };

		this.patchNumbers = new String[100];
		// Performance numbers go from 00 to 99
		for (int count = 0; count < patchNumbers.length; count++) {
			if(count < 10) {
				patchNumbers[count] = "0" + String.valueOf(count);
			}
			else {
				patchNumbers[count] = String.valueOf(count);
			}
		}

		// Patch size is variable (644 Bytes for manual dump from device, 223 Bytes for trimmed dump).
		// Using 223 as this can be easily requested
		this.patchSize = QuasimidiQuasarSingleDriver.QUASAR_PATCH_SIZE;
		
		int trimSize = QuasimidiQuasarSingleDriver.QUASAR_PATCH_SIZE;
    }

    public void calculateChecksum(Patch p,int start,int end,int ofs) {
        // no checksum
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
    	int performanceOffset = patchNum + QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_OFFSET;

        // Set the performance number
        p.sysex[5]   = (byte) performanceOffset;
        p.sysex[79]  = (byte) performanceOffset;
        p.sysex[112] = (byte) performanceOffset;
        p.sysex[145] = (byte) performanceOffset;
        p.sysex[178] = (byte) performanceOffset;
        p.sysex[211] = (byte) performanceOffset;

        sendPatchWorker(p);

		try {Thread.sleep(100); } catch (Exception e){}
		setPatchNum(patchNum);
    }

    // Send the current performance to the temporary place
    public void sendPatch (Patch p) {
    	int performanceOffset = QuasimidiQuasarSingleDriver.QUASAR_SYSEX_TEMPORARY_OFFSET;

        // Set the performance number
        p.sysex[5]   = (byte) performanceOffset;

        p.sysex[79]  = (byte) performanceOffset;
        p.sysex[80]   = (byte) 0x0D; // Part 13
        p.sysex[112] = (byte) performanceOffset;
        p.sysex[113]   = (byte) 0x0E; // Part 14
        p.sysex[145] = (byte) performanceOffset;
        p.sysex[146]   = (byte) 0x0F; // Part 15
        p.sysex[178] = (byte) performanceOffset;
        p.sysex[179]   = (byte) 0x10; // Part 16
        p.sysex[211] = (byte) performanceOffset;
        p.sysex[212] = (byte) 0x11; // The temporary name

        sendPatchWorker(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {

		if (sysexRequestDump == null) {
			JOptionPane.showMessageDialog(PatchEdit.instance,
				"The " + getDriverName() + " driver does not support patch getting.\n\nPlease start the patch dump manually...",
				"Get Patch",
				JOptionPane.WARNING_MESSAGE
			);
			byte buffer[] = new byte[256*1024];

			try {
				while (PatchEdit.MidiIn.messagesWaiting(inPort) > 0)
					PatchEdit.MidiIn.readMessage(inPort, buffer, 1024);
			} catch (Exception ex) {
				ErrorMsg.reportError("Error", "Error Clearing Midi In buffer.",ex);
			}
		}
		else {
			//"F03F**2052**********F7" is the format for requests

			for(int count = 0; count < QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_REQUEST.length; count++) {
				this.sysexRequestDump = new SysexHandler( QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_REQUEST[count] );

				sysexRequestDump.send(
					port, (byte)channel,
					new NameValue("perfNumber", patchNum + QuasimidiQuasarSingleDriver.QUASAR_SYSEX_PERFORMANCE_OFFSET)
				);

				try {
					// Wait a little bit so that everything is in the correct sequence
					Thread.sleep(50);
				} catch (Exception ex) {
					// Ignore these exceptions
				}
			}
		}
	}

	public void setPatchName(Patch p, String name) {
        if (patchNameSize==0) {
        	ErrorMsg.reportError ("Error", "The Driver for this patch does not support Patch Name Editing.");
			return;
        }

        if (name.length () < patchNameSize)
        	name = name + "            ";

        byte [] namebytes = new byte [64];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++)
                p.sysex[patchNameStart + i] = namebytes[i];

        } catch (UnsupportedEncodingException ex) {
        	return;
        }
    }
    
    /**
    * Trims 644 Byte sysex files to the 223 Bytes this driver uses
    */
	public int trimSysex(Patch p) {
		if (trimSize > 0) {
			if (p.sysex.length == 644) {
				byte [] sysex = new byte[trimSize];
								
				// starting at 19h (=25): Temporary name: 17 Bytes
				System.arraycopy(p.sysex, 25, sysex, 206, 17);
				// starting at 2Ah (= 42): Temporary common parameters: 74 Bytes
				System.arraycopy(p.sysex, 42, sysex, 0, 74);
				// starting at 200h (=512): Part 13: 33 Byte
				System.arraycopy(p.sysex, 512, sysex, 74, 33);
				// starting at 221h (=545): Part 14: 33 Byte
				System.arraycopy(p.sysex, 545, sysex, 107, 33);
				// starting at 242h (=578): Part 15: 33 Byte
				System.arraycopy(p.sysex, 578, sysex, 140, 33);
				// starting at 263h (=611): Part 16: 33 Byte
				System.arraycopy(p.sysex, 611, sysex, 173, 33);
				
				p.sysex = sysex;
			}
		}
		return p.sysex.length;
	}

    public Patch createNewPatch() {
		return createNewPatch(0);
	}

	final public Patch createNewPatch(int performanceNumber) {
		return createNewPatch(performanceNumber, QuasimidiQuasarSingleDriver.QUASAR_SYSEX_TEMPORARY_OFFSET);
	}

    final public Patch createNewPatch(int performanceNumber, int sysexOffset) {
        byte [] sysex = new byte[223];
        int offset = 0;

         // Locations in array: 5, (79, 112, 145, 178), 211
        int performanceOffset = performanceNumber + sysexOffset;

		/*
		* Standard dump to device header
		*/
        sysex[0] = QuasimidiQuasarSingleDriver.SYSEX_START_BYTE;
        sysex[1] = (byte) 0x3F;
        sysex[2] = (byte) 0x00; // Device number
        sysex[3] = (byte) 0x20;
        sysex[4] = (byte) 0x44;
        /*
		* Performance common parameter
		*/
        sysex[5] = (byte) performanceOffset;
        sysex[6] = (byte) 0x00;
        sysex[7] = (byte) 0x00;

		// sysex[offset + 0] = (byte) 0x00; //

		/*
		* Common-Parameter
		*/
		offset = 8; // 8 Bytes were used until now
		sysex[offset + 0] = (byte) 0x46; // Performance level
		sysex[offset + 1] = (byte) 0x00; // Performance mode
		sysex[offset + 2] = (byte) 0x00; // Performance value (Splitkey, detune)
		sysex[offset + 3] = (byte) 0x00; // Reserved
		sysex[offset + 4] = (byte) 0x00; // Free controller number (0 - 97)
		sysex[offset + 5] = (byte) 0x00; // Foot controller number
		sysex[offset + 6] = (byte) 0x00; // Foot control on value
		sysex[offset + 7] = (byte) 0x7F; // Foot control off value
		sysex[offset + 8] = (byte) 0x00; // Foot control toggle mode (00h = off, 01h = on)
		// Modulation matrix
		offset += 9; // There are 9 "standard" common parameters
//System.err.println("\t[QuasarDebug]: offset (should be 17): " + Integer.toString(offset));
		for(int count = 1; count <= 4; count++) {
			sysex[offset + 0] = (byte) 0x00; // mod.depth[SOURCE1][DEST1]
			sysex[offset + 1] = (byte) 0x00; // mod.depth[SOURCE1][DEST2]
			sysex[offset + 2] = (byte) 0x00; // mod.depth[SOURCE1][DEST3]
			sysex[offset + 3] = (byte) 0x00; // mod.depth[SOURCE1][DEST4]
			sysex[offset + 4] = (byte) 0x00; // mod.depth[SOURCE1][DEST5]
			sysex[offset + 5] = (byte) 0x00; // mod.depth[SOURCE1][DEST6]
			sysex[offset + 6] = (byte) 0x00; // mod.depth[SOURCE1][DEST7]
			sysex[offset + 7] = (byte) 0x00; // mod.depth[SOURCE1][DEST8]

			offset += 8;
		}
		// FX Parameter
		// 32 is the value of the Modulation Matrix parameters
//System.err.println("\t[QuasarDebug]: offset (should be 49): " + Integer.toString(offset));
		sysex[offset + 0] = (byte) 0x00; // fx1 activity (00h = off, 01h = on)
		sysex[offset + 1] = (byte) 0x00; // fx1 typ
		sysex[offset + 2] = (byte) 0x00; // fx1 parameter[PAGE1][PAR1]
		sysex[offset + 3] = (byte) 0x00; // fx1 parameter[PAGE1][PAR2]
		sysex[offset + 4] = (byte) 0x00; // fx1 parameter[PAGE1][PAR3]
		sysex[offset + 5] = (byte) 0x00; // fx1 parameter[PAGE2][PAR1]
		sysex[offset + 6] = (byte) 0x00; // fx1 parameter[PAGE2][PAR2]
		sysex[offset + 7] = (byte) 0x00; // fx1 parameter[PAGE2][PAR3]

		sysex[offset + 8]  = (byte) 0x00; // fx2 activity (00h = off, 01h = on)
		sysex[offset + 9]  = (byte) 0x00; // fx2 typ
		sysex[offset + 10] = (byte) 0x00; // fx2 parameter[PAGE1][PAR1]
		sysex[offset + 11] = (byte) 0x00; // fx2 parameter[PAGE1][PAR2]
		sysex[offset + 12] = (byte) 0x00; // fx2 parameter[PAGE1][PAR3]
		sysex[offset + 13] = (byte) 0x00; // fx2 parameter[PAGE2][PAR1]
		sysex[offset + 14] = (byte) 0x00; // fx2 parameter[PAGE2][PAR2]
		sysex[offset + 15] = (byte) 0x00; // fx2 parameter[PAGE2][PAR3]
		sysex[offset + 16] = (byte) 0x00; // fx2 parameter[PAGE3][PAR1]
		sysex[offset + 17] = (byte) 0x00; // fx2 parameter[PAGE3][PAR2]
		sysex[offset + 18] = (byte) 0x00; // fx2 parameter[PAGE4][PAR3]
		// Arpeggiator

		 /* arp pak 1
		 *
		 * bit 2	arp_on (0 = off, 1 = on)
		 * bits 0-1	arp resolution (00 = 4, 01 = 8, 10 = 16, 11 = 32)
		 */
		sysex[offset + 19] = (byte) 0x00;
		/* speed */
		sysex[offset + 20] = (byte) 0x00;
		/* gate */
		sysex[offset + 21] = (byte) 0x00;
		/* arp pak 2
		*
		* bits 5-6	arp_sync (00 = int, 01 = ext1, 10 = ext2)
		* bits 3-4	arp_dir (00 = up, 01 = down, 10 = up/down)
		* bit 2		arp_sort (0 = off, 1 = on)
		* bit 1		arp_hold (0 = off, 1 = on)
		* bit 0		arp_velo (0 = off, 1 = on)
		*/
		sysex[offset + 22] = (byte) 0x00;
		/* arp pak 3
		*
		* bits 3-6	arp_track (0000 = 1, 0001 = 2, 0010 = 3, ... , 1111 = 16)
		* bit 2		arp_thru (0 = off, 1 = on)
		* bit 1		arp_out (0 = off, 1 = on)
		* bit 0		arp_freeze (0 = off, 1 = on)
		*/
		sysex[offset + 23] = (byte) 0x00;
		/*
		* End of performance common parameters
		*/
		sysex[offset + 24] = QuasimidiQuasarSingleDriver.SYSEX_END_BYTE;

		// 74 Bytes so far ((offset = 49) + 24 + 1)
        offset += 24 + 1;
//System.err.println("\t[QuasarDebug]: offset (should be 74): " + Integer.toString(offset));

        for (int partNumber = 1; partNumber <= 4; partNumber++) {
        	/*
			* Standard dump to device header
			*/
	        sysex[offset + 0] = QuasimidiQuasarSingleDriver.SYSEX_START_BYTE;
	        sysex[offset + 1] = (byte) 0x3F;
	        sysex[offset + 2] = (byte) 0x00;
	        sysex[offset + 3] = (byte) 0x20;
	        sysex[offset + 4] = (byte) 0x44;
	        /*
			* Performance part parameter
			*/
	        sysex[offset + 5] = (byte) performanceOffset;
	        sysex[offset + 6] = (byte) partNumber;
	        sysex[offset + 7] = (byte) 0x00;

			offset += 8; // 8 Bytes were used until now

	        sysex[offset + 0] = (byte) 0x00; // Bank number
	        sysex[offset + 1] = (byte) 0x00; // Patch number
	        sysex[offset + 2] = (byte) 0x01; // Trackmode (00h = muted, 01h = poly, 02h = mono)
	        sysex[offset + 3] = (byte) 0x7F; // Level
	        sysex[offset + 4]  = (byte) 0x08; // Panorama
	        sysex[offset + 5]  = (byte) 0x00; // FX1 Send
	        sysex[offset + 6]  = (byte) 0x00; // FX2 Send
	        sysex[offset + 7]  = (byte) 0x18; // Transpose (18h = no transpose, 00h = -24, 30h = +24)
	        sysex[offset + 8]  = (byte) 0x40; // Tune (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 9]  = (byte) 0x40; // Cutoff frequency (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 10] = (byte) 0x40; // Resonance (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 11] = (byte) 0x40; // EG Attack (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 12] = (byte) 0x40; // EG Decay (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 13] = (byte) 0x40; // EG Release (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 14] = (byte) 0x40; // Vibrato rate (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 15] = (byte) 0x40; // Vibrato depth (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 16] = (byte) 0x40; // Vibrato delay (Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 17] = (byte) 0x00; // Velocity curve
	        sysex[offset + 18] = (byte) 0x00; // Holdpedal (00h = off, 01h = on)
	        sysex[offset + 19] = (byte) 0x00; // Modulation depth
	        sysex[offset + 20] = (byte) 0x0C; // Pitch sensivity (00h = -12, 0Ch = 0, 18h = +12)
	        sysex[offset + 21] = (byte) 0x40; // Volume mod. sens.(Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 22] = (byte) 0x40; // Tone mod. sens.(Offset value, -64 ... +63, 40h = 0)
	        sysex[offset + 23] = (byte) 0x00; // Portamento time

//System.err.println("\t[QuasarDebug]: offset: " + (Integer.toString(offset + 24)));

	        /*
			* End of performance part parameter
			*/
			sysex[offset + 24] = QuasimidiQuasarSingleDriver.SYSEX_END_BYTE;

			offset += 24 + 1;
        } // End of for loop for performance part parameters

        // 33 Bytes per PART-Parameter * 4 = 132 Bytes
        // 74 Bytes + 132 Bytes = 206 Bytes so far

        /*
		* Standard dump to device header
		*/
        sysex[offset + 0] = QuasimidiQuasarSingleDriver.SYSEX_START_BYTE;
        sysex[offset + 1] = (byte) 0x3F;
        sysex[offset + 2] = (byte) 0x00;
        sysex[offset + 3] = (byte) 0x20;
        sysex[offset + 4] = (byte) 0x44;
        /*
		* Performance name
		*/
        sysex[offset + 5] = (byte) performanceOffset;
        sysex[offset + 6] = (byte) 0x05;
        sysex[offset + 7] = (byte) 0x00;

        sysex[offset + 8]  = (byte) 'N';
        sysex[offset + 9]  = (byte) 'e';
        sysex[offset + 10] = (byte) 'w';
        sysex[offset + 11] = (byte) ' ';
        sysex[offset + 12] = (byte) 'P';
        sysex[offset + 13] = (byte) 'e';
        sysex[offset + 14] = (byte) 'r';
        sysex[offset + 15] = (byte) 'f';
//System.err.println("\t[QuasarDebug]: offset (should be 222): " + (Integer.toString(offset + 16)));
        /*
		* End of performance name
		*/
		sysex[offset + 16] = QuasimidiQuasarSingleDriver.SYSEX_END_BYTE;

		// 17 Bytes
		// 206 Bytes + 17 Bytes = 223 Bytes

        Patch p = new Patch(sysex);
        p.ChooseDriver();
        return p;
    }
}

