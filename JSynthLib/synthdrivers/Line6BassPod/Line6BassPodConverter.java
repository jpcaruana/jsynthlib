//
//  Line6BassPodConverter.java
//  JSynthLib
//
//  Created by Jeff Weber on 8/29/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//
package synthdrivers.Line6BassPod;

import core.*;


/**
* Removes "Garbage Data" from Line6 response to dump request and extracts desired patch
 *
 * @author Jeff Weber
 */
public class Line6BassPodConverter extends Converter{
    static final String bankList[] = new String[] {
        "1"
    };
    static final String patchList[] = new String[] {
        "Edit"
    };
    
    
    public Line6BassPodConverter() {
        super("Bass Pod All Dump Converter", "Jeff Weber");
        
//        this.sysexID = "F000010C0201****";
        this.sysexID = "F0**************";
        this.patchSize = 0;
        /* This doesn't seem to be used */
        //this.deviceIDoffset = 0;
        bankNumbers = bankList;
        patchNumbers = patchList;
    }
    /**
    * Extracts desired patch(program, edit buffer, or bank)
     */
    public Patch[] extractPatch(Patch p) {
    	byte[] sysex = p.getByteArray();
        Patch[] newPatchArray = new Patch[1];
//        byte [] temporarySysex = new byte[TG100Constants.PATCH_SIZE * TG100Constants.PATCH_NUMBER_LENGTH];
        
//        System.arraycopy(   sysex,
//                            TG100Constants.ALL_DUMP_OFFSET,
//                            temporarySysex,
//                            0,
//                            TG100Constants.PATCH_SIZE * TG100Constants.PATCH_NUMBER_LENGTH);
        
        
//        newPatchArray[0] = new Patch(temporarySysex, getDevice());
        newPatchArray[0] = p;
        return newPatchArray;
    }    
}
