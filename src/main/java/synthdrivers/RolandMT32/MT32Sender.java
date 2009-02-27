/*
 * Copyright 2004,2005 Fred Jan Kraan
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

/**
 * MT32 Sender for Roland MT32.
 *
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import org.jsynthlib.core.SysexSender;

class MT32Sender extends SysexSender {
  /* message: Data set DT1 12h
   *
   *  00-04 F0 41 10 16 12  // start of sysex + header 
   *                           (manufactID, devID, modelID, cmdID)
   *  05-07 aa aa aa        // address
   *  08-(End-2) dd         // data
   *  (End-1)    cc         // checksum
   *  (End)    F7           // end of sysex
   */
    private int parameter;
    private int baseaddr;
    private byte[] b = new byte[11];

    public MT32Sender(int param, int basad) {
	parameter = param;  // The parameter address (split in two bytes)
        baseaddr = basad;   // The parameter group address 
        b[0] = (byte)0xF0;  
        b[1] = (byte)0x41;  
        b[2] = (byte)0x10;  
        b[3] = (byte)0x16;  
        b[4] = (byte)0x12; 
        b[5] = (byte)(baseaddr & 0x7F);  
        b[6] = (byte)(parameter/128);
        b[7] = (byte)(parameter&127);
        // b[8] data
        // b[9] checksum
        b[10]= (byte)0xF7;
    }


    public byte[] generate (int value) {
	    b[8] = (byte) (value & 127);      
        b[9] = (byte)((0 - (b[5] + b[6] + b[7] + b[8])) & 0x7F);
	return b;
    }
}
