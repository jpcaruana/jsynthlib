/*
 * Copyright 2005 Jeff Weber
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

package synthdrivers.BehringerFCB1010;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;

import core.*;


/** Removes "Garbage Data" from response to dump request and extracts desired patch.
* 
* When responding to a sysex data dump request, the FCB1010 sometimes sends a single sysex message and sometimes
* it sends a block of multiple messages along with garbage data (the behavior is unpredictable). The last
* valid sysex message message sent is always the one we are interested in but it can be preceded or succeeded
* with other garbage data. This converter will attempt to parse out the last FCB patch in the block and discard
* the rest of the data.
* 
* @author Jeff Weber
*/
class FCB1010Converter extends Converter {
    
    /** Constructor for FCB1010Converter */
    FCB1010Converter() {
        super(Constants.CONVERTER_NAME, Constants.AUTHOR);
        
        this.sysexID = Constants.CONV_SYSEX_MATCH_ID;
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
    }
    
    /** Extracts the last valid patch in a dump request and validates it.
    * Note that supportsPatch does not convert the patch. It only attempts to
    * parse out the last patch of a block of data and compare the header to the
    * header bytes. The job of actually converting the patch is handled by
    * extractPatch. If supportsPatch returns true, JSynthLib Core will call
    * extractPatch.
    */
    public boolean supportsPatch (String patchString, byte[] sysex) {
        if (sysex.length == Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE + 1) {
            return false;  // If sysex has already been converted, Converter no longer supports it.
        } else {
            if (this.thisSupportsPatch(parseSysex(sysex))) {  // Parse out the last patch in the block and test it.
                return true;  // If sysex contains a block'o'data, it still needs to be converted
            } else {
                return false;  // Not a FCB1010 sysex message
            }
        }
    }
    
    /** Part of the logic for the overridden supportsPatch method. Checks the
    * the sysex record against the defined header for the FCB1010
    * and returns true if there is a match.
    */
    private boolean thisSupportsPatch(byte[] sysex) {
        if (isFCBPatch(sysex,0)) {
            if (sysex.length == Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE + 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /** Extracts a patch from a patch dump response (block of data).
    * Calls parseSysex to do the extraction.
    */
    public Patch[] extractPatch(Patch p) {
    	byte[] sysex = parseSysex(p.getByteArray());
        
        Patch[] newPatchArray = new Patch[1];
        newPatchArray[0] = new Patch(sysex, new FCB1010Driver());
        return newPatchArray;
    }
    
    /** Attempts to parse an FCB patch from an FCB response to a dump request.
    * If parseSysex cannot find a valid FCB patch within the block of data, it
    * returns the original block of data, unchanged.
    */
    private byte[] parseSysex(byte[] sysex) {
        int patchLength = sysex.length;
        byte[] FCBSysex = new byte[sysex.length];
        for (int i = sysex.length-1; i >= 0; i--) {
            if (sysex[i] == Constants.FCB1010_DUMP_HDR_BYTES[0]) {
                if ((i + 4) < sysex.length) {
                    if (isFCBPatch(sysex, i)) {
                        if (sysex[i + Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE] == (byte)0xF7) {
                            patchLength = Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE + 1;
                        }
                        
                        if ((i + patchLength - 1) < sysex.length) {
                            if (sysex[i + patchLength - 1] == (byte)0xF7) {
                                System.arraycopy(sysex, i, FCBSysex, 0, (patchLength));
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        if ((FCBSysex[0] == (byte)0xF0) && (FCBSysex[patchLength-1] == (byte)0xF7)) {
            byte[] rtnSysex = new byte[patchLength];
            System.arraycopy(FCBSysex, 0, rtnSysex, 0, (patchLength));
            return rtnSysex;
        } else {
            return sysex;
        }
    }
    
    /**Checks the first 6 bytes of the patch header and returns true if the 
    * patch is a FCB1010 patch.
    */
    private boolean isFCBPatch(byte[] patchBytes, int offset) {
        boolean isFCBSysex = true;
        for (int i = 0; i < 5;  i++) {
            if (patchBytes[i + offset] != Constants.FCB1010_DUMP_HDR_BYTES[i]) {
                isFCBSysex = false;
                break;
            }         
        }
        
        return isFCBSysex;
    }
}
