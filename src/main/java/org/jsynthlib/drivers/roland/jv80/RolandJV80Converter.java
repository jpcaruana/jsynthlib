/*
 * Copyright 2004 Sander Brandenburg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
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
package org.jsynthlib.drivers.roland.jv80;

import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;


/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80Converter extends Converter {

    final RolandJV80BankDriver bankDriver;
    final RolandJV80PatchDriver singleDriver;

	public RolandJV80Converter(RolandJV80BankDriver bankDriver) {
		super("Converter","Sander Brandenburg");
		
		this.bankDriver = bankDriver;
		sysexID         = JV80Constants.SYSEXID;
		singleDriver    = bankDriver.getPatchDriver();
	}
    
	//returns offset in the BankDriver patch regarding the 
	//memory address in the sysex
	// -1 if patch is rejected.
	int acceptAddress(byte addr1, byte addr2, byte addr3, byte addr4) {
	    if (addr1 == 0x01 && addr4 == 0x00) {
	        if (addr2 >= 0x40 && addr2 <= 0x7F) {
	            int patchNum = addr2 - 0x40;
	            if (addr3 == 0x20) {
	                return singleDriver.getPatchSize() * patchNum;
	            } else if (addr3 >= 0x28 && addr3 <= 0x2b) {
	                int toneNum = addr3 - 0x28;
	                return singleDriver.getPatchSize() * patchNum + 
	                	singleDriver.patchCommonLength +
	                	singleDriver.patchToneOffsets[toneNum]; 
	            }
	        }
	    }
	    return -1;
	}
	
	public boolean supportsPatch(String header, byte[] sysex) {
	    if (sysex.length < 16)
	        return false;
	    
	    return sysex[0] == (byte) 0xF0 && sysex[1] == 0x41 && 
    		sysex[3] == 0x46 && sysex[4] == 0x12;
	}
	
	boolean supportsData(byte[] sysex) {
	    if (sysex.length < 16)
	        return false;
	    
	    return sysex[0] == 0x41 && sysex[2] == 0x46 && sysex[3] == 0x12;
	}
   
	public Patch[] extractPatch(Patch p) {
	    Patch bpatch = bankDriver.createNewPatch();
	    
	    SysexMessage[] msgs = p.getMessages();
	    
	    for (int i = 0; i < msgs.length; i++) {
	        SysexMessage sm = msgs[i];
	        byte dump[] = sm.getData(); // <-- MISSING THE FIRST F0 AND ENDING F7!! 
	        
	        if (supportsData(dump)) {
	            int addr = acceptAddress(dump[4], dump[5], dump[6], dump[7]);
	            if (addr != -1) {
	                // addr + 1 to compensate the missing F0 in dump
	                // dump.length - 1 to not write the checksum byte at the end
	                // this way a JV80 patch tone will not overwrite 
	                // a byte of a JV880 patch tone in a JV880 bank.
	                // A JV880 patch tone in a JV80 bank will overwrite the checksum
	                // byte, which is revalidated anyway.
	                System.arraycopy(dump, 0, bpatch.sysex, addr + 1, dump.length - 1);
	            }
	        }
	    }
	    
	    return new Patch[]{bpatch};
	}
}
