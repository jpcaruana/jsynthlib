//
//  ScaledDblParamModel.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.*;

/** Scalable Double ParamModel--to allow different max values for CC and Sysex -- for two byte sysex value*/
class ScaledDblParamModel extends ParamModel {
    private int minCC = 0;
    private int maxCC;
    private int minSysex = 0;
    private int maxSysex;
    private boolean reverse = false;
    
    public ScaledDblParamModel(Patch p, int o) {
        this(p, o, 0, 1, 0, 1, false);
    }
    
    public ScaledDblParamModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, 0, maxCC, 0, maxSysex, false);
    }
    
    public ScaledDblParamModel(Patch p, int o, int minCC, int maxCC, int minSysex, int maxSysex, boolean reverse) {
        super(p, o);
        this.reverse = reverse;
        this.minCC = minCC;
        this.maxCC = maxCC;
        this.minSysex = minSysex;
        this.maxSysex = maxSysex;
    }
    
    public void set(int i) {
        if (reverse) {
            i = (maxCC - minCC) - (i - minCC) + minCC;
        }
        int sysexValue = ((i - minCC) * (maxSysex - minSysex) / (maxCC - minCC)) + minSysex;
        byte msB = (byte)(sysexValue / 256);
        byte lsB = (byte)(sysexValue % 256);
        patch.sysex[ofs]=msB;
        patch.sysex[ofs+1]=lsB;
    }
    
    public int get() {
        System.out.println(">>>>>>>>>>>>>>>>"); //Test
        System.out.println("Offset = " + ofs);  //Test
        
//        int msB = (int)(0xff & patch.sysex[ofs]);
        int msB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs);
//        int lsB = (int)(0xff & patch.sysex[ofs+1]);
        int lsB = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs+1);
        System.out.println("msB = " + msB);  //Test
        System.out.println("lsB = " + lsB);  //Test

        int returnValue = msB << 8;
        returnValue = returnValue | lsB;
        System.out.println("returnValue = " + returnValue);  //Test
        System.out.println("<<<<<<<<<<<<<<<<"); //Test
        returnValue = ((returnValue - minSysex) * (maxCC - minCC) / (maxSysex - minSysex)) + minCC;
        if (reverse) {
            returnValue = (maxCC - minCC) - (returnValue - minCC) + minCC;
        }
        return returnValue;
    }
}

