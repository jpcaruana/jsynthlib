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

/** Constants class for AlesisDM5
* 
* @author Jeff Weber
*/
final class Constants {
    
    /** Manufacturer of device*/
    static final String MANUFACTURER_NAME = "Alesis";
    /** Name of device*/
    static final String DEVICE_NAME = "DM5";
    /** Name of Converter for device*/
    static final String CONVERTER_NAME = "Alesis DM5 All Dump Converter";
    /** Alesis DM5 Universal Device Inquiry*/
    static final String INQUIRY_ID = "F07E..0601F7";
    /** Text displayed in the synth driver device details window in preferences*/
    static final String INFO_TEXT = "Device for Alesis DM5. The DM5 must be set to \"Omni On\" to work with JSynthLib. This must be done by setting the channel to 00 on the front panel of the device. See your DM5 manual for details";
    /** Author of this Driver*/
    static final String AUTHOR = "Jeff Weber";
    
    /** Dump Header Size */
    static final int HDR_SIZE = 7;
    /** System Info Patch size (not including header and stop byte)*/
    static final int SYS_INFO_SIZE = 3;
    /** Edit Buffer Patch size (not including header and stop byte)*/
    static final int EDIT_BUFF_SIZE = 335;
    /** Program Change Table Patch size (not including header and stop byte)*/
    static final int PROG_CHNG_SIZE = 129;
    /** Trigger Setup Patch size (not including header and stop byte)*/
    static final int TRIG_SETP_SIZE = 61;
    /** Single Set Patch size (not including header and stop byte)*/
    static final int SINGL_SET_SIZE = 335;
    
    /** Offset of the patch name in the sysex record (includes the sysex header).*/
    static final int PATCH_NAME_START = 7;
    /** Patch Name--Size in bytes*/
    static final int PATCH_NAME_SIZE = 14;
    /** Offset of the device ID in the sysex record--Not used by DM5*/
    static final int DEVICE_ID_OFFSET = 0;
    
    /** List of bank numbers for system info driver*/
    static final String SYS_INFO_BANK_LIST[] = new String[] {
        "System Info"
    };
    /** List of patch numbers for system info driver*/
    static final String SYS_INFO_PATCH_LIST[] = new String[] {
        "System Info"
    };
    
    /** List of bank numbers for program change table driver*/
    static final String PROG_CHNG_BANK_LIST[] = new String[] {
        "Program Change Table"
    };
    /** List of patch numbers for program change table driver*/
    static final String PROG_CHNG_PATCH_LIST[] = new String[] {
        "Program Change Table"
    };
    
    /** List of bank numbers for trigger setup driver*/
    static final String TRIG_SETP_BANK_LIST[] = new String[] {
        "Trigger Setup"
    };
    /** List of patch numbers for trigger setup driver*/
    static final String TRIG_SETP_PATCH_LIST[] = new String[] {
        "Trigger Setup"
    };
    
    /** List of virtual bank numbers for edit buffer driver*/
    static final String EDIT_BUFF_BANK_LIST[] = new String[] {
        "Edit Buffer"
    };
    /** List of patch edit buffer driver*/
    static final String EDIT_BUFF_PATCH_LIST[] = new String[] {
        "Edit Buffer"
    };
    
    /** List of virtual bank numbers for bank driver*/
    static final String SINGL_SET_BANK_LIST[] = new String[] {
        "DM5 Patches"
    };
    /** List of patch Bank driver*/
    static final String SINGL_SET_PATCH_LIST[] = new String[] {
        "0","1","2","3",
        "4","5","6","7",
        "8","9","10","11",
        "12","13","14","15",
        "16","17","18","19",
        "20"
    };
    
    /** Converter Match ID--Used to match a patch to a AlesisDM5Converter*/
    static final String CONV_SYSEX_MATCH_ID = "F000000E13****";
    
    /** System Info Dump Request ID--Sent to DM5 for a system info dump request.*/
    static final String SYS_INFO_DUMP_REQ_ID = "F0 00 00 0E 13 *channel* 40 F7";    
    /** System Info Patch Type String*/
    static final String SYS_INFO_PATCH_TYP_STR = "System Info";
    /** System Info Sysex Match ID--Used to match a patch to an AlesisDM5SysInfoDriver*/
//    static final String SYS_INFO_SYSEX_MATCH_ID = "F000000E13**00";  Not used. Sys Info patch sometimes has wrong opcode.
    static final String SYS_INFO_SYSEX_MATCH_ID = "F000000E13****";
    /** System Info Dump Header Bytes--Bytes in a system info dump header*/
    static final byte[] SYS_INFO_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x01 
    };

    /** Edit Buffer Dump Request ID--Sent to DM5 for edit buffer dump request.*/
    static final String EDIT_BUFF_DUMP_REQ_ID = "F0 00 00 0E 13 *channel* 41 F7";    
    /** Edit Buffer Patch Type String*/
    static final String EDIT_BUFF_PATCH_TYP_STR = "Edit Buffer";
    /** Edit Buffer Sysex Match ID--Used to match a patch to an AlesisDM5EdBufDriver*/
    static final String EDIT_BUFF_SYSEX_MATCH_ID = "F000000E13**01";
    /** Edit Buffer Dump Header Bytes--Bytes in an edit buffer dump header*/
    static final byte[] EDIT_BUFF_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x01 
    };
    
    /** Program Change Dump Request ID--Sent to DM5 for Program change dump request.*/
    static final String PROG_CHNG_DUMP_REQ_ID = "F0 00 00 0E 13 *channel* 43 F7";    
    /** Program Change Patch Type String*/
    static final String PROG_CHNG_PATCH_TYP_STR = "Program Change Table";
    /** Program Change Sysex Match ID--Used to match a patch to an AlesisDM5PrChgDriver*/
    static final String PROG_CHNG_SYSEX_MATCH_ID = "F000000E13**03";
    /** Program Change Dump Header Bytes--Bytes in an program change dump header*/
    static final byte[] PROG_CHNG_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x01, (byte)0x03, (byte)0x00, (byte)0x01 
    };
    
    /** Trigger Setup Dump Request ID--Sent to DM5 for trigger setup dump request.*/
    static final String TRIG_SETP_DUMP_REQ_ID = "F0 00 00 0E 13 *channel* 45 F7";    
    /** Trigger Setup Patch Type String*/
    static final String TRIG_SETP_PATCH_TYP_STR = "Trigger Setup";
    /** Trigger Setup Sysex Match ID--Used to match a patch to an AlesisDM5TrSetDriver*/
    static final String TRIG_SETP_SYSEX_MATCH_ID = "F000000E13**05";
    /** Trigger Setup Dump Header Bytes--Bytes in a trigger setup dump header*/
    static final byte[] TRIG_SETP_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x01, (byte)0x05, (byte)0x00, (byte)0x01 
    };

    /** Single Set Dump Request ID--Sent to DM5 for Single set dump request.*/
    static final String SINGL_SET_DUMP_REQ_ID = "F0 00 00 0E 13 *channel* *patchNum* F7";    
    /** Single Set Patch Type String*/
    static final String SINGL_SET_PATCH_TYP_STR = "Single Set";
    /** Single Set Sysex Match ID--Used to match a patch to an AlesisDM5SgSetDriver*/
    static final String SINGL_SET_SYSEX_MATCH_ID = "F000000E13****";
    /** Single Set Dump Header Bytes--Bytes in a single set dump header*/
    static final byte[] SINGL_SET_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x01, (byte)0x0A, (byte)0x00, (byte)0x01 
    };
}
