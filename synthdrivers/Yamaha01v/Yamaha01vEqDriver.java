/*
 * Copyright 2006 Robert Wirski
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.Yamaha01v;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.DriverUtil;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;
//import core.JSLFrame;


public class Yamaha01vEqDriver extends Driver {
    
    private static final SysexHandler SYS_REQ = new SysexHandler( "F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 51 *patchNum* F7" );

    public Yamaha01vEqDriver() {
        super("Equalizer Library", "Robert Wirski");
        
        sysexID = "F0430*7E002E4C4D20203842333451";
        
        
        patchSize = 54;
        patchNameStart = 16;
        patchNameSize = 12;
        deviceIDoffset = 2;
        
        checksumStart = 6;
        checksumOffset = 52;
        checksumEnd = 51;       

        bankNumbers = new String[] { "" };

        patchNumbers = new String[108];
        System.arraycopy(DriverUtil.generateNumbers(1, 80, "Lib 00"), 0, patchNumbers, 0, 80);
        System.arraycopy(DriverUtil.generateNumbers(1,12,"Ch 0"), 0, patchNumbers, 80, 12);
        patchNumbers[92]= new String("Ch 13/14");
        patchNumbers[93]= new String("Ch 15/16");
        patchNumbers[94]= new String("RTN 1/2");
        System.arraycopy(DriverUtil.generateNumbers(1,4,"AUX 0"), 0, patchNumbers, 95, 4);
        patchNumbers[99]= new String("ST MAS");
        System.arraycopy(DriverUtil.generateNumbers(17,24,"Ch 0"), 0, patchNumbers, 100, 8);
    }

    /**
     * Correct the patchNumber value due to the fact, that
     * there is a hole in the patch numbers in Equalizer
     * Library dump request.<p>
     */
    protected int correctPatchNumber(int patchNumber) {
        if (patchNumber>79) patchNumber += 16;
        return patchNumber;
        }
    
    /**
     * Sends a patch to a set location on a synth.<p>
     * 
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        patchNum = correctPatchNumber(patchNum);
        setPatchNum(patchNum);
        setBankNum(0);
        p.sysex[15] = (byte) patchNum; // Location
        calculateChecksum(p);
        
        sendPatchWorker(p);
    }    
   
    /**
     * @see org.jsynthlib.core.Driver#createNewPatch()
     */
    public Patch createNewPatch() {
        byte[] sysex  = new byte[patchSize];
        Patch p;
        
        try {
            java.io.InputStream fileIn = getClass().getResourceAsStream("01v_Equalizer.syx");
            fileIn.read(sysex);
            fileIn.close();
            
        } catch (Exception e)
            { System.err.println("Unable to find 01v_Equalizer.syx."); };
        
        p = new Patch(sysex, this);
        return p;
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("ID", getDeviceID() + 0x1F),
                                    new SysexHandler.NameValue("patchNum", correctPatchNumber(patchNum))));
    }
}

