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

class TrigSender implements SysexWidget.ISender {
    final static int TR_V_CURVE        = 1;
    final static int TR_NOTE_NBR       = 2;
    final static int TR_GAIN           = 3;
    final static int TR_X_TALK         = 4;
    final static int TR_DECAY          = 5;
    final static int TR_NOISE_FLR      = 6;
    
    private final static int NRPN_MSB          = 99;
    private final static int NRPN_LSB          = 98;
    private final static int DATA_ENTRY_MSB    = 6;
    private final static int SELECT_ACTIVE_TRG = 0;
    
    private final static int MAX_TRIG_NUM = 11;
    private static int LAST_TRIG_NUM = 99;
    private int param;
    private int trigNum;
    private int max;
    
    public TrigSender(int trigNum, int param, int max) {
        this.trigNum = trigNum;
        this.param = param;
        this.max = max;
    }
    
    public void send(IPatchDriver driver, int value) {
        if (trigNum != LAST_TRIG_NUM) {
            try {
                driver.send(newControlChange(driver, NRPN_MSB, 0));  // Send NRPN MSB
                driver.send(newControlChange(driver, NRPN_LSB, SELECT_ACTIVE_TRG));  // Command to select active trigger number (NRPN LSB)
                driver.send(newControlChange(driver, 6, trigNum * 127 / MAX_TRIG_NUM));  // Set the Trigger Number
            } catch  (InvalidMidiDataException e) {
                ErrorMsg.reportStatus(e);
            }
            LAST_TRIG_NUM = trigNum;            
            
            try {
                Thread.sleep (50);
            } catch (Exception e) {}
        }
        
        try {
            driver.send(newControlChange(driver, NRPN_LSB, param));  // Command to select the parameter
            driver.send(newControlChange(driver, DATA_ENTRY_MSB, value * 127 / max));  // Set the value of the parameter
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
    
    private ShortMessage newControlChange(IPatchDriver driver, int controlNumber, int value) throws InvalidMidiDataException {
        ShortMessage ccMessage = new ShortMessage();
        ccMessage.setMessage(ShortMessage.CONTROL_CHANGE, driver.getDevice().getChannel() - 1, controlNumber, value);
        return ccMessage;
    }
}
