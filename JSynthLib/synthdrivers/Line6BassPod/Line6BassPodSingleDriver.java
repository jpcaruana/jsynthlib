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
    
    protected void calculateChecksum(Patch p)
    {
        // Pod doesn't use checksum
    }
    
    protected static void calculateChecksum(Patch p, int start, int end, int ofs)
    {
        // Pod doesn't use checksum
    }
    
    protected String getPatchName(Patch p) {
        char c[] = new char[patchNameSize];
        for (int i = 0; i < patchNameSize; i++) {
            c[i] = (char)PatchBytes.getSysexByte(p.sysex, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart);
        }
        return new String(c);
    }
    
    protected void setPatchName(Patch p, String name) {
        byte nameByte[] = name.getBytes();
        int i;
        for (i = 0; i < Math.min(name.length(), patchNameSize); i++) {
            PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart, nameByte[i]);
        }
        for (int j = i; j < patchNameSize; j++) {
            PatchBytes.setSysexByte(p, Constants.PDMP_HDR_SIZE, Constants.PDMP_HDR_SIZE + i + patchNameStart, (byte)0x20);
        }
    }
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
    
    // Sends a patch to a set location in the user bank
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
    
    // Pod does not "Play" patches, so the playPatch method is not used
    // We're using it here for test purposes only.
    protected void playPatch(Patch p)
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
    
    protected Patch createNewPatch()
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
    
    protected JSLFrame editPatch(Patch p)
    {
        return new Line6BassPodSingleEditor((Patch)p);
    }
}

