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

package synthdrivers.Line6Pod20;

import core.*;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

// POD Generic Sender class for KnobWidgets
class CCSender extends SysexSender implements SysexWidget.ISender {
    private int param;
    private int multiplier;
    private boolean reverse = false;
    
    CCSender(int param) {
        this(param, 1, false);
    }
    
    CCSender(int param, boolean reverse) {
        this(param, 1, reverse);
    }
    
    CCSender(int param, int multiplier) {
        this(param, multiplier, false);
    }
    
    CCSender(int param, int multiplier, boolean reverse) {
        this.param = param;
        this.multiplier = multiplier;
        this.reverse = reverse;
    }
    
    public void send(IPatchDriver driver, int value) {
        if (reverse) {
            value = 127 - value;
        }
        value = Math.min(127, value * multiplier);
        ShortMessage m = new ShortMessage();
        try{
            m.setMessage(ShortMessage.CONTROL_CHANGE, ((Driver)driver).getChannel() - 1, param, value);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
}

