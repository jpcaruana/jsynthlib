/*
 * Copyright 2004 Joachim Backhaus
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
package synthdrivers.QuasimidiQuasar;

/**
 * Sender for the Quasimidi Quasar synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */

import org.jsynthlib.core.SysexSender;

public class QuasarSender extends SysexSender {
    
    private static final int SYSEX_LENGTH = 10;    
    private byte[] sysex;

    // Only 0x10 works here
    protected byte channel = (byte) 0x10;

    /*
    * Only offsetAddress2 is needed here as offsetAddress1 is set by storePatch
    */
    public QuasarSender(int offsetAddress2, int paramOffset) {        
        
        sysex = new byte[SYSEX_LENGTH];        

        sysex[0]  = QuasarConstants.SYSEX_START_BYTE;
        sysex[1]  = (byte) 0x3F;
        sysex[2]  = channel; // Device number
        sysex[3]  = (byte) 0x20;
        sysex[4]  = (byte) 0x44;

        // Internal voice        
        sysex[5]  = (byte) QuasarConstants.SYSEX_PERFORMANCE_OFFSET; // <-- This is offsetAddress1 here for Performance No. 1
        sysex[6]  = (byte) offsetAddress2;
        sysex[7]  = (byte) paramOffset;

        // Data
        sysex[8]  = (byte) 0x00;
        
        sysex[9]  = QuasarConstants.SYSEX_END_BYTE;
    }

    public byte[] generate(int value) {        
        // Data
        sysex[7]  = (byte) value;        

        return sysex;
    }
}


