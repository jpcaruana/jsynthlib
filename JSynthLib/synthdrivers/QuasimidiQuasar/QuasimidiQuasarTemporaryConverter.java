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


/**
 * Converts temporary parameter SysEx data (644 Bytes) to a "Single Performance" (223 Bytes)
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarTemporaryConverter extends Converter {


    public QuasimidiQuasarTemporaryConverter() {
        super("Temporary Converter", "Joachim Backhaus");

        this.sysexID = QuasarConstants.SYSEX_ID;
        this.patchSize = QuasarConstants.TEMPORARY_SIZE;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = QuasarConstants.ARRAY_DEVICE_ID_OFFSET; 
    }

    /**
    * Converts 644 Byte sysex files to the 223 Bytes the "Single Performance" driver uses
    */
    public IPatch[] extractPatch(IPatch ip) {
    	byte[] sysex = ip.getByteArray();
        IPatch[] newPatchArray = new Patch[1];
        byte [] temporarySysex = new byte[QuasarConstants.PATCH_SIZE];

        // starting at 19h (=25): Temporary name: 17 Bytes
        System.arraycopy(sysex,  25, temporarySysex, QuasarConstants.ARRAY_NAME_OFFSET, 17);
        // starting at 2Ah (= 42): Temporary common parameters: 74 Bytes
        System.arraycopy(sysex,  42, temporarySysex, 0, 74);
        // starting at 200h (=512): Part 13: 33 Bytes
        System.arraycopy(sysex, 512, temporarySysex, QuasarConstants.ARRAY_PART_1_OFFSET, 33);
        // starting at 221h (=545): Part 14: 33 Bytes
        System.arraycopy(sysex, 545, temporarySysex, QuasarConstants.ARRAY_PART_2_OFFSET, 33);
        // starting at 242h (=578): Part 15: 33 Bytes
        System.arraycopy(sysex, 578, temporarySysex, QuasarConstants.ARRAY_PART_3_OFFSET, 33);
        // starting at 263h (=611): Part 16: 33 Bytes
        System.arraycopy(sysex, 611, temporarySysex, QuasarConstants.ARRAY_PART_4_OFFSET, 33);

        newPatchArray[0] = new Patch(temporarySysex, getDevice());

        return newPatchArray;
    }
}
