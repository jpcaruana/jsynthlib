/*
 * Copyright 2005 Joachim Backhaus
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

package synthdrivers.WaldorfMW2;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import core.Driver;
import core.DriverUtil;
import core.ErrorMsg;
import core.ISinglePatch;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

/*
 * 
 * TODO 
 * 
 * - Request single programs doesn't work anymore!
 */
/**
 * Driver for Microwave 2 / XT / XTK single programs
 *
 * @author Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2SingleDriver extends Driver {
    
    public WaldorfMW2SingleDriver() {
        super("Single program", "Joachim Backhaus");
        
        this.sysexID = MW2Constants.SYSEX_ID + "10****";
        
        this.sysexRequestDump = new SysexHandler( "F0 3E 0E @@ 00 *BB* *NN* *XSUM* F7" );
        
        this.patchNameStart = MW2Constants.PATCH_NAME_START;
        this.patchNameSize = MW2Constants.PATCH_NAME_SIZE;
        this.deviceIDoffset = MW2Constants.DEVICE_ID_OFFSET;
        
        // The SysEx documentation said 5 but that's wrong!
        this.checksumStart = MW2Constants.SYSEX_HEADER_OFFSET;
        this.checksumOffset = this.checksumStart + MW2Constants.PURE_PATCH_SIZE;
        this.checksumEnd = this.checksumOffset - 1;        
        
        this.bankNumbers = new String[] { "A", "B" };
        
        this.patchNumbers = DriverUtil.generateNumbers(1, 128, "#");
        
        // Patch size (265 Bytes)
        this.patchSize = MW2Constants.PATCH_SIZE;
    }
    
    protected static void calculateChecksum(Patch p, int start, int end, int ofs)  {        
        int sum = 0;        
        for (int i = start; i <= end; i++)
            sum += p.sysex[i];        
        p.sysex[ofs] =  (byte) (sum & 0x7F); 
    }
    
    /**
     * Calculate check sum of a <code>Patch</code>.<p>
     *
     * @param p a <code>Patch</code> value
     */
    protected void calculateChecksum(Patch p) {        
        calculateChecksum(p, this.checksumStart, this.checksumEnd, this.checksumOffset);        
    }
    
    /**
     * Send Control Change (Bank Select) MIDI message.
     * @see #storePatch(Patch, int, int)
     */
    protected void setBankNum(int bankNum) {
        try {
            ShortMessage msg = new ShortMessage();            
            msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1,
                    0x20, //  Bank Select (LSB)
                    bankNum); // Bank Number (MSB)
            send(msg);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
    
    /**
     * Sends a patch to a set location on a synth.<p>
     * 
     * @see Patch#send(int, int)
     */
    protected void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        
        p.sysex[5] = (byte) bankNum; // Location
        p.sysex[6] = (byte) patchNum; // Location
        calculateChecksum(p);
        
        sendPatchWorker(p);
    }    
    
    /**
     * Sends a patch to the synth's edit buffer.<p>
     *     
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    protected void sendPatch(Patch p) {
        p.sysex[5] = (byte) 0x20; // Location (use Edit Buffer)
        p.sysex[6] = (byte) 0x00; // Location (use Edit Buffer)
        calculateChecksum(p);
        
        sendPatchWorker(p);
    }   
    
    protected static void createPatchHeader(Patch tempPatch, int bankNo, int patchNo) {
        if (tempPatch.sysex.length > 8) {
            tempPatch.sysex[0] = MW2Constants.SYSEX_START_BYTE;
            tempPatch.sysex[1] = (byte) 0x3E; // Waldorf Electronics GmbH ID
            tempPatch.sysex[2] = (byte) 0x0E; // Microwave 2 ID
            tempPatch.sysex[3] = (byte) tempPatch.getDevice().getDeviceID(); // Device ID
            tempPatch.sysex[4] = (byte) 0x10; // Sound Dump
            tempPatch.sysex[5] = (byte) bankNo; // Location
            tempPatch.sysex[6] = (byte) patchNo; // Location
            tempPatch.sysex[7] = (byte) 0x01; // Sound format (has to be 1, as 0 doesn't work!)
        }
    }
    
    protected void createPatchHeader(Patch tempPatch) {
        // Location (use Edit Buffer)
        createPatchHeader(tempPatch, 0x20, 0x00);        
    }

    /**
     * @see core.Driver#createNewPatch()
     */
    public Patch createNewPatch() {
        byte[] sysex  = new byte[MW2Constants.PATCH_SIZE];
        Patch p;
        
        try {
            java.io.InputStream fileIn = getClass().getResourceAsStream(MW2Constants.DEFAULT_SYSEX_FILENAME);
            fileIn.read(sysex);
            fileIn.close();
            p = new Patch(sysex, this);
            
        } catch (Exception e) {
            System.err.println("Unable to find " + MW2Constants.DEFAULT_SYSEX_FILENAME + " using hardcoded default.");
                       
            p = new Patch(sysex, this);
            createPatchHeader(p);
            //createPatchFooter(p);
            //p.sysex[263] = (byte) 0x00; // Checksum
            p.sysex[264] = MW2Constants.SYSEX_END_BYTE;
            setPatchName(p, "New program");
            calculateChecksum(p);
        }
        
        return p;
    }
    
    /**
     * Request the dump of a single program
     *
     * @param bankNum    The bank number (0 = A, 1 = B)
     * @param patchNum   The number of the requested single program
     */
    public void requestPatchDump(int bankNum, int patchNum) {
        
        if (sysexRequestDump == null) {
            JOptionPane.showMessageDialog
            (PatchEdit.getInstance(),
                    "The " + toString()
                    + " driver does not support patch getting.\n\n"
                    + "Please start the patch dump manually...",
                    "Get Patch", JOptionPane.WARNING_MESSAGE);
        }
        else {
            SysexHandler.NameValue[] nameValues = {
                    new SysexHandler.NameValue("BB", bankNum ),
                    new SysexHandler.NameValue("NN", patchNum ),
                    new SysexHandler.NameValue("XSUM", ((byte)(bankNum + patchNum)) & 0x7F )
            };
            
            send(sysexRequestDump.toSysexMessage(getDeviceID(), nameValues) );
        }
    }
}

