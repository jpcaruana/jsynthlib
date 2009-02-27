/*
 * Copyright 2001 Roger Westerlund
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

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;


/**
 * @author  Roger Westerlund <roger.westerlund@home.se>
 */
public class RolandD10Device extends Device {

    private static final String infoText =
        "This driver supports editing of Tones, Timbres and Patches for the D-10. " +
        "It currently only supports device id 17.";

    // Roland D-10 does not support the universal inquiry message.
    private static final String inquiryId = null;

    /** Creates new D10Device */
	public RolandD10Device() {
        super("Roland", "D-10", inquiryId, infoText, "Roger Westerlund");
	}

    public RolandD10Device(Preferences preferences) {
        this();
        this.prefs = preferences;

        RolandD10ToneDriver toneDriver = new RolandD10ToneDriver();
        addDriver(toneDriver);
        addDriver(new RolandD10ToneBankDriver(toneDriver));
        RolandD10TimbreDriver timbreDriver = new RolandD10TimbreDriver();
        addDriver(timbreDriver);
        addDriver(new RolandD10TimbreBankDriver(timbreDriver));
        RolandD10PatchDriver patchDriver = new RolandD10PatchDriver();
        addDriver(patchDriver);
        addDriver(new RolandD10PatchBankDriver(patchDriver));
//        RolandD10RythmSetupDriver rythmSetupDriver = new RolandD10RythmSetupDriver();
//        addDriver(rythmSetupDriver);
//        addDriver(new RolandD10RythmSetupBankDriver(rythmSetupDriver));
    }
}
