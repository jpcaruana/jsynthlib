//
//  ScaledParamModel.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.*;

/** Scalable ParamModel--to allow different max values for CC and Sysex*/
class ScaledParamModel extends ParamModel {
    private int maxCC;
    private int maxSysex;
    private boolean reverse = false;
    
    ScaledParamModel(Patch p, int o) {
        this(p, o, 1, 1);
    }
    
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, maxCC, maxSysex, false);
    }
    
    ScaledParamModel(Patch p, int o, int maxCC, int maxSysex, boolean reverse) {
        super(p, o);
        this.maxCC = maxCC;
        this.maxSysex = maxSysex;
        this.reverse = reverse;
    }
    
    public void set(int i) {
        if (reverse) {
            i = (maxSysex - i);
        }
        PatchBytes.setSysexByte(patch, 9, ofs, (byte)(i * maxSysex / maxCC));
    }
    
    public int get() {
        int returnValue = (int)PatchBytes.getSysexByte(patch.sysex, 9, ofs) * maxCC / maxSysex;
        return returnValue;
    }
}
