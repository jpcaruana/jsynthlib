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

package synthdrivers.AlesisDM5;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;


/** Removes "Garbage Data" from Alesis response to dump request and extracts desired patch.
* 
* When responding to a sysex data dump request, the DM5 sometimes sends a single sysex message and sometimes
* it sends a block of multiple messages along with garbage data (the behavior is unpredictable). The last
* valid sysex message message sent is always the one we are interested in but it can be preceded or succeeded
* with other garbage data. This converter will attempt to parse out the last DM5 patch in the block and discard
* the rest of the data. It will work with any of the five patch types  - system info, edit buffer, program change
* table, single set, or trigger setup.
* 
* @author Jeff Weber
*/
public class AlesisDM5Converter extends Converter{
    
    /** Constructor for AlesisDM5Converter */
    AlesisDM5Converter() {
        super(Constants.CONVERTER_NAME, Constants.AUTHOR);
        
        this.sysexID = Constants.CONV_SYSEX_MATCH_ID;
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
    }
    
    /** Extracts the last valid patch in a Alesis dump request and validates it.
    * Note that supportsPatch does not convert the patch. It only attempts to
    * parse out the last patch of a block of data and compare the header to the
    * Alesis header bytes. The job of actually converting the patch is handled by
    * extractPatch. If supportsPatch returns true, JSynthLib Core will call
    * extractPatch.
    */
    public boolean supportsPatch (String patchString, byte[] sysex) {
        if (this.thisSupportsPatch(parseSysex(sysex))) {  // Parse out the last patch in the block and test it.
            if ((sysex.length == Constants.HDR_SIZE + Constants.SYS_INFO_SIZE + 1)
                || (sysex.length == Constants.HDR_SIZE + Constants.PROG_CHNG_SIZE + 1)
                || (sysex.length == Constants.HDR_SIZE + Constants.TRIG_SETP_SIZE + 1)
                || (sysex.length == Constants.HDR_SIZE + Constants.EDIT_BUFF_SIZE + 1)
                || (sysex.length == Constants.HDR_SIZE + Constants.SINGL_SET_SIZE + 1)
                ) {
                return false;  // If sysex has already been converted to program or bank, Converter no longer supports it.
            } else {
                return true;  // If sysex contains a block'o'data, it still needs to be converted
            }
        } else {
            return false;  // Not a DM5 sysex message
        }
    }
    
    /** Part of the logic for the overridden supportsPatch method. Checks the
    * the sysex record against the defined headers for each of the DM5 patch
    * types and returns true if there is a match. This is a more thorough check
    * than the isDM5Patch method, in that it checks the whole header, including
    * the opcode byte.
    */
    private boolean thisSupportsPatch(byte[] sysex) {
        if (isDM5Patch(sysex,0)) {
            if (((sysex[6] == Constants.SYS_INFO_DUMP_HDR_BYTES[6])
                 && (sysex.length == Constants.HDR_SIZE + Constants.SYS_INFO_SIZE + 1))
                || ((sysex[6] == Constants.EDIT_BUFF_DUMP_HDR_BYTES[6])
                    && (sysex.length == Constants.HDR_SIZE + Constants.EDIT_BUFF_SIZE + 1))
                || ((sysex[6] == Constants.PROG_CHNG_DUMP_HDR_BYTES[6])
                    && (sysex.length == Constants.HDR_SIZE + Constants.PROG_CHNG_SIZE + 1))
                || ((sysex[6] == Constants.TRIG_SETP_DUMP_HDR_BYTES[6])
                    && (sysex.length == Constants.HDR_SIZE + Constants.TRIG_SETP_SIZE + 1))
                || ((sysex[6] >= (byte)0x20 && (sysex[6] <= (byte)0x34))
                    && (sysex.length == Constants.HDR_SIZE + Constants.SINGL_SET_SIZE + 1))
                ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /** Extracts a Alesis patch from a Alesis patch dump response (block of data).
    * Calls parseSysex to do the extraction.
    */
    public Patch[] extractPatch(Patch p) {
    	byte[] sysex = parseSysex(p.getByteArray());
        
        Patch[] newPatchArray = new Patch[1];
        if (sysex[6] == (byte)0x00) {  // Is this a system info patch?
            newPatchArray[0] = new Patch(sysex, new AlesisDM5SysInfoDriver());
        } else if (sysex[6] == (byte)0x01) {  // Is this an edit buffer patch?
            newPatchArray[0] = new Patch(sysex, new AlesisDM5EdBufDriver()); // Convert to a AlesisDM5SgSetDriver
        } else if (sysex[6] == (byte)0x03) {  // Is this a program change table patch?
            newPatchArray[0] = new Patch(sysex, new AlesisDM5PrChgDriver());
        } else if (sysex[6] == (byte)0x05) {  // Is this a trigger setup patch?
            newPatchArray[0] = new Patch(sysex, new AlesisDM5TrSetDriver());
        } else if ((sysex[6] >= (byte)0x20) && (sysex[6] <= (byte)0x34)) {  // Is this a single drumset patch?
            newPatchArray[0] = new Patch(sysex, new AlesisDM5SgSetDriver());
        }
        return newPatchArray;
    }
    
    /** Attempts to parse a Alesis patch (program, edit buffer, or bank) from 
    * a Alesis response to a dump request. If parseSysex cannot find a valid
    * Alesis patch within the block of data, it returns the original block of
    * data, unchanged.
    */
    private byte[] parseSysex(byte[] sysex) {
        int patchLength = sysex.length;
        byte[] l6Sysex = new byte[sysex.length];
        for (int i = sysex.length-1; i >= 0; i--) {
            if (sysex[i] == Constants.SYS_INFO_DUMP_HDR_BYTES[0]) {
                if ((i + 6) < sysex.length) {
                    if (isDM5Patch(sysex, i)) {
                        if ((sysex[i+6] == Constants.SYS_INFO_DUMP_HDR_BYTES[6])
                            || (sysex[i + Constants.HDR_SIZE + Constants.SYS_INFO_SIZE] == (byte)0xF7)) {
                            patchLength = Constants.HDR_SIZE + Constants.SYS_INFO_SIZE + 1;
                            if (sysex[i+6] != (byte)0x00) { //Sometimes Sys Info patch sends the wrong opcode
                                sysex[i+6] = (byte)0x00;    //Fix it!
                            }
                        } else if (sysex[i+6] == Constants.EDIT_BUFF_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.HDR_SIZE + Constants.EDIT_BUFF_SIZE + 1;
                        } else if (sysex[i+6] == Constants.PROG_CHNG_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.HDR_SIZE + Constants.PROG_CHNG_SIZE + 1;
                        } else if (sysex[i+6] == Constants.TRIG_SETP_DUMP_HDR_BYTES[6]) {
                            patchLength = Constants.HDR_SIZE + Constants.TRIG_SETP_SIZE + 1;
                        } else if ((sysex[i+6] >= (byte)0x20) && (sysex[i+6] <= (byte)0x34)) {
                            patchLength = Constants.HDR_SIZE + Constants.SINGL_SET_SIZE + 1;
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
    
    /**Checks the first 6 bytes of the patch header and returns true if the 
    * patch is any of the five DM5 patch types. This is a generic check that
    * does not include the opcode byte.
    */
    private boolean isDM5Patch(byte[] patchBytes, int offset) {
        boolean isDM5Sysex = true;
        for (int i = 0; i < 5;  i++) {
            if (patchBytes[i + offset] != Constants.SYS_INFO_DUMP_HDR_BYTES[i]) {
                isDM5Sysex = false;
                break;
            }         
        }
        
        return isDM5Sysex;
    }
}
