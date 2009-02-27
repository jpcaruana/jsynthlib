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

package org.jsynthlib.drivers.yamaha.a01v;

import org.jsynthlib.core.Driver;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;


public class Yamaha01vRemoteIntDriver extends Driver {
    
    private static final SysexHandler SYS_REQ = new SysexHandler( "F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 49 *patchNum* F7" );

    public Yamaha01vRemoteIntDriver() {
        super("Remote(Internal Par.)", "Robert Wirski");
        
        sysexID = "F0430*7E00644C4D20203842333449";
        
        
        patchSize = 108;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 2;
        
        checksumStart = 6;
        checksumOffset = 106;
        checksumEnd = 105;       

        bankNumbers = new String[] { "" };
        patchNumbers = new String[] { "BANK 1", "BANK 2", "BANK 3", "BANK 4"};
        
    }
       
    /**
     * Sends a patch to a set location on a synth.<p>
     * 
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setPatchNum(patchNum);
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
            java.io.InputStream fileIn = getClass().getResourceAsStream("01v_RemoteInt.syx");
            fileIn.read(sysex);
            fileIn.close();
            
        } catch (Exception e)
            { System.err.println("Unable to find 01v_RemoteInt.syx."); };
        
        p = new Patch(sysex, this);
        return p;
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
                                    new SysexHandler.NameValue("ID", getDeviceID() + 0x1F),
                                    new SysexHandler.NameValue("patchNum", patchNum)));
    }
}

