/*
 * Copyright 2004 Jeff Weber
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

package synthdrivers.AlesisDM5;

import core.*;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

class NRPNSender implements SysexWidget.ISender {
    
    final static int NOTE_BANK         = 0x08;
    final static int NOTE_SOUND        = 0x09;
    final static int NOTE_COARSE_TUNE  = 0x0A;
    final static int NOTE_FINE_TUNE    = 0x0B;
    final static int NOTE_VOLUME       = 0x0C;
    final static int NOTE_PAN          = 0x0D;
    final static int NOTE_OUTPUT       = 0x0E;
    final static int NOTE_GROUP        = 0x0F;
    final static int SET_ROOT_NOTE     = 0x10;
    final static int PREVIEW_NOTE      = 0x19;
    final static int FW_CLOSE_NOTE     = 0x1C;
    final static int FW_HELD_NOTE      = 0x1D;
    
    private final static int NRPN_MSB          = 99;
    private final static int NRPN_LSB          = 98;
    private final static int DATA_ENTRY_MSB    = 6;
    
    protected int param;
    protected int max;
    protected int ccMap[];
    
    public NRPNSender(int param, int[] conValues) {
        this.param = param;
        this.ccMap = conValues;
    }
    
    public NRPNSender(int param, int max) {
        this.param = param;
        this.max = max;
        this.ccMap = getCCMap(max);
    }
    
    public void setMax(int max) {
        this.max = max;
        this.ccMap = getCCMap(max);
    }
    
    private int[] getCCMap(int max) {
        int[] convTable = new int[max + 1];
        for (int i = 127; i >= 0; i--) {
            int cc = (i * max) / 127;
            convTable[cc] = i;
        }
        return convTable;
    }
    
    public void send(IPatchDriver driver, int value) {
        try {
            driver.send(newControlChange(driver, NRPN_MSB, 0));  // Send NRPN MSB
            driver.send(newControlChange(driver, NRPN_LSB, param));  // Command to select the parameter
            driver.send(newControlChange(driver, DATA_ENTRY_MSB, ccMap[value]));  // Set the NRPN value using the table
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
        
        try {
            Thread.sleep (50);
        } catch (Exception e) {}
    }
    
    protected ShortMessage newControlChange(IPatchDriver driver, int controlNumber, int value) throws InvalidMidiDataException {
        ShortMessage ccMessage = new ShortMessage();
        ccMessage.setMessage(ShortMessage.CONTROL_CHANGE, driver.getDevice().getChannel() - 1, controlNumber, value);
        return ccMessage;
    }
}

