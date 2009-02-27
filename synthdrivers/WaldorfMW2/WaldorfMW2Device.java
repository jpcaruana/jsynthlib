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

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;


/**
 * Device class for Microwave 2 / XT / XTK
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class WaldorfMW2Device extends Device {
    private static final String INFO_TEXT = "Microwave 2 / XT / XTK librarian.";
    
    /**
     * Constructor for DeviceListWriter.
     */
    public WaldorfMW2Device() {
        super(  "Waldorf",
                "Microwave 2/XT/XTK",
                "F07E06023E0E00............F7",
                INFO_TEXT,
                "Joachim Backhaus");
    }
    
    
    /**
     * Constructor for the actual work.
     *
     * @param prefs  The Preferences for this device
     */
    public WaldorfMW2Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        
        addDriver(new WaldorfMW2AllSoundConverter());
        addDriver(new WaldorfMW2SingleDriver());
        addDriver(new WaldorfMW2BankDriver());
    }
}
