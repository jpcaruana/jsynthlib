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

/** Line6 Single Driver. Used for Line6 program patch.
* @author Jeff Weber
*/
public class AlesisDM5PrChgDriver extends Driver {
    
    /** Single Program Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.PROG_CHNG_DUMP_REQ_ID); //System Info Dump Request
    
    /** Sysex program dump byte array representing a new program change table patch*/
    static final byte NEW_SYSEX[] =
    {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x00, (byte)0x03, (byte)0x00,
        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
        (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10,
        (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
        (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B,
        (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13,
        (byte)0x14, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06,
        (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E,
        (byte)0x0F, (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x00, (byte)0x01,
        (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09,
        (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10, (byte)0x11,
        (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04,
        (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C,
        (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14,
        (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
        (byte)0x08, (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F,
        (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x00, (byte)0x01, (byte)0x6D,
        (byte)0xF7
    };
    
    /** Constructs a AlesisDM5PrChgDriver.
    */
    public AlesisDM5PrChgDriver()
    {
        super(Constants.PROG_CHNG_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.PROG_CHNG_SYSEX_MATCH_ID;
        
        patchSize = Constants.HDR_SIZE + Constants.PROG_CHNG_SIZE + 1;
        //        patchNameStart = Constants.PATCH_NAME_START; // includes the sysex header
        //        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.PROG_CHNG_BANK_LIST;
        patchNumbers = Constants.PROG_CHNG_PATCH_LIST;
        checksumStart = Constants.HDR_SIZE;
        checksumEnd = patchSize - 3;
        checksumOffset = checksumEnd + 1;
    }
    
    /** Constructs a AlesisDM5PrChgDriver. Called by AlesisDM5EdBufDriver
        */
    public AlesisDM5PrChgDriver(String patchType, String authors)
    {
        super(patchType, authors);
    }
    
    /** Send Program Change MIDI message. The Alesis Program Change Table driver
        * does not utilize program change messages. This method is overriden
        * with a null method.
        */
    protected void setPatchNum(int patchNum) {
    }
    
    /** Send Control Change (Bank Select) MIDI message. The Alesis Program 
        * Change Table driver does not utilize bank select. This method is
        * overriden with a null method.
        */
    protected void setBankNum(int bankNum) {
    }
    
    /** Calculates the checksum for the DM5 by calling 
        calculateChecksum(Patch patch, int start, int end, int offset). This 
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
        * This patch does not utilize bank select or program changes. 
        */
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("channel", getChannel())
                                    )
             );
    }
    
    /** Creates a new program change table patch with default values.
        */
    protected Patch createNewPatch()
    {
        Patch p = new Patch(NEW_SYSEX, this);
        calculateChecksum(p);
        return p;
    }
    
    /** Opens an edit window on the specified patch.
        */
    protected JSLFrame editPatch(Patch p)
    {
        return new AlesisDM5PrChgEditor((Patch)p);
    }
}