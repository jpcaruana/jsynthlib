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

package synthdrivers.YamahaTG100;

import core.Driver;
import core.JSLFrame;
import core.Patch;


/**
 * Driver for Yamaha TG100 Singles's (Yamaha calls them "Voices")
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class YamahaTG100SingleDriver extends Driver {

    /*
    * Sends only "all" dumps of size 8266 in Bytes
    */
    public YamahaTG100SingleDriver() {
        super("Voice", "Joachim Backhaus");

        this.sysexID = TG100Constants.SYSEX_ID;
/*
        this.sysexRequestDump = new SysexHandler(
            "F0 43 10 7A 4C 4D 20 20 30 30 36 38 52 51 30 *adressOffset2* *adressOffset3*"
            + " *byteCount1* *byteCount2* *byteCount3* 00 00 00 00 00 00 00 00 00 *checksum* F7" );
            */

        this.patchNameStart = TG100Constants.PATCH_NAME_START;
        this.patchNameSize = TG100Constants.PATCH_NAME_SIZE;

        // Use always 0x10 as Device ID, else it doesn't work
        //this.deviceIDoffset = 2;
        this.deviceIDoffset = 0;

        this.checksumStart = TG100Constants.CHECKSUM_START;
        this.checksumEnd = TG100Constants.CHECKSUM_END;
        this.checksumOffset = TG100Constants.CHECKSUM_OFFSET;

        this.bankNumbers = new String[] { "Internal voice bank" }; // Bank is ignored since there is only one bank for internal voices

        this.patchNumbers = Driver.generateNumbers(1, TG100Constants.PATCH_NUMBER_LENGTH, "#");

        this.patchSize = TG100Constants.PATCH_SIZE;
    }

    // For internal use only!!!
    private void storePatch(Patch p, int patchNum) {
        int iTemp = TG100Constants.SYSEX_VOICE_START_ADDRESS3
                    + (patchNum * TG100Constants.SYSEX_SINGLE_VOICE_SIZE);

        ((Patch)p).sysex[5] = (byte) ((iTemp / 128) + TG100Constants.SYSEX_VOICE_START_ADDRESS2 );
        ((Patch)p).sysex[6] = (byte) (iTemp % 128);

        calculateChecksum(p);
        sendPatchWorker(p);
    }

    /**
    * Saves the Patch to Voice 1 as there is no Edit Buffer
    */
    public void sendPatch(Patch p) {
        setPatchNum(0);
        storePatch(p, 0);
    }

    /**
    * Stores the Patch in the internal memory
    *
    * @param p          The Voice data
    * @param bankNum    Ignored
    * @param patchNum   The number of the internal voice memory
    */
    public void storePatch (Patch p, int bankNum, int patchNum) {
        //setBankNum(64);
        setPatchNum(patchNum);
        storePatch(p, patchNum);
    }

    /*
    * Request the dump of a single Voice
    *
    * @param bankNum    Ignored
    * @param patchNum   The number of the Voice which is requested
    *
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
            int iTemp = TG100Constants.SYSEX_VOICE_START_ADDRESS3
                        + (patchNum * TG100Constants.SYSEX_SINGLE_VOICE_SIZE);
            int startAddress2 = (int) ((iTemp / 128) + TG100Constants.SYSEX_VOICE_START_ADDRESS2);
            int startAddress3 = (int) (iTemp % 128);

            int sum = 0;
            int checksum = 0;

            //sum = 0x4C + 0x4D + 0x20 + 0x20 + 0x30 + 0x30 + 0x36 + 0x38 + 0x52 + 0x51
//                    + 0x30 + startAddress2 + startAddress3 + TG100Constants.SYSEX_SINGLE_VOICE_SIZE;
            //sum = 0x30 + startAddress2 + startAddress3 + TG100Constants.SYSEX_SINGLE_VOICE_SIZE;
            sum = 0x30 + startAddress2 + startAddress3;

            checksum = (byte) (-sum & 0x7f);
            //checksum = (byte) 0x00;

            NameValue[] nameValues = {
                new NameValue("adressOffset2", startAddress2 ),
                new NameValue("adressOffset3", startAddress3 ),
                new NameValue("byteCount1", 0),
                new NameValue("byteCount2", 0),
                new NameValue("byteCount3", TG100Constants.SYSEX_SINGLE_VOICE_SIZE),
                new NameValue("checksum", checksum)
            };

            send(sysexRequestDump.toSysexMessage(getDeviceID(), nameValues) );

        }
    }*/

    public Patch createNewPatch() {
        Patch p = this.createNewPatch(0);
        calculateChecksum(p);
        return p;
    }

    /**
    * Creates a new Patch at a given memory number.
    *
    * @param patchNum   The number of Voice memory
    */
    public static final Patch createNewPatch(int patchNum) {

        byte [] sysex = new byte[TG100Constants.PATCH_SIZE];
        int iTemp = TG100Constants.SYSEX_VOICE_START_ADDRESS3
                    + (patchNum * TG100Constants.SYSEX_SINGLE_VOICE_SIZE);


        // Create new patch on internal voice 1
        sysex[0]  = (byte) 0xF0;
        sysex[1]  = (byte) 0x43;
        sysex[2]  = (byte) 0x10; // Device number
        sysex[3]  = (byte) 0x27;

        // Internal voice 0 (= 1 in the TG-100 UI)
        sysex[4]  = (byte) 0x30;
        sysex[5]  = (byte) ((iTemp / 128) + TG100Constants.SYSEX_VOICE_START_ADDRESS2 );
        sysex[6]  = (byte) (iTemp % 128);

        // Common Parameter (24 Bytes)
        sysex[7]  = (byte) 0x02; // 2 Elements
        sysex[8]  = (byte) 0x7F; // Level 127
        sysex[9]  = (byte) 0x7F; // Level 127
        sysex[10] = (byte) 0x40;
        sysex[11] = (byte) 0x40;
        sysex[12] = (byte) 0x01;
        sysex[13] = (byte) 0x0F;
        sysex[14] = (byte) 0x00; // don't care
        sysex[15] = (byte) 0x00;
        sysex[16] = (byte) 0x00; // don't care
        sysex[17] = (byte) 0x00;
        sysex[18] = (byte) 0x3C;
        sysex[19] = (byte) 0x40;
        sysex[20] = (byte) 0x40;
        sysex[21] = (byte) 0x00;
        sysex[22] = (byte) 0x3C;
        // 23 - 30 : Patch Name
        sysex[23] = (byte) 'N';
        sysex[24] = (byte) 'e';
        sysex[25] = (byte) 'w';
        sysex[26] = (byte) 'V';
        sysex[27] = (byte) 'o';
        sysex[28] = (byte) 'i';
        sysex[29] = (byte) 'c';
        sysex[30] = (byte) 'e';

        // Element 1 parameter (36 Bytes)
        sysex[31] = (byte) 0x00; sysex[32] = (byte) 0x00; // Waveform 0
        sysex[33] = (byte) 0x40;
        sysex[34] = (byte) 0x40;
        sysex[35] = (byte) 0x40;
        sysex[36] = (byte) 0x40;
        sysex[37] = (byte) 0x40;
        sysex[38] = (byte) 0x40;
        sysex[39] = (byte) 0x08; sysex[40] = (byte) 0x00;
        sysex[41] = (byte) 0x08; sysex[42] = (byte) 0x00;
        sysex[43] = (byte) 0x08; sysex[44] = (byte) 0x00;
        sysex[45] = (byte) 0x08; sysex[46] = (byte) 0x00;
        sysex[47] = (byte) 0x00; // Panpot
        sysex[48] = (byte) 0x04; // LFO Speed
        sysex[49] = (byte) 0x00; // LFO Delay
        sysex[50] = (byte) 0x00; // "don't care"
        sysex[51] = (byte) 0x00;
        sysex[52] = (byte) 0x00;
        sysex[53] = (byte) 0x00;
        sysex[54] = (byte) 0x01;
        sysex[55] = (byte) 0x01;
        sysex[56] = (byte) 0x00;
        sysex[57] = (byte) 0x3F; // P-EG R1 (Rate 1)
        sysex[58] = (byte) 0x3F; // P-EG R2 (Rate 2)
        sysex[59] = (byte) 0x3F; // P-EG R3 (Rate 3)
        sysex[60] = (byte) 0x3F; // P-EG RR (Release Rate)
        sysex[61] = (byte) 0x40; // P-EG LO (Level Offset???)
        sysex[62] = (byte) 0x40; // P-EG L1 (Level 1)
        sysex[63] = (byte) 0x40; // P-EG L2 (Level 2)
        sysex[64] = (byte) 0x40; // P-EG L3 (Level 3)
        sysex[65] = (byte) 0x40; // P-EG RL (Release Level)
        sysex[66] = (byte) 0x00; // Velocity curve

        // Element 2 parameter (36 Bytes)
        sysex[67] = (byte) 0x00; sysex[68] = (byte) 0x00; // Waveform 0
        sysex[69] = (byte) 0x40;
        sysex[70] = (byte) 0x40;
        sysex[71] = (byte) 0x40;
        sysex[72] = (byte) 0x40;
        sysex[73] = (byte) 0x40;
        sysex[74] = (byte) 0x40;
        sysex[75] = (byte) 0x08; sysex[76] = (byte) 0x00;
        sysex[77] = (byte) 0x08; sysex[78] = (byte) 0x00;
        sysex[79] = (byte) 0x08; sysex[80] = (byte) 0x00;
        sysex[81] = (byte) 0x08; sysex[82] = (byte) 0x00;
        sysex[83] = (byte) 0x00; // Panpot
        sysex[84] = (byte) 0x04; // LFO Speed
        sysex[85] = (byte) 0x00; // LFO Delay
        sysex[86] = (byte) 0x00; // "don't care"
        sysex[87] = (byte) 0x00;
        sysex[88] = (byte) 0x00;
        sysex[89] = (byte) 0x00;
        sysex[90] = (byte) 0x01;
        sysex[91] = (byte) 0x01;
        sysex[92] = (byte) 0x00;
        sysex[93] = (byte) 0x3F; // P-EG R1 (Rate 1)
        sysex[94] = (byte) 0x3F; // P-EG R2 (Rate 2)
        sysex[95] = (byte) 0x3F; // P-EG R3 (Rate 3)
        sysex[96] = (byte) 0x3F; // P-EG RR (Release Rate)
        sysex[97] = (byte) 0x40; // P-EG L0 (Level 0)
        sysex[98] = (byte) 0x40; // P-EG L1 (Level 1)
        sysex[99] = (byte) 0x40; // P-EG L2 (Level 2)
        sysex[100] = (byte) 0x40; // P-EG L3 (Level 3)
        sysex[101] = (byte) 0x40; // P-EG RL (Release Level)
        sysex[102] = (byte) 0x00; // Velocity curve

        // 103 is the checksum

        sysex[104] = (byte) 0xF7;


        Patch p = new Patch(sysex);
        
        return p;
    }
    
    public JSLFrame editPatch(Patch p) {
	    return new YamahaTG100SingleEditor(p);
    }
}

