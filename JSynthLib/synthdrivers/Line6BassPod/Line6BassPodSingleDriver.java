//
//  Line6BassPodSingleDriver.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6BassPod;

import core.*;
import javax.swing.*;

public class Line6BassPodSingleDriver extends Driver {
    
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.SIGL_DUMP_REQ_ID); //Program Patch Dump Request
    
    public Line6BassPodSingleDriver()
    {
        super(Constants.SIGL_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.SIGL_SYSEX_MATCH_ID;
        
        patchSize = Constants.PDMP_HDR_SIZE + Constants.SIGL_SIZE + 1;
        patchNameStart = Constants.PATCH_NAME_START; // does NOT include sysex header
        patchNameSize = Constants.PATCH_NAME_SIZE;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.PRGM_BANK_LIST;
        patchNumbers = Constants.PRGM_PATCH_LIST;
    }
    
    public Line6BassPodSingleDriver(String patchType, String authors)
    {
        super(patchType, authors);
    }
    
    public void calculateChecksum(Patch p)
    {
        // Pod doesn't use checksum
    }
    
    public static void calculateChecksum(Patch p, int start, int end, int ofs)
    {
        // Pod doesn't use checksum
    }
    
    public String getPatchName(Patch p) {
        char c[] = new char[patchNameSize];
        for (int i = 0; i < patchNameSize; i++) {
            c[i] = (char)PatchBytes.getSysexByte(p.sysex, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart);
        }
        return new String(c);
    }
    
    public void setPatchName(Patch p, String name) {
        byte nameByte[] = name.getBytes();
        int i;
        for (i = 0; i < Math.min(name.length(), patchNameSize); i++) {
            PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart, nameByte[i]);
        }
        for (int j = i; j < patchNameSize; j++) {
            PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart, (byte)0x20);
        }
    }
    
    // When responding to a sysex data dump request, the PODs sometimes send a single sysex message and sometimes
    // they send a block of multiple messages along with garbage data (the behavior is unpredictable). The last
    // valid sysex message message sent is always the one we are interested in but it can be preceded or succeeded
    // with other garbage data. Searching for F0 and F7 isn't always enough because sometimes the garbage data starts
    // with an F0 (with no F7) or sometimes it ends with an F7 with no F0 at the beginning. This version of supportsPatch
    // contains some extra logic to extract the last message in the block (or the only message if there is only one).

    public boolean supportsPatch (String patchString, byte[] sysex) {
        if (sysex.length > patchSize) {
            sysex = parseSysex(sysex);
        }
        
        String ptchStr = PatchBytes.getPatchString(sysex, 0, patchString.length());
        boolean isPodPatch = super.supportsPatch(ptchStr, sysex);
        return isPodPatch; 
    }
    
    private byte[] parseSysex(byte[] sysex) {
        byte[] l6Sysex = new byte[patchSize];
        for (int i = sysex.length-1; i > 0; i--) {
            if (sysex[i] == Constants.SIGL_DUMP_HDR_BYTES[0]) {
                if ((i + patchSize - 1) < sysex.length) {
                    if ((sysex[i+1] == Constants.SIGL_DUMP_HDR_BYTES[1])
                        && (sysex[i+2] == Constants.SIGL_DUMP_HDR_BYTES[2])
                        && (sysex[i+3] == Constants.SIGL_DUMP_HDR_BYTES[3])
                        && (sysex[i+4] == Constants.SIGL_DUMP_HDR_BYTES[4])
                        && (sysex[i+5] == Constants.SIGL_DUMP_HDR_BYTES[5])) {

                        if (((sysex[i+6] == Constants.SIGL_DUMP_HDR_BYTES[6]) || (sysex[i+6] == Constants.EDIT_DUMP_HDR_BYTES[6]))
                            && (sysex[i + patchSize - 1] == (byte)0xF7)) {
                            System.arraycopy(sysex, i, l6Sysex, 0, (patchSize));
                            break;
                        }
                    }
                }
            }
        }

        if ((l6Sysex[0] == (byte)0xF0) && (l6Sysex[patchSize-1] == (byte)0xF7)) {
            return l6Sysex;
        } else {
            return sysex;
        }
    } 

    protected int trimSysex(Patch p) {
        p.sysex = parseSysex(p.sysex);
        String ptchStr = p.getPatchHeader().toString().substring(0, Constants.EDIT_BUFR_PATCH.length());
        if (ptchStr.equalsIgnoreCase(Constants.EDIT_BUFR_PATCH)) {    // Is this an edit buffer patch?
            p.sysex = convertToProgramPatch(p.sysex);
        }
        return p.sysex.length;
    }
    
    public byte[] convertToProgramPatch (byte[] sysex) {
        int newSysexLength = sysex.length + 1;
        byte newSysex[] = new byte[newSysexLength];
        System.arraycopy(Constants.SIGL_DUMP_HDR_BYTES, 0, newSysex, 0, Constants.PDMP_HDR_SIZE);
        System.arraycopy(sysex, Constants.EDMP_HDR_SIZE, newSysex, Constants.PDMP_HDR_SIZE, newSysexLength - Constants.PDMP_HDR_SIZE);
        return newSysex;
    }
    
    public void sendPatch (Patch p)
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
    
    // Sends a patch to a set location in the user bank
    public void storePatch(Patch p, int bankNum, int patchNum)
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
    
    // Pod does not "Play" patches, so the playPatch method is not used
    // We're using it here for test purposes only.
    public void playPatch(Patch p)
    {
        if (ErrorMsg.debug >= 2) {
            System.out.println(getPatchName(p));
            System.out.println("  Header -- ");
            System.out.println("  " + Utility.hexDump(p.sysex, 0, Constants.PDMP_HDR_SIZE, 16));
            System.out.println("  Data -- ");
            System.out.println("  " + Utility.hexDump(p.sysex, Constants.PDMP_HDR_SIZE, -1, 16));
        }
        
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame, Constants.PLAY_CMD_MSG);
    }
    
    public Patch createNewPatch()
    {
        Patch p = new Patch(Constants.NEW_SYSEX, this);
        setPatchName(p, "NewPatch        ");
        return p;
    }
    
    // Even though, from an operational standpoint, the POD has nine banks (numbered 1 through 9) of
    // four patches each (numbered A, B, C, and D), internally there is only a single bank of 36 patch
    // locations, referenced by program change numbers 0-35. By assigning the numbers 0 through 8 for
    // the banks and 0 through 3 for the patches, the conversion is as follows:
    //                      program number = (bank number * 4) + patch number 
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
    
    public JSLFrame editPatch(Patch p)
    {
        return new Line6BassPodSingleEditor((Patch)p);
    }
}

