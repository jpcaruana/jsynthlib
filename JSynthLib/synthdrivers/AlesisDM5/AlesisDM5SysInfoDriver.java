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
import core.*;
import java.io.UnsupportedEncodingException;
import javax.swing.*;

/** Line6 Single Driver. Used for Line6 program patch.
* @author Jeff Weber
*/
public class AlesisDM5SysInfoDriver extends Driver {
    
    /** Single Program Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SYS_INFO_DUMP_REQ_ID); //System Info Dump Request
    
    /** Constructs a AlesisDM5SysInfoDriver.
    */
    public AlesisDM5SysInfoDriver()
    {
        super(Constants.SYS_INFO_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.SYS_INFO_SYSEX_MATCH_ID;
        
        patchSize = Constants.HDR_SIZE + Constants.SYS_INFO_SIZE + 1;
//        patchNameStart = Constants.PATCH_NAME_START; // includes the sysex header
//        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.SYS_INFO_BANK_LIST;
        patchNumbers = Constants.SYS_INFO_PATCH_LIST;
    }
    
    /** Constructs a AlesisDM5SysInfoDriver. Called by AlesisDM5EdBufDriver
        */
    public AlesisDM5SysInfoDriver(String patchType, String authors)
    {
        super(patchType, authors);
    }

    /** Checks whether this is a valid Alesis DM5 System Info Patch.
        * This version does not use patchString because patchString sometimes
        * has the wrong opcode for a SysInfo patch.
        */
    public boolean supportsPatch (String patchString, byte[] sysex) {
        if ((sysex[0] == Constants.SYS_INFO_DUMP_HDR_BYTES[0])
            && (sysex[1] == Constants.SYS_INFO_DUMP_HDR_BYTES[1])
            && (sysex[2] == Constants.SYS_INFO_DUMP_HDR_BYTES[2])
            && (sysex[3] == Constants.SYS_INFO_DUMP_HDR_BYTES[3])
            && (sysex[4] == Constants.SYS_INFO_DUMP_HDR_BYTES[4])
            && (sysex[5] == (byte)getChannel())
            && (sysex[6] == Constants.SYS_INFO_DUMP_HDR_BYTES[6])
            && (sysex.length == Constants.HDR_SIZE + Constants.SYS_INFO_SIZE + 1)) {
            return true;
        } else {
            return false;
        }           
    }
    
    /** Send Program Change MIDI message. The Alesis System Info driver does
        * not utilize program change messages. This method is overrided with a
        * null method.*/
    protected void setPatchNum(int patchNum) {
    }
    
    /** Send Control Change (Bank Select) MIDI message. The Alesis System Info 
        * driver does not utilize bank select. This method is overrided with a
        * null method.
        */
    protected void setBankNum(int bankNum) {
    }
    
    /** Requests a dump of the system info message.
        * This patch does not utilize bank select or program changes. 
        */
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("channel", getChannel())));
    }
}