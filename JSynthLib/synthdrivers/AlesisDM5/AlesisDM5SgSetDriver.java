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
import core.Driver;
import core.ErrorMsg;
import core.Patch;
import core.SysexHandler;

/** Line6 Single Driver. Used for Line6 program patch.
* @author Jeff Weber
*/
public class AlesisDM5SgSetDriver extends Driver {
    
    /** Single Program Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SINGL_SET_DUMP_REQ_ID); //System Info Dump Request
    
    /** Constructs a AlesisDM5SgSetDriver.
    */
    public AlesisDM5SgSetDriver()
    {
        super(Constants.SINGL_SET_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.SINGL_SET_SYSEX_MATCH_ID;
        
        patchSize = Constants.HDR_SIZE + Constants.SINGL_SET_SIZE + 1;
        patchNameStart = Constants.PATCH_NAME_START; // includes sysex header
        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.SINGL_SET_BANK_LIST;
        patchNumbers = Constants.SINGL_SET_PATCH_LIST;
        checksumStart = Constants.HDR_SIZE;
        checksumEnd = patchSize - 3;
        checksumOffset = checksumEnd + 1;
    }
    
    /** Constructs a AlesisDM5SgSetDriver. Called by AlesisDM5EdBufDriver
        */
    public AlesisDM5SgSetDriver(String patchType, String authors)
    {
        super(patchType, authors);
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
    
    /** Calculates the checksum for the DM5 by calling 
        this.calculateChecksum(Patch patch, int start, int end, int offset). This 
        needs to be included to override the version in the Driver class.
        */
    protected void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }
    
    /** Calculates the checksum for the DM5. Equal to the mod 128 of the sum of
        all the bytes from offset header+1 to offset total patchlength-3.
        */
    protected static void calculateChecksum(Patch patch, int start, int end, int offset) {
        int sum = 0;
        ErrorMsg.reportStatus("Checksum was " + patch.sysex[offset]);
        
        for (int i = start; i <= end; i++) {
            sum += patch.sysex[i];
        }
        patch.sysex[offset] = (byte)(sum % 128);
        
        ErrorMsg.reportStatus("Checksum now is " + patch.sysex[offset]);
    }
    
    /** Requests a dump of the system info message.
        * This patch does not utilize bank select. 
        */
    public void requestPatchDump(int bankNum, int patchNum) {
        patchNum += 96;
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("channel", getChannel()),
                                    new SysexHandler.NameValue("patchNum", patchNum)
                                    )
             );
    }
}