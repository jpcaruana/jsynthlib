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

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import core.Driver;
import core.ErrorMsg;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

/** Driver for Quasimidi Quasar Singles Performance's
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarSingleDriver extends Driver {

    /*
    * The Quasar sends only all 100 RAM performances (22300 Bytes)
    * or all temporary parameters and the system parameters (644 Bytes)
    *
    * The pure performance needs only 223 Bytes
    */
    public QuasimidiQuasarSingleDriver() {
        super("Single Performance", "Joachim Backhaus");

        this.sysexID = QuasarConstants.SYSEX_ID;

        // This one is never really used, just a dummy to prevent the "this synth doesn't support patch request" dialog
        this.sysexRequestDump = new SysexHandler( QuasarConstants.SYSEX_PERFORMANCE_REQUEST[0] );

        this.patchNameStart = QuasarConstants.PATCH_NAME_START;
        this.patchNameSize = QuasarConstants.PATCH_NAME_SIZE;
        this.deviceIDoffset = QuasarConstants.ARRAY_DEVICE_ID_OFFSET;

        // "Temporary" is a fake bank! Otherwise the selection of patch numbers doesn't work
        this.bankNumbers = new String[] { "RAM", "Temporary" };

        this.patchNumbers = QuasarConstants.PATCH_NUMBERS;

        // Patch size is variable (644 Bytes for manual dump from device, 223 Bytes for trimmed dump).
        // Using 223 as this can be easily requested
        this.patchSize = QuasarConstants.PATCH_SIZE;
    }

    /**
    * The Quasar uses no checksum therefore this method is empty
    */
    public void calculateChecksum(Patch p) {
        // no checksum, do nothing
    }

    /**
    * Replacement for sendPatchWorker
    * which doesn't work here as the Device ID has to be set in each one of the 6 SysEx sub parts
    *
    * @param p      The patch containing the Performance parameters
    */
    private final void doSendPatch(Patch p) {

        if (deviceIDoffset > 0) {
            int deviceID = ( getDeviceID() - 1);

            p.sysex[deviceIDoffset]                                       = (byte) deviceID;
            p.sysex[deviceIDoffset + QuasarConstants.ARRAY_PART_1_OFFSET] = (byte) deviceID;
            p.sysex[deviceIDoffset + QuasarConstants.ARRAY_PART_2_OFFSET] = (byte) deviceID;
            p.sysex[deviceIDoffset + QuasarConstants.ARRAY_PART_3_OFFSET] = (byte) deviceID;
            p.sysex[deviceIDoffset + QuasarConstants.ARRAY_PART_4_OFFSET] = (byte) deviceID;
            p.sysex[deviceIDoffset + QuasarConstants.ARRAY_NAME_OFFSET]   = (byte) deviceID;

            send(p.sysex);
        }
    }

    /**
    * Store the performance permanently
    *
    * @param p          The patch containing the performance parameters
    * @param bankNum    Ignored
    * @param patchNum   The number where to store the Performance (0 - 99)
    */
    public void storePatch(Patch p, int bankNum, int patchNum) {
        int performanceOffset = patchNum + QuasarConstants.SYSEX_PERFORMANCE_OFFSET;

        // Set the performance number
        p.sysex[QuasarConstants.ARRAY_PERFORMANCE_OFFSET]   = (byte) performanceOffset;

        // Part 1
        p.sysex[QuasarConstants.ARRAY_PART_1_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_1_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x01;
        // Part 2
        p.sysex[QuasarConstants.ARRAY_PART_2_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_2_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x02;
        // Part 3
        p.sysex[QuasarConstants.ARRAY_PART_3_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_3_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x03;
        // Part 4
        p.sysex[QuasarConstants.ARRAY_PART_4_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_4_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x04;
         // The performance name
        p.sysex[QuasarConstants.ARRAY_NAME_OFFSET   + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_NAME_OFFSET   + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x05;

        doSendPatch(p);

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        setPatchNum(patchNum);
    }

    /**
    * Send the current performance to the temporary place
    *
    * @param p          The patch containing the Performance parameters
    */
    public void sendPatch(Patch p) {
        int performanceOffset = QuasarConstants.SYSEX_TEMPORARY_OFFSET;

        // Set the performance number
        p.sysex[QuasarConstants.ARRAY_PERFORMANCE_OFFSET]   = (byte) performanceOffset;

        // Part 13
        p.sysex[QuasarConstants.ARRAY_PART_1_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_1_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x0D;
        // Part 14
        p.sysex[QuasarConstants.ARRAY_PART_2_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_2_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x0E;
        // Part 15
        p.sysex[QuasarConstants.ARRAY_PART_3_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_3_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x0F;
        // Part 16
        p.sysex[QuasarConstants.ARRAY_PART_4_OFFSET + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_PART_4_OFFSET + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x10;
        // The temporary name
        p.sysex[QuasarConstants.ARRAY_NAME_OFFSET   + QuasarConstants.ARRAY_PERFORMANCE_OFFSET] = (byte) performanceOffset;
        p.sysex[QuasarConstants.ARRAY_NAME_OFFSET   + QuasarConstants.ARRAY_PERF_PART_OFFSET]   = (byte) 0x11;

        doSendPatch(p);
    }

    /**
    * Request the dump of a single Performance
    *
    * @param bankNum    Ignored
    * @param patchNum   The number of the Performance which is requested
    */
    public void requestPatchDump(int bankNum, int patchNum) {

        if (sysexRequestDump == null) {
            JOptionPane.showMessageDialog
                (PatchEdit.getInstance(),
                 "The " + toString()
                 + " driver does not support patch getting.\n\n"
                 + "Please start the patch dump manually...",
                 "Get Patch", JOptionPane.WARNING_MESSAGE);
        }
        else {

            for(int count = 0; count < QuasarConstants.SYSEX_PERFORMANCE_REQUEST.length; count++) {
                this.sysexRequestDump = new SysexHandler( QuasarConstants.SYSEX_PERFORMANCE_REQUEST[count] );

                send(sysexRequestDump.toSysexMessage(getDeviceID(),
                         new SysexHandler.NameValue("perfNumber", patchNum + QuasarConstants.SYSEX_PERFORMANCE_OFFSET)
                                                    )
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

    /**
    * Set the name of the Performance
    *
    * @param p      The patch containing the Performance parameters
    * @param name   The name of the Performance
    */
    public void setPatchName(Patch p, String name) {
        if (patchNameSize == 0) {
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

    /*
    * Trims 644 Byte sysex files to the 223 Bytes this driver uses
    *
    public int trimSysex(Patch p) {
        if (p.sysex.length == 644) {
            byte [] sysex = new byte[QuasarConstants.PATCH_SIZE];

            // starting at 19h (=25): Temporary name: 17 Bytes
            System.arraycopy(p.sysex, 25, sysex, QuasarConstants.ARRAY_NAME_OFFSET, 17);
            // starting at 2Ah (= 42): Temporary common parameters: 74 Bytes
            System.arraycopy(p.sysex, 42, sysex, 0, 74);
            // starting at 200h (=512): Part 13: 33 Bytes
            System.arraycopy(p.sysex, 512, sysex, QuasarConstants.ARRAY_PART_1_OFFSET, 33);
            // starting at 221h (=545): Part 14: 33 Bytes
            System.arraycopy(p.sysex, 545, sysex, QuasarConstants.ARRAY_PART_2_OFFSET, 33);
            // starting at 242h (=578): Part 15: 33 Bytes
            System.arraycopy(p.sysex, 578, sysex, QuasarConstants.ARRAY_PART_3_OFFSET, 33);
            // starting at 263h (=611): Part 16: 33 Bytes
            System.arraycopy(p.sysex, 611, sysex, QuasarConstants.ARRAY_PART_4_OFFSET, 33);

            p.sysex = sysex;
        }

        return p.sysex.length;
    }*/

    /**
    * Creates a new "Single Performance"
    */
    public Patch createNewPatch() {
        return createNewPatch(0);
    }

    /**
    * Same as createNewPatch(performanceNumber, QuasarConstants.SYSEX_TEMPORARY_OFFSET);
    *
    * <strong>For internal use only!!!</strong>
    *
    * @param performanceNumber  The number where the Performance should be stored later 
    */
    private final Patch createNewPatch(int performanceNumber) {
        return createNewPatch(performanceNumber, QuasarConstants.SYSEX_TEMPORARY_OFFSET);
    }

    /**
    * Creates a new Performance
    *
    * @param performanceNumber  The number where the Performance should be stored later
    * @param sysexOffset        The offset where the Performance should be stored:<br>
    *                           QuasarConstants.SYSEX_TEMPORARY_OFFSET<br>
    *                           or<br>
    *                           QuasarConstants.SYSEX_PERFORMANCE_OFFSET<br>
    */
    public static final Patch createNewPatch(int performanceNumber, int sysexOffset) {
        byte [] sysex = new byte[223];
        int offset = 0;

         // Locations in array: 5, (79, 112, 145, 178), 211
        int performanceOffset = performanceNumber + sysexOffset;

        /*
        * Standard dump to device header
        */
        sysex[0] = QuasarConstants.SYSEX_START_BYTE;
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

        for(int count = 1; count <= 4; count++) {
            sysex[offset + 0] = (byte) 0x00; // mod.depth[SOURCEx][DEST1]
            sysex[offset + 1] = (byte) 0x00; // mod.depth[SOURCEx][DEST2]
            sysex[offset + 2] = (byte) 0x00; // mod.depth[SOURCEx][DEST3]
            sysex[offset + 3] = (byte) 0x00; // mod.depth[SOURCEx][DEST4]
            sysex[offset + 4] = (byte) 0x00; // mod.depth[SOURCEx][DEST5]
            sysex[offset + 5] = (byte) 0x00; // mod.depth[SOURCEx][DEST6]
            sysex[offset + 6] = (byte) 0x00; // mod.depth[SOURCEx][DEST7]
            sysex[offset + 7] = (byte) 0x00; // mod.depth[SOURCEx][DEST8]

            offset += 8;
        }
        // FX Parameter
        // 32 is the value of the Modulation Matrix parameters
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
         * bit 2    arp_on (0 = off, 1 = on)
         * bits 0-1 arp resolution (00 = 4, 01 = 8, 10 = 16, 11 = 32)
         */
        sysex[offset + 19] = (byte) 0x00;
        /* speed */
        sysex[offset + 20] = (byte) 0x00;
        /* gate */
        sysex[offset + 21] = (byte) 0x00;
        /* arp pak 2
        *
        * bits 5-6  arp_sync (00 = int, 01 = ext1, 10 = ext2)
        * bits 3-4  arp_dir (00 = up, 01 = down, 10 = up/down)
        * bit 2     arp_sort (0 = off, 1 = on)
        * bit 1     arp_hold (0 = off, 1 = on)
        * bit 0     arp_velo (0 = off, 1 = on)
        */
        sysex[offset + 22] = (byte) 0x00;
        /* arp pak 3
        *
        * bits 3-6  arp_track (0000 = 1, 0001 = 2, 0010 = 3, ... , 1111 = 16)
        * bit 2     arp_thru (0 = off, 1 = on)
        * bit 1     arp_out (0 = off, 1 = on)
        * bit 0     arp_freeze (0 = off, 1 = on)
        */
        sysex[offset + 23] = (byte) 0x00;
        /*
        * End of performance common parameters
        */
        sysex[offset + 24] = QuasarConstants.SYSEX_END_BYTE;

        // 74 Bytes so far ((offset = 49) + 24 + 1)
        offset += 24 + 1;

        for (int partNumber = 1; partNumber <= 4; partNumber++) {
            /*
            * Standard dump to device header
            */
            sysex[offset + 0] = QuasarConstants.SYSEX_START_BYTE;
            sysex[offset + 1] = (byte) 0x3F;
            sysex[offset + 2] = (byte) 0x00; // Device number
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
            /*
            * End of performance part parameter
            */
            sysex[offset + 24] = QuasarConstants.SYSEX_END_BYTE;

            offset += 24 + 1;
        } // End of for loop for performance part parameters

        // 33 Bytes per PART-Parameter * 4 = 132 Bytes
        // 74 Bytes + 132 Bytes = 206 Bytes so far

        /*
        * Standard dump to device header
        */
        sysex[offset + 0] = QuasarConstants.SYSEX_START_BYTE;
        sysex[offset + 1] = (byte) 0x3F;
        sysex[offset + 2] = (byte) 0x00; // Device number
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
        /*
        * End of performance name
        */
        sysex[offset + 16] = QuasarConstants.SYSEX_END_BYTE;

        // 17 Bytes
        // 206 Bytes + 17 Bytes = 223 Bytes

        Patch p = new Patch(sysex);
        return p;
    }
}

