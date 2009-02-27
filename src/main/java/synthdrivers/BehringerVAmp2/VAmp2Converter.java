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

package synthdrivers.BehringerVAmp2;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.Utility;

/**
 * Removes "Garbage Data" from response to dump request and extracts desired
 * patch.
 * 
 * When responding to a sysex data dump request, the device sometimes sends a
 * single sysex message and sometimes it sends a block of multiple messages
 * along with garbage data (the behavior is unpredictable). The last valid sysex
 * message sent is always the one we are interested in but it can be preceded or
 * succeeded with other garbage data. This converter will attempt to parse out
 * the last FCB patch in the block and discard the rest of the data.
 * 
 * @author Jeff Weber
 */
public class VAmp2Converter extends Converter {

    /** Constructor for VAmp2Converter
     */
    VAmp2Converter() {
        super(Constants.CONVERTER_NAME, Constants.AUTHOR);

        this.sysexID = Constants.CONV_SYSEX_MATCH_ID;
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
    }

    /**
     * Extracts the last valid patch in a dump request and validates it. Note
     * that supportsPatch does not convert the patch. It only attempts to parse
     * out the last patch of a block of data and compare the header to the
     * V-Amp 2 header bytes. The job of actually converting the patch is handled
     * by extractPatch. If supportsPatch returns true, JSynthLib Core will call
     * extractPatch.
     * @param patchString
     *              String representing the patch header & identifying information of a driver.
     * @param sysex
     *              Byte array containing the sysex data.
     * @return
     *              Boolean representing if the patch is supported by this driver. A value
     *              of true indicates the patch is supported.
     */
    public boolean supportsPatch(String patchString, byte[] sysex) {
        if ((sysex.length == Constants.HDR_SIZE + Constants.SINGLE_PATCH_SIZE
                + 1)
                || (sysex.length == Constants.HDR_SIZE
                        + Constants.BANK_PATCH_SIZE + 1)) {
            return false; // If sysex has already been converted, Converter no
                          // longer supports it.
        } else {
            if (this.thisSupportsPatch(parseSysex(sysex))) { // Parse out the
                                                             // last patch in
                                                             // the block and
                                                             // test it.
                return true; // If sysex contains a block'o'data, it still needs
                             // to be converted
            } else {
                return false; // Not a VAmp2 sysex message
            }
        }
    }

    /**
     * Part of the logic for the overridden supportsPatch method. Checks the the
     * sysex record against the defined headers for each of the VAmp patch types
     * and returns true if there is a match. This is a more thorough check than
     * the isVAmpPatch method, in that it checks the whole header, including the
     * opcode byte.
     * @param sysex
     *              Byte array containing the sysex data.
     * @return
     *              Boolean representing if the patch is supported by this driver. A value
     *              of true indicates the patch is supported.
     */
    private boolean thisSupportsPatch(byte[] sysex) {
        if (isVAmpPatch(sysex, 0)) {
            if ((sysex.length == Constants.HDR_SIZE
                    + Constants.SINGLE_PATCH_SIZE + 1)
                    || (sysex.length == Constants.HDR_SIZE
                            + Constants.BANK_PATCH_SIZE + 1)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Extracts a V-Amp 2 patch from a V-Amp 2 patch dump response (block of
     * data). Calls parseSysex to do the extraction.
     * @return
     *          A patch object containing the extracted sysex data.
     */
    public Patch[] extractPatch(Patch p) {
        byte[] sysex = parseSysex(p.getByteArray());

        Patch[] newPatchArray = new Patch[1];
        newPatchArray[0] = new Patch(sysex, new VAmp2SingleDriver());

        ErrorMsg.reportStatus(">>>>>>> Patch From Device <<<<<<<<<");
        ErrorMsg.reportStatus("  "
                + Utility.hexDump(newPatchArray[0].sysex, 0, -1, 16));
        ErrorMsg.reportStatus(">>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<");

        return newPatchArray;
    }

    /**
     * Attempts to parse a VAmp patch from an VAmp response to a dump request.
     * If parseSysex cannot find a valid VAmp patch within the block of data, it
     * returns the original block of data, unchanged.
     * @return
     *          Sysex data for the last valid patch in a block of data, or if it cannot
     *          extract a valid patch, the original block of data is returned unchanged.
     */
    private byte[] parseSysex(byte[] sysex) {
        int patchLength = sysex.length;
        byte[] VAmpSysex = new byte[sysex.length];
        for (int i = sysex.length - 1; i >= 0; i--) {
            if (sysex[i] == Constants.VAMP2_DUMP_HDR_BYTES[0]) {
                if (isVAmpPatch(sysex, i)) {
                    if ((i + Constants.HDR_SIZE + Constants.SINGLE_PATCH_SIZE) < sysex.length
                            && sysex[i + Constants.HDR_SIZE
                                    + Constants.SINGLE_PATCH_SIZE] == (byte) 0xF7) {
                        patchLength = Constants.HDR_SIZE
                                + Constants.SINGLE_PATCH_SIZE + 1;
                    }
                    if ((i + Constants.HDR_SIZE + Constants.BANK_PATCH_SIZE) < sysex.length
                            && sysex[i + Constants.HDR_SIZE
                                    + Constants.BANK_PATCH_SIZE] == (byte) 0xF7) {
                        patchLength = Constants.HDR_SIZE
                                + Constants.BANK_PATCH_SIZE + 1;
                    }

                    if ((i + patchLength - 1) < sysex.length) {
                        if (sysex[i + patchLength - 1] == (byte) 0xF7) {
                            System.arraycopy(sysex, i, VAmpSysex, 0,
                                    (patchLength));
                            break;
                        }
                    }
                }
            }
        }

        if ((VAmpSysex[0] == (byte) 0xF0)
                && (VAmpSysex[patchLength - 1] == (byte) 0xF7)) {
            byte[] rtnSysex = new byte[patchLength];
            System.arraycopy(VAmpSysex, 0, rtnSysex, 0, (patchLength));
            return rtnSysex;
        } else {
            return sysex;
        }
    }

    /**
     * Checks the first 5 bytes of the patch header and returns true if the
     * patch is a VAmp patch. This is a generic check that does not include the
     * opcode byte.
     * @return
     *          A boolean representing whether a patch is a valid V-Amp patch or not.
     *          A value of true indicates the patch is a valid V-Amp patch.
     */
    private boolean isVAmpPatch(byte[] patchBytes, int offset) {
        int identLength = 5;

        boolean isVAmpSysex = true;

        if (identLength + offset < patchBytes.length) {
            for (int i = 0; i <= identLength; i++) {
                if (i != 4) {
                    if (patchBytes[i + offset] != Constants.VAMP2_DUMP_HDR_BYTES[i]) {
                        isVAmpSysex = false;
                        break;
                    }
                }
            }
        } else {
            isVAmpSysex = false;
        }

        return isVAmpSysex;
    }
}