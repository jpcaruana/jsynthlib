//
//  CCSender.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//
package synthdrivers.Line6BassPod;

import core.*;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

// POD Generic Sender class for KnobWidgets
class CCSender extends SysexSender implements SysexWidget.ISender {
    private int param;
    private int multiplier;
    private boolean reverse = false;
    
    public CCSender(int param) {
        this(param, 1, false);
    }
    
    public CCSender(int param, boolean reverse) {
        this(param, 1, reverse);
    }
    
    public CCSender(int param, int multiplier) {
        this(param, multiplier, false);
    }
    
    public CCSender(int param, int multiplier, boolean reverse) {
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

