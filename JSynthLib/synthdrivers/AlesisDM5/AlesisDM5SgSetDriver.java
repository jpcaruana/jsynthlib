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

/** Alesis DM5 Single Set Driver
* @author Jeff Weber
*/
public class AlesisDM5SgSetDriver extends Driver {
    
    /** Single Program Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SINGL_SET_DUMP_REQ_ID); //System Info Dump Request
    
    /** Sysex program dump byte array representing a new drumset patch*/
    private static final byte NEW_SYSEX[] =
    {
        (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x0E, (byte)0x13, (byte)0x00, (byte)0x01, (byte)0x54,
        (byte)0x65, (byte)0x73, (byte)0x74, (byte)0x20, (byte)0x50, (byte)0x61, (byte)0x74, (byte)0x63,
        (byte)0x68, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x20, (byte)0x24, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00,
        (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63,
        (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00,
        (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C,
        (byte)0x63, (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x3D, (byte)0xF7
    };
    
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
    
    /** Sends a single drumset patch to a set location on the DM5.<p>
        * Overrides the Driver.storePatch method to embed the program number
        * in the patch. The input bankNum parameter is not used. The input
        * patch number specifies the location (0 thru 20). Location numbers
        * in the DM5 are are represented as binary 0010 0000 through
        * 0011 0100 (32 plus patch number 0-20).
        */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        p.sysex[6] = (byte)(32 + patchNum);
        calculateChecksum(p);
        sendPatchWorker(p);
    }
    
    /** Send Program Change MIDI message. The Alesis Single Set driver does
        * not utilize the program change the same way other devices do. Instead
        * it embeds the program number in the patch. This is done by the overridden
        * storePatch method. This method is overridden by a null method.
        */
    protected void setPatchNum(int patchNum) {
    }
    
    /** Send Control Change (Bank Select) MIDI message. The Alesis Single Set 
        * driver does not utilize bank select. This method is overriden with a
        * null method.
        */
    protected void setBankNum(int bankNum) {
    }
    
    /** Sends a patch to the edit buffer on the DM5.<p>
        * Overrides the Driver.sendPatch method to embed the program number
        * in the patch, in this case the value is 1.
        */
    protected void sendPatch(Patch p) {
        p.sysex[6] = 1;
        calculateChecksum(p);
        sendPatchWorker(p);
    }

    /** Calculates the checksum for the DM5 by calling 
        * this.calculateChecksum(Patch patch, int start, int end, int offset). This 
        * needs to be included to override the version in the Driver class.
        */
    protected void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }
    
    /** Calculates the checksum for the DM5. Equal to the mod 128 of the sum of
        * all the bytes from offset header+1 to offset total patchlength-3.
        */
    protected static void calculateChecksum(Patch patch, int start, int end, int offset) {
        int sum = 0;
        
        for (int i = start; i <= end; i++) {
            sum += patch.sysex[i];
        }
        patch.sysex[offset] = (byte)(sum % 128);
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
    
    /** Creates a new single drumset patch with default values.
        */
    protected Patch createNewPatch()
    {
        Patch p = new Patch(NEW_SYSEX, this);
        setPatchName(p, "NewPatch      ");
        calculateChecksum(p);
        return p;
    }
    
    /** Opens an edit window on the specified patch.
        */
    protected JSLFrame editPatch(Patch p)
    {
        return new AlesisDM5SgSetEditor((Patch)p);
    }
}