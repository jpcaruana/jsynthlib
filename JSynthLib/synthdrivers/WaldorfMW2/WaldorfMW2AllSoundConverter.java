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


/**
 * Converts an "all sounds" dump to two banks
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2AllSoundConverter extends Converter {


    public WaldorfMW2AllSoundConverter() {
        super("All sound converter", "Joachim Backhaus");

        this.sysexID = MW2Constants.SYSEX_ID;
        this.patchSize = MW2Constants.ALL_SOUNDS_SIZE;        
    }
    
    public Patch[] extractPatch(Patch basePatch) {
    	byte[] baseSysex = basePatch.getByteArray();
        Patch[] newPatchArray = new Patch[2];
        byte[] temporarySysex = new byte[MW2Constants.PATCH_SIZE];
        Patch tempPatch;
        byte[] bankSysex = new byte[MW2Constants.PATCH_SIZE * MW2Constants.PATCH_NUMBERS];

        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
            System.arraycopy(   baseSysex,
                    WaldorfMW2BankDriver.getPatchStart(patchNo),
                    temporarySysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PATCH_SIZE );
            tempPatch = new Patch(temporarySysex, getDevice());
            
            WaldorfMW2SingleDriver.createPatchHeader(tempPatch);
            WaldorfMW2SingleDriver.createPatchFooter(tempPatch);            
            WaldorfMW2SingleDriver.calculateChecksum( tempPatch,
                MW2Constants.SYSEX_HEADER_OFFSET, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1);
            
            System.arraycopy(   tempPatch.sysex,
                    0,
                    bankSysex,
                    patchNo * MW2Constants.PATCH_SIZE,
                    MW2Constants.PATCH_SIZE );            
        }                               
        
        newPatchArray[0] = new Patch(bankSysex, getDevice());
        
        for(int patchNo = 0; patchNo < patchNumbers.length; patchNo++) {
            System.arraycopy(   baseSysex,
                    WaldorfMW2BankDriver.getPatchStart(patchNo + patchNumbers.length),
                    temporarySysex,
                    MW2Constants.SYSEX_HEADER_OFFSET,
                    MW2Constants.PATCH_SIZE );
            tempPatch = new Patch(temporarySysex, getDevice());
            
            WaldorfMW2SingleDriver.createPatchHeader(tempPatch);
            WaldorfMW2SingleDriver.createPatchFooter(tempPatch);            
            WaldorfMW2SingleDriver.calculateChecksum( tempPatch,
                MW2Constants.SYSEX_HEADER_OFFSET, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE, 
                MW2Constants.SYSEX_HEADER_OFFSET + MW2Constants.PURE_PATCH_SIZE - 1);
            
            System.arraycopy(   tempPatch.sysex,
                    0,
                    bankSysex,
                    patchNo * MW2Constants.PATCH_SIZE,
                    MW2Constants.PATCH_SIZE );            
        }

        return newPatchArray;
    }
}
