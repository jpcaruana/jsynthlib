//
//  Line6BassPodConverter.java
//  JSynthLib
//
//  Created by Jeff Weber on 8/29/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//
package synthdrivers.Line6BassPod;

import core.*;


/**
* Removes "Garbage Data" from Line6 response to dump request and extracts desired patch.
 * 
 * When responding to a sysex data dump request, the PODs sometimes send a single sysex message and sometimes
 * they send a block of multiple messages along with garbage data (the behavior is unpredictable). The last
 * valid sysex message message sent is always the one we are interested in but it can be preceded or succeeded
 * with other garbage data. Searching for F0 and F7 isn't always enough because sometimes the garbage data starts
 * with an F0 (with no F7) or sometimes it ends with an F7 with no F0 at the beginning. This converter will attempt
 * to parse out the last POD patch and validate it. If the block of data sent by the POD contains a valid POD patch
 * (program, edit buffer, or bank), the Converter will strip away all the unneeded data and replace the patch with
 * a new one containing only the POD sysex data we are interested in.
 * 
 * @author Jeff Weber
 */
public class Line6BassPodConverter extends Converter{
    
    /**
    Constructor for 
     */
    //    public Line6BassPodConverter() {
    Line6BassPodConverter() {
        super(Constants.CONVERTER_NAME, Constants.AUTHOR);
        
        this.sysexID = Constants.CONV_SYSEX_MATCH_ID;
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
    }
    
    /**
    */
    // If supportsPatch returns true, extractPatch will get called. If it returns false, extractPatch will not get called.
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
    
    /**
    * Extracts desired patch(program, edit buffer, or bank)
     */
    public Patch[] extractPatch(Patch p) {
    	byte[] sysex = parseSysex(p.getByteArray());
        if (sysex[6] == (byte)0x01) {  // Is this an edit buffer patch?
            sysex = convertToProgramPatch(sysex);
        }
        
        Patch[] newPatchArray = new Patch[1];
        if (sysex[6] == (byte)0x00) {  // Is this a program patch?
            newPatchArray[0] = new Patch(sysex, new Line6BassPodSingleDriver());
        } else {
            newPatchArray[0] = new Patch(sysex, new Line6BassPodBankDriver());
        }
        return newPatchArray;
    }    
    
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
    
    private byte[] convertToProgramPatch (byte[] sysex) {
        int newSysexLength = sysex.length + 1;
        byte newSysex[] = new byte[newSysexLength];
        System.arraycopy(Constants.SIGL_DUMP_HDR_BYTES, 0, newSysex, 0, Constants.PDMP_HDR_SIZE);
        System.arraycopy(sysex, Constants.EDMP_HDR_SIZE, newSysex, Constants.PDMP_HDR_SIZE, newSysexLength - Constants.PDMP_HDR_SIZE);
        return newSysex;
    }
}
