//
//  Line6Pod20BankDriver.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;
import core.*;
import java.io.*;
import javax.swing.*;

public class Line6Pod20BankDriver extends BankDriver
{
    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.BANK_DUMP_REQ_ID); //Program Bank Dump Request
    
    public Line6Pod20BankDriver()
    {
        super(Constants.BANK_PATCH_TYP_STR, Constants.AUTHOR, Constants.PATCHES_PER_BANK, 1);        
        sysexID = Constants.BANK_SYSEX_MATCH_ID;
        
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        
        bankNumbers  = Constants.BANK_BANK_LIST;
        patchNumbers = Constants.BANK_PATCH_LIST;  
        
        singleSysexID = Constants.SIGL_SYSEX_MATCH_ID;
        singleSize = Constants.SIGL_SIZE + Constants.PDMP_HDR_SIZE + 1;
        patchSize=Constants.PATCHES_PER_BANK * Constants.SIGL_SIZE + Constants.BDMP_HDR_SIZE + 1;
        patchNameSize = Constants.PATCH_NAME_SIZE;
    }
    
    /** Returns the offset of the start of the patch in nibblized (non-native) bytes.*/
    public int getPatchStart(int patchNum)
    {
        int start=(Constants.SIGL_SIZE / 2 * patchNum);
        start+=Constants.BDMP_HDR_SIZE;  //sysex header
        return start;
    }
    
    public String getPatchName(Patch p,int patchNum) {
        int nameStart=getPatchStart(patchNum);
        nameStart+=Constants.PATCH_NAME_START; //offset of name in patch data
        char c[] = new char[patchNameSize];
        for (int i = 0; i < patchNameSize; i++) {
            c[i] = (char)PatchBytes.getSysexByte(p.sysex, Constants.BDMP_HDR_SIZE, i + nameStart);
        }
        
        return new String(c);
    }
    
    public void setPatchName(Patch p,int patchNum, String name)
    {
        patchNameStart=getPatchStart(patchNum);
        patchNameStart+=Constants.PATCH_NAME_START; //offset of name in patch data
        if (name.length()<patchNameSize) name=name+"                ";
        byte [] namebytes = new byte [64];
        try {
            namebytes=name.getBytes("US-ASCII");
            for (int i=0;i<patchNameSize;i++) {
                PatchBytes.setSysexByte(p, Constants.BDMP_HDR_SIZE, i + patchNameStart, namebytes[i]);
            }
        } catch (UnsupportedEncodingException ex) {return;}
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
            if (sysex[i] == Constants.BANK_DUMP_HDR_BYTES[0]) {
                if ((i + patchSize - 1) < sysex.length) {
                    if ((sysex[i+1] == Constants.BANK_DUMP_HDR_BYTES[1])
                        && (sysex[i+2] == Constants.BANK_DUMP_HDR_BYTES[2])
                        && (sysex[i+3] == Constants.BANK_DUMP_HDR_BYTES[3])
                        && (sysex[i+4] == Constants.BANK_DUMP_HDR_BYTES[4])
                        && (sysex[i+5] == Constants.BANK_DUMP_HDR_BYTES[5])) {
                        
                        if ((sysex[i+6] == Constants.BANK_DUMP_HDR_BYTES[6])
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
        return p.sysex.length;
    }
    
    public void putPatch(Patch bank, Patch p, int patchNum)  // Tested??  // Retest with new version of Core.*
    { 
        if (!canHoldPatch(p)) {
            JOptionPane.showMessageDialog
            (null,
             "This type of patch does not fit in to this type of bank.",
             "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.arraycopy(p.sysex, Constants.PDMP_HDR_SIZE, bank.sysex, getSysexStart(patchNum),Constants.SIGL_SIZE);
    }
    
    public Patch getPatch(Patch bank, int patchNum)
    {
        byte [] sysex=new byte[Constants.SIGL_SIZE + Constants.PDMP_HDR_SIZE + 1];
        System.arraycopy(Constants.SIGL_DUMP_HDR_BYTES, 0, sysex, 0, Constants.PDMP_HDR_SIZE);
        sysex[7]=(byte)patchNum;  
        sysex[Constants.SIGL_SIZE + Constants.BDMP_HDR_SIZE+1]=(byte)0xF7;    
        System.arraycopy(bank.sysex, getSysexStart(patchNum), sysex, Constants.PDMP_HDR_SIZE, Constants.SIGL_SIZE);
        try{
            Patch p = new Patch(sysex, getDevice());
            return p;
        }catch (Exception e) {
            ErrorMsg.reportError("Error","Error in Bass Pod Bank Driver",e);
            return null;
        }
    }
    
    /** Returns the offset of the start of the patch in nibblized (native) bytes.*/
    public int getSysexStart(int patchNum)
    {
        int start=(Constants.SIGL_SIZE * patchNum);
        start+=Constants.BDMP_HDR_SIZE;  //sysex header
        return start;
    }
        
    /** Creates a new bank patch..*/
    public Patch createNewPatch()
    {
        byte [] sysex = new byte[Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK) + 1];
        System.arraycopy(Constants.BANK_DUMP_HDR_BYTES, 0, sysex, 0, Constants.BDMP_HDR_SIZE);
        sysex[Constants.BDMP_HDR_SIZE + (Constants.SIGL_SIZE * Constants.PATCHES_PER_BANK)]=(byte)0xF7;
        Patch p = new Patch(sysex);
        boolean isValidDriver = p.chooseDriver();
        for (int i=0;i<Constants.PATCHES_PER_BANK;i++) {
            System.arraycopy(Constants.NEW_SYSEX, Constants.PDMP_HDR_SIZE, p.sysex, getSysexStart(i), Constants.SIGL_SIZE);
            setPatchName(p,i,"New Patch");
        }
        return p;
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new NameValue("bankNum", bankNum << 1)));
    }
    
    // storePatch needs to be rewritten to send individual program patches instead of the whole bank at once
    // because the Pod can't keep up with the data transmission rate. This will be done much like it is when
    // transferring a scene, where it calls Line6Pod20SingleDriver.storePatch (IPatch ip, int bankNum,int patchNum)
    // repeatedly.
    public void storePatch (Patch p, int bankNum,int patchNum)
    {
        Patch[] thisPatch = new Patch[Constants.PATCHES_PER_BANK];
        for (int progNbr=0; progNbr<Constants.PATCHES_PER_BANK; progNbr++) {
            thisPatch[progNbr] = getPatch(p, progNbr);
        }
        for (int progNbr=0; progNbr<Constants.PATCHES_PER_BANK; progNbr++) {
            int bankNbr = progNbr / 4;
            int ptchNbr = progNbr % 4;
            ((Line6Pod20SingleDriver)thisPatch[progNbr].getDriver()).storePatch(thisPatch[progNbr], bankNbr, ptchNbr);
        }
    }
}