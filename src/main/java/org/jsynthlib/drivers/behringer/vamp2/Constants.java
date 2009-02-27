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

package org.jsynthlib.drivers.behringer.vamp2;

/**
 * Constants class for the Behringer VAmp2
 * 
 * @author Jeff Weber
 */
final class Constants {
    
    /**
     * Manufacturer of device.
     */
    static final String MANUFACTURER_NAME = "Behringer";

    /**
     * Name of device.
     */
    static final String DEVICE_NAME = "V-Amp 2";

    /**
     * Name of Converter for device.
     */
    static final String CONVERTER_NAME = "Behringer V-Amp 2 Native Dump Converter";

    /**
     * Universal Device Inquiry - does not apply to Behringer V-Amp 2.
     */
    static final String INQUIRY_ID = "F0002032..1101F7";

    /**
     * Text displayed in the synth driver device details window in preferences.
     */
    static final String INFO_TEXT = "Device for Behringer V-Amp 2. "
            + "\n\n"
            + "Note that the device ID/MIDI channel setting in JSynthlib "
            + "preferences is numbered 0-15 while the setting on the V-Amp 2 "
            + "is numbered 1-16. In order for JSynthlib to recognize patches, "
            + "from the V-Amp 2, the setting in the preferences must be 1 less "
            + "than the setting on the V-Amp 2." + "\n\n";
    
    /**
     * Author of this Driver.
     */
    static final String AUTHOR = "Jeff Weber";

    /**
     * Header Size of single or bank patch returned from the device.
     */
    static final int HDR_SIZE = 9;

    /**
     * Size of single patch used by driver (not including header and stop byte).
     */
    static final int SINGLE_PATCH_SIZE = 48;

    /**
     * Size of bank patch used by driver (not including header and stop byte).
     */
    static final int BANK_PATCH_SIZE = 6000;

    /**
     * Offset of the patch name in the sysex record (does not include the sysex
     * header).
     */
    static final int PATCH_NAME_START = 32;

    /**
     * Patch Name--Size in bytes.
     */
    static final int PATCH_NAME_SIZE = 16;
    
    /**
     * Offset of the device ID in the sysex record. Note even though the device
     * id is at offset 4, it is not used by the driver.
     */
    static final int DEVICE_ID_OFFSET = 0;
    
    /**
     * List of virtual bank numbers for single driver.
     */
    static final String PRGM_BANK_LIST[] = new String[] { "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "24", "25" };

    /**
     * List of patches within a bank.
     */
    static final String PRGM_PATCH_LIST[] = new String[] { "A", "B", "C", "D",
            "E" };
    
    /**
     * List of bank numbers for driver. The V-Amp 2 does not use banks, so this
     * is set to an arbitrary value.
     */
    static final String BANK_BANK_LIST[] = new String[] { "VAmp2 Patches" };

    /**
     * List of patch numbers for driver.
     */
    static final String BANK_PATCH_LIST[] = new String[] {
        "1-A","1-B","1-C","1-D","1-E",
        "2-A","2-B","2-C","2-D","2-E",
        "3-A","3-B","3-C","3-D","3-E",
        "4-A","4-B","4-C","4-D","4-E",
        "5-A","5-B","5-C","5-D","5-E",
        "6-A","6-B","6-C","6-D","6-E",
        "7-A","7-B","7-C","7-D","7-E",
        "8-A","8-B","8-C","8-D","8-E",
        "9-A","9-B","9-C","9-D","9-E",
        "10-A","10-B","10-C","10-D","10-E",
        "11-A","11-B","11-C","11-D","11-E",
        "12-A","12-B","12-C","12-D","12-E",
        "13-A","13-B","13-C","13-D","13-E",
        "14-A","14-B","14-C","14-D","14-E",
        "15-A","15-B","15-C","15-D","15-E",
        "16-A","16-B","16-C","16-D","16-E",
        "17-A","17-B","17-C","17-D","17-E",
        "18-A","18-B","18-C","18-D","18-E",
        "19-A","19-B","19-C","19-D","19-E",
        "20-A","20-B","20-C","20-D","20-E",
        "21-A","21-B","21-C","21-D","21-E",
        "22-A","22-B","22-C","22-D","22-E",
        "23-A","23-B","23-C","23-D","23-E",
        "24-A","24-B","24-C","24-D","24-E",
        "25-A","25-B","25-C","25-D","25-E"
    };
    
    /**
     * Converter Match ID--Used to match a patch to a VAmp2Converter.
     */
    static final String CONV_SYSEX_MATCH_ID = "F0 00 20 32 ** 0C 0F";

    /**
     * Sysex Match ID for single preset--Used to match a patch to an
     * VAmp2SingleDriver.
     */
    static final String VAMP2_SINGLE_MATCH_ID = "F0002032**1120";

    /**
     * Sysex Match ID for bank--Used to match a patch to an VAmp2BankDriver.
     */
    static final String VAMP2_BANK_MATCH_ID = "F0002032**1121";

    /**
     * Single Preset Dump Request ID--Sent to V-Amp 2 for a single preset dump
     * request.
     */
    static final String VAMP2_SINGLE_DUMP_REQ_ID = "F0 00 20 32 *channel* 11 60 *progNum* F7";
    
    /**
     * Bank Dump Request ID--Sent to V-Amp 2 for a bank dump request.
     */
    static final String VAMP2_BANK_DUMP_REQ_ID = "F0 00 20 32 *channel* 11 61 F7";

    /**
     * Patch Type String.
     */
    static final String VAMP2_PATCH_TYP_STR = "Single Preset";

    /**
     * Bank Type String.
     */
    static final String VAMP2_BANK_TYP_STR = "Bank";
    
    /** the number of single patches in an actual bank patch. */
    static final int PATCHES_PER_BANK = 125;

    /**
     * Edit Buffer Patch Type String.
     */
    static final String VAMP2_EDBUF_PATCH_TYP_STR = "Edit Buffer";
    
    /**
     * Single Dump Header Bytes--The first six bytes identifies the patch as a
     * V-Amp 2 patch.
     */
    static final byte[] VAMP2_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x32, (byte)0x01, (byte)0x11, (byte)0x20, (byte)0x00, (byte)0x30
    };
    
    /**
     * Single Dump Header Bytes--The first six bytes identifies the patch as a
     * V-Amp 2 patch.
     */
    static final byte[] BANK_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x32, (byte)0x01, (byte)0x11, (byte)0x21, (byte)0x7D, (byte)0x30
    };
    
    /**
     * Message displayed when the Play command is invoked.
     */
    static final String PLAY_CMD_MSG = "Play your guitar to hear the patch.";
    
    /**
     * Delay of pauses in milliseconds between each patch when sending a whole
     * bank of patches. Delay is needed so VAmp can keep up.
     */
    static final int PATCH_SEND_INTERVAL = 200;

    /**
     * Sysex program dump byte array representing a new preset.
     */
    static final byte[] NEW_SINGLE_SYSEX = {
            (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x32, (byte)0x03, (byte)0x11, (byte)0x20, (byte)0x7F,
            (byte)0x30, (byte)0x29, (byte)0x66, (byte)0x30, (byte)0x31, (byte)0x6C, (byte)0x57, (byte)0x36,
            (byte)0x07, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x02, (byte)0x06, (byte)0x00,
            (byte)0x00, (byte)0x01, (byte)0x24, (byte)0x21, (byte)0x40, (byte)0x40, (byte)0x01, (byte)0x15,
            (byte)0x1E, (byte)0x72, (byte)0x2D, (byte)0x7B, (byte)0x04, (byte)0x1B, (byte)0x49, (byte)0x40,
            (byte)0x1C, (byte)0x4E, (byte)0x65, (byte)0x77, (byte)0x20, (byte)0x50, (byte)0x61, (byte)0x74,
            (byte)0x63, (byte)0x68, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20,
            (byte)0x20, (byte)0xF7
    }; 
}