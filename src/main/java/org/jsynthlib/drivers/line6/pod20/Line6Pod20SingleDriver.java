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

package org.jsynthlib.drivers.line6.pod20;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSLFrame;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;
import org.jsynthlib.core.Utility;

/** Line6 Single Driver. Used for Line6 program patch.
* @author Jeff Weber
*/
public class Line6Pod20SingleDriver extends Driver {
    
    /** Single Program Dump Request
    */
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SIGL_DUMP_REQ_ID); //Program Patch Dump Request
    
    /** Offset of the patch name in the sysex record, not including the sysex header.*/
    private static int nameStart = Constants.PATCH_NAME_START;
    
    /** Constructs a Line6Pod20SingleDriver.
        */
    public Line6Pod20SingleDriver()
    {
        super(Constants.SIGL_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.SIGL_SYSEX_MATCH_ID;
        
        patchSize = Constants.PDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
        patchNameStart = Constants.PDMP_HDR_SIZE + Constants.PATCH_NAME_START; // DOES include sysex header
        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.PRGM_BANK_LIST;
        patchNumbers = Constants.PRGM_PATCH_LIST;
    }
    
    /** Constructs a Line6Pod20SingleDriver. Called by Line6Pod20EdBufDriver
        */
    public Line6Pod20SingleDriver(String patchType, String authors)
    {
        super(patchType, authors);
    }
    
    /** Null method. Line6 devices do not use checksum.
        */
    protected void calculateChecksum(Patch p)
    {
        // Pod doesn't use checksum
    }
    
    /** Null method. Line6 devices do not use checksum.
        */
    protected void calculateChecksum(Patch p, int start, int end, int ofs)
    {
        // Pod doesn't use checksum
    }
    
    /** Gets the name of the program patch.
        * Patch p is the target program patch.
        */
    protected String getPatchName(Patch p) {
        char c[] = new char[patchNameSize];
        for (int i = 0; i < patchNameSize; i++) {
            c[i] = (char)PatchBytes.getSysexByte(p.sysex, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + nameStart);
        }
        return new String(c);
    }
    
    /** Sets the name of the program patch.
        * Patch p is the target program patch. String name
        * contains the name to be assigned to the patch.
        */
    protected void setPatchName(Patch p, String name) {
        if (name.length()<patchNameSize) name = name + "                ";
        byte nameBytes[] = new byte[patchNameSize];
        try {
            nameBytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + nameStart, nameBytes[i]);
            }
        } catch (UnsupportedEncodingException ex) {return;}
    }
    
    /** Converts a single program patch to an edit buffer patch and sends it to
        * the edit buffer. Patch p is the patch to be sent.
        */
    protected void sendPatch (Patch p)
    {
        byte[] saveSysex = p.sysex;  //Save the patch to a temp save area
        
        //Convert to a edit buffer patch
        int newSysexLength = p.sysex.length - 1;
        byte newSysex[] = new byte[newSysexLength];
        System.arraycopy(Constants.EDIT_DUMP_HDR_BYTES, 0, newSysex, 0, Constants.EDMP_HDR_SIZE);
        System.arraycopy(p.sysex, Constants.PDMP_HDR_SIZE, newSysex, Constants.EDMP_HDR_SIZE, newSysexLength - Constants.EDMP_HDR_SIZE);
        p.sysex = newSysex;
        sendPatchWorker(p);
        
        p.sysex = saveSysex;  //Restore the patch from the temp save area
    }
    
    /** Sends a a single program patch to a set patch location in the device.
        * bankNum is a user bank number in the range 0 to 9.
        * patchNum is a patch number within the bank, in the range 0 to 3.
        */
    protected void storePatch(Patch p, int bankNum, int patchNum)
    {
        int progNum = bankNum * 4 + patchNum;
        p.sysex[0] = (byte)0xF0;
        p.sysex[7] = (byte)progNum;
        sendPatchWorker(p);
        try {
            Thread.sleep (Constants.PATCH_SEND_INTERVAL);  // Delay so POD can keep up (pauses between each patch when sending a whole bank of patches)
        } catch (Exception e) {
        }
    }
    
    /** Presents a dialog instructing the user to play his instrument.
        * Line6 Pod devices do not "Play" patches, so a dialog is presented instead.
        */
    protected void playPatch(Patch p)
    {
        ErrorMsg.reportStatus(getPatchName(p)
                + "  Header -- "
                + "  " + Utility.hexDump(p.sysex, 0, Constants.PDMP_HDR_SIZE, 16)
                + "  Data -- "
                + "  " + Utility.hexDump(p.sysex, Constants.PDMP_HDR_SIZE, -1, 16));
        
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, Constants.PLAY_CMD_MSG);
    }
    
    /** Creates a new program patch with default values.
        */
    protected Patch createNewPatch()
    {
        Patch p = new Patch(Constants.NEW_SYSEX, this);
        setPatchName(p, "NewPatch        ");
        return p;
    }
    
    /** Requests a dump of a single program patch.
        * Even though, from an operational standpoint, the POD has nine banks
        * (numbered 1 through 9) of four patches each (numbered A, B, C, and D),
        * internally there is only a single bank of 36 patch locations,
        * referenced by program change numbers 0-35. By assigning the numbers 0
        * through 8 for the banks and 0 through 3 for the patches,
        * the conversion is as follows:
        * program number = (bank number * 4) + patch number 
        */
    public void requestPatchDump(int bankNum, int patchNum) {
        int progNum = bankNum * 4 + patchNum;
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("progNum", progNum)));
    }
    
    /*
     public void setBankNum (int bankNum) {
         // Not used for POD
     }
     
     public void setPatchNum (int patchNum) {
         // Not used for POD
     }
     */
    
    /** Opens an edit window on the specified patch.
        */
    protected JSLFrame editPatch(Patch p)
    {
        return new Line6Pod20SingleEditor((Patch)p);
    }
}

