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

package synthdrivers.Line6Pod20;
import core.*;

/** Line6 Edit Buffer Driver. Used for Line6 Edit Buffer patch.
* Note that on the Pod, the edit buffer patch has an 8 byte header and the
* program patch has a 9 byte header. The only reason for having this driver
* is to be able to request an edit buffer patch. As soon as the edit buffer
* patch is received, Line6Pod20Converter converts it to a regular program 
* patch. From that point on it is handled like any other program patch.
* 
* @author Jeff Weber
*/
public class Line6Pod20EdBufDriver extends Line6Pod20SingleDriver {
    
    /** Edit Buffer Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.EDIT_DUMP_REQ_ID); //Edit Buffer Dump Request
    
    /** Constructs a Line6Pod20EdBufDriver.
    */
    public Line6Pod20EdBufDriver()
    {
        super(Constants.EDIT_PATCH_TYP_STR, Constants.AUTHOR);
        //Edit buffer patch will be converted to regular program patch right away
        // So we want supportsPatch to compare to program patch header.
        sysexID = Constants.EDIT_SYSEX_MATCH_ID;
        
        patchSize = Constants.EDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
        patchNameStart = Constants.PATCH_NAME_START; // does NOT include sysex header
        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.EDIT_BANK_LIST;
        patchNumbers = Constants.EDIT_PATCH_LIST;
    }
    
    /** Requests a dump of the Line6 edit buffer.
        * The bankNum and patchNum parameters are ignored.
        */
    public void requestPatchDump(int bankNum, int patchNum) {
        int progNum = 0;
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("progNum", progNum)));
    }
}
