/*
 * Copyright 2005 Joachim Backhaus
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

package synthdrivers.WaldorfMW2;

import core.Converter;
import core.Patch;

/*
 * 
 * TODO 
 * - In the bank names are obscure special characters after import an all sound dump -> fixed
 */
/**
 * Converts an "all sounds" dump to two banks
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2AllSoundConverter extends Converter {


    public WaldorfMW2AllSoundConverter() {
        super("All sound converter", "Joachim Backhaus");

        this.sysexID = MW2Constants.SYSEX_ID + "101000";
        // Should be 65.545 Bytes
        this.patchSize = MW2Constants.ALL_SOUNDS_SIZE;        
    }
    
    /**
     * Get the index where the patch starts in the banks SysEx data.
     */
    private int getPatchStart(int patchNo) {                                                    
        return (MW2Constants.PURE_PATCH_SIZE * patchNo) + MW2Constants.SYSEX_HEADER_OFFSET;
    }        
    
    public Patch[] extractPatch(Patch basePatch) {
    	byte[] baseSysex = basePatch.getByteArray();
        Patch[] newPatchArray = new Patch[2];
        byte[] temporarySysex = new byte[MW2Constants.PATCH_SIZE];
        Patch tempPatch;
        byte[] bankSysex = new byte[MW2Constants.PATCH_SIZE * MW2Constants.PATCH_NUMBERS];

        // Convert the sounds of bank A        
        for(int patchNo = 0; patchNo < MW2Constants.PATCH_NUMBERS; patchNo++) {
            System.arraycopy(   baseSysex,
                    getPatchStart(patchNo),
                    temporarySysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PURE_PATCH_SIZE + MW2Constants.SYSEX_FOOTER_SIZE );
            tempPatch = new Patch(temporarySysex, getDevice());
            
            WaldorfMW2SingleDriver.createPatchHeader(tempPatch, 0, patchNo);                        
            WaldorfMW2SingleDriver.calculateChecksum( tempPatch.sysex,
                MW2Constants.SYSEX_HEADER_OFFSET, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE);
            
            System.arraycopy(   tempPatch.sysex,
                    0,
                    bankSysex,
                    patchNo * MW2Constants.PATCH_SIZE,
                    MW2Constants.PATCH_SIZE );            
        }                               
        
        tempPatch = new Patch(bankSysex, getDevice());
        tempPatch.setComment("Bank A from an all sound dump.");
        newPatchArray[0] = tempPatch;        
        
        // Convert the sounds of bank B
        
        // 32.678 Bytes (Without the header!!!)
        int halfSize = MW2Constants.PURE_PATCH_SIZE * MW2Constants.PATCH_NUMBERS;
        int index = 0;
        bankSysex = new byte[MW2Constants.PATCH_SIZE * MW2Constants.PATCH_NUMBERS];
        
        for(int patchNo = 0; patchNo < MW2Constants.PATCH_NUMBERS; patchNo++) {
            index = halfSize + getPatchStart(patchNo);
            System.arraycopy(   baseSysex,
                    index,
                    temporarySysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PURE_PATCH_SIZE + MW2Constants.SYSEX_FOOTER_SIZE );
            tempPatch = new Patch(temporarySysex, getDevice());
            
            WaldorfMW2SingleDriver.createPatchHeader(tempPatch, 1, patchNo);                       
            WaldorfMW2SingleDriver.calculateChecksum(tempPatch.sysex,
                MW2Constants.SYSEX_HEADER_OFFSET, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE);
            
            System.arraycopy(   tempPatch.sysex,
                    0,
                    bankSysex,
                    patchNo * MW2Constants.PATCH_SIZE,
                    MW2Constants.PATCH_SIZE );            
        }
        
        tempPatch = new Patch(bankSysex, getDevice());
        tempPatch.setComment("Bank B from an all sound dump.");
        newPatchArray[1] = tempPatch;

        return newPatchArray;
    }
}
