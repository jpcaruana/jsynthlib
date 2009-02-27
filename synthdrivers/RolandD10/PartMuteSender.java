/*
 * Copyright 2006 Roger Westerlund
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
package synthdrivers.RolandD10;

import core.IPatchDriver;
import core.SysexWidget.ISender;

/**
 * This class is used for sending a Tones partitial mute setting. Since there
 * are four checkboxes that operates on the same byte the senders for each
 * checkbox must share the same ParamModel. The actual sending is delegated to
 * general EditSender supplied to this class.
 * 
 * @author Roger Westerlund
 */
class PartMuteSender implements ISender {

    private ISender sender;
    private PartMuteDataModel model;

    /**
     * 
     */
    public PartMuteSender(PartMuteDataModel model, ISender sender) {
        super();
        this.sender = sender;
        this.model = model;
    }

    public void send(IPatchDriver driver, int value) {
        sender.send(driver, model.getData());
    }
}
