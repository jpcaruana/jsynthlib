//
//  Line6Pod20EdBufDriver.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.*;
import javax.swing.*;

// Note that on the Pod, the edit buffer patch has an 8 byte header and the program patch has a 9 byte header. 
// The only reason for having this driver is to be able to request an edit buffer patch. As soon as the edit buffer
// patch is received, it is converted to a regular program patch by Line6Pod20SingleDriver.supportsPatch. From that
// point on it is handled like any other program patch. Note that we have declared Constants.EDMP_HDR_SIZE and sysexID to the values for 
// a program patch, not an edit buffer patch. By the time these values are referenced, the patch has already been converted.

public class Line6Pod20EdBufDriver extends Line6Pod20SingleDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(Constants.EDIT_DUMP_REQ_ID); //Edit Buffer Dump Request
    
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

    // Even though, from an operational standpoint, the POD has nine banks (numbered 1 through 9) of
    // four patches each (numbered A, B, C, and D), internally there is only a single bank of 36 patch
    // locations, referenced by program change numbers 0-35. By assigning the numbers 0 through 8 for
    // the banks and 0 through 3 for the patches, the conversion is as follows:
    //                      program number = (bank number * 4) + patch number 
    public void requestPatchDump(int bankNum, int patchNum) {
        int progNum = 0;
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new NameValue("progNum", progNum)));
    }
    
    public JSLFrame editPatch(Patch p)
    {
        return new Line6Pod20SingleEditor((Patch)p);
    }
}
