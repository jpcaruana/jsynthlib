/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.drivers.line6.pod20;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;


/** Removes "Garbage Data" from Line6 response to dump request and extracts desired patch.
* 
* When responding to a sysex data dump request, the PODs sometimes send a single sysex message and sometimes
* they send a block of multiple messages along with garbage data (the behavior is unpredictable). The last
* valid sysex message message sent is always the one we are interested in but it can be preceded or succeeded
* with other garbage data. This converter will attempt to parse out the last Pod patch in the block and discard
* the rest of the data. It will work with any of the three patch types (program, edit buffer, or bank).
* This converter will also convert an edit buffer patch to a normal program patch.
* 
* @author Jeff Weber
*/
public class Line6Pod20Converter extends Converter{
    
    /** Constructor for Line6Pod20Converter */
    Line6Pod20Converter() {
        super(Constants.CONVERTER_NAME, Constants.AUTHOR);
        
        this.sysexID = Constants.CONV_SYSEX_MATCH_ID;
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
    }
    
    /** Extracts the last valid patch in a Line6 dump request and validates it.
    * Note that supportsPatch does not convert the patch. It only attempts to
    * parse out the last patch of a block of data and compare the header to the
    * Line6 header bytes. The job of actually converting the patch is handled by
    * extractPatch. If supportsPatch returns true, JSynthLib Core will call
    * extractPatch.
    */
    // 
    // if block of data contains (as the last valid line6 patch) a program patch or a bank patch--
    // (Not testing for edit buffer patch here because if it is, it still needs to be converted.)
    public boolean supportsPatch (String patchString, byte[] sysex) {
        if (super.supportsPatch(patchString, parseSysex(sysex))) {  // Parse out the last patch in the block and test it.
            if ((sysex.length == Constants.PDMP_HDR_SIZE + Constants.SIGL_SIZE + 1)
                || (sysex.length == Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK) + 1)) {
                return false;  // If sysex has already been converted to program or bank, Converter no longer supports it.
            } else {
                return true;  // If sysex contains a block'o'data (or edit buffer sysex), it still needs to be converted
            }
        } else {
            return false;  // Not a POD sysex message
        }
    }
    
    /** Extracts a Line6 patch from a Line6 patch dump response (block of data).
    * Calls parseSysex to do the extraction. If the extracted patch is an edit
    * buffer patch, calls convertToProgramPatch.
    */
    public Patch[] extractPatch(Patch p) {
    	byte[] sysex = parseSysex(p.getByteArray());
        if (sysex[6] == (byte)0x01) {  // Is this an edit buffer patch?
            sysex = convertToProgramPatch(sysex);
        }
        
        Patch[] newPatchArray = new Patch[1];
        if (sysex[6] == (byte)0x00) {  // Is this a program patch?
            newPatchArray[0] = new Patch(sysex, new Line6Pod20SingleDriver());
        } else {
            newPatchArray[0] = new Patch(sysex, new Line6Pod20BankDriver());
        }
        return newPatchArray;
    }    
    
    /** Attempts to parse a Line6 patch (program, edit buffer, or bank) from 
    * a Line6 response to a dump request. If parseSysex cannot find a valid
    * Line6 patch within the block of data, it returns the original block of
    * data, unchanged.
    */
    private byte[] parseSysex(byte[] sysex) {
        int patchLength = sysex.length;
        byte[] l6Sysex = new byte[sysex.length];
        for (int i = sysex.length-1; i > 0; i--) {
            if (sysex[i] == Constants.SIGL_DUMP_HDR_BYTES[0]) {
                if ((i + 6) < sysex.length) {
                    if ((sysex[i+1] == Constants.SIGL_DUMP_HDR_BYTES[1])
                        && (sysex[i+2] == Constants.SIGL_DUMP_HDR_BYTES[2])
                        && (sysex[i+3] == Constants.SIGL_DUMP_HDR_BYTES[3])
                        && (sysex[i+4] == Constants.SIGL_DUMP_HDR_BYTES[4])
                        && (sysex[i+5] == Constants.SIGL_DUMP_HDR_BYTES[5])) {
                        
                        if (sysex[i+6] == Constants.SIGL_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.PDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
                        }
                        if (sysex[i+6] == Constants.EDIT_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.EDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
                        }
                        if (sysex[i+6] == Constants.BANK_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK) + 1;
                        }
                        
                        if ((i + patchLength - 1) < sysex.length) {
                            if (sysex[i + patchLength - 1] == (byte)0xF7) {
                                System.arraycopy(sysex, i, l6Sysex, 0, (patchLength));
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        if ((l6Sysex[0] == (byte)0xF0) && (l6Sysex[patchLength-1] == (byte)0xF7)) {
            byte[] rtnSysex = new byte[patchLength];
            System.arraycopy(l6Sysex, 0, rtnSysex, 0, (patchLength));
            return rtnSysex;
        } else {
            return sysex;
        }
    } 
    
    /** Converts a Line6 Edit Buffer patch to a Line6 program patch.
    * Any time an edit buffer patch is received from a Line6 device it is
    * converted to a program patch. From that point on it is handled like
    * any other program patch.
    */
    private byte[] convertToProgramPatch (byte[] sysex) {
        int newSysexLength = sysex.length + 1;
        byte newSysex[] = new byte[newSysexLength];
        System.arraycopy(Constants.SIGL_DUMP_HDR_BYTES, 0, newSysex, 0, Constants.PDMP_HDR_SIZE);
        System.arraycopy(sysex, Constants.EDMP_HDR_SIZE, newSysex, Constants.PDMP_HDR_SIZE, newSysexLength - Constants.PDMP_HDR_SIZE);
        return newSysex;
    }
}
