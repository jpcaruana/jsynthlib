//
//  DelayCoarseSpeedModel.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.*;

/** For the coarse speed, we're only interested in looking at bytes 27-28. So to get the coarse value we just
do a mod 3 (or mod 0x03) instead of mod 768 (or mod 0x0300) and subtract that result from the original value.*/
class DelayCoarseSpeedModel extends ParamModel {
    private int minCC = 0;
    private int maxCC;
    private int minSysex = 0;
    private int maxSysex;
    private boolean reverse = false;
    private int scaleFactor;
    
    public DelayCoarseSpeedModel(Patch p, int o) {
        this(p, o, 0, 1, 0, 1, false);
    }
    
    public DelayCoarseSpeedModel(Patch p, int o, int maxCC, int maxSysex) {
        this(p, o, 0, maxCC, 0, maxSysex, false);
    }
    
    public DelayCoarseSpeedModel(Patch p, int o, int minCC, int maxCC, int minSysex, int maxSysex, boolean reverse) {
        super(p, o);
        this.reverse = reverse;
        this.minCC = minCC;
        this.maxCC = maxCC;
        this.minSysex = minSysex;
        this.maxSysex = maxSysex;
        this.scaleFactor = maxSysex / maxCC;
    }
    
    public void set(int i) {
        if (reverse) {
            i = (maxCC - minCC) - (i - minCC) + minCC;
        }
        int sysexValue = ((i - minCC) * (maxSysex - minSysex) / (maxCC - minCC)) + minSysex;
        sysexValue += getFineValue();
        byte msB = (byte)(sysexValue / 256);
        byte lsB = (byte)(sysexValue % 256);
        patch.sysex[ofs]=msB;
        patch.sysex[ofs+1]=lsB;
        //  System.out.println("Sysex offset = " + (ofs - 9) + "  Setting Value = " + TestNibbilizer.dump(patch.sysex[ofs]) + " " + TestNibbilizer.dump(patch.sysex[ofs+1]) + " from " + i);    //Test
    }
    
    public int get() {
        int returnValue = getCoarseValue();
        returnValue = ((returnValue - minSysex) * (maxCC - minCC) / (maxSysex - minSysex)) + minCC;
        if (reverse) {
            returnValue = (maxCC - minCC) - (returnValue - minCC) + minCC;
        }
        //  System.out.println("Sysex offset = " + (ofs - 9) + "  Getting From Value = " + TestNibbilizer.dump(patch.sysex[ofs]) + " " + TestNibbilizer.dump(patch.sysex[ofs+1]) + " to " + returnValue);    //Test
        return returnValue;
    }
    
    private int getBytesValue() {
        int msB = (int)(0xff & patch.sysex[ofs]);
        int lsB = (int)(0xff & patch.sysex[ofs+1]);
        int bytesValue = msB << 8;
        bytesValue = bytesValue | lsB;
        //  System.out.println("Bytes Value = " + bytesValue);
        return bytesValue;
    }
    
    private int getCoarseValue() {
        int coarseValue = getBytesValue() - getFineValue();
        //  System.out.println("Coarse Value = " + coarseValue);
        return coarseValue;
    }
    
    private int getFineValue() {
        int fineValue = getBytesValue() % scaleFactor;
        //  System.out.println("Fine Value = " + fineValue);
        return fineValue;
    }
}