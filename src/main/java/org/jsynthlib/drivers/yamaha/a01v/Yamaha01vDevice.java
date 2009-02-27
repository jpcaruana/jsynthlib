/*
 * Copyright 2006 Robert Wirski
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

package org.jsynthlib.drivers.yamaha.a01v;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

public class Yamaha01vDevice extends Device {
    private static final String INFO_TEXT = "Yamaha 01v librarian.";
    
    /**
     * Constructor for DeviceListWriter.
     */
    public Yamaha01vDevice() {
        super(  "Yamaha",
                "01v",
                "",
                INFO_TEXT,
                "Robert Wirski");
    }
    
    
    /**
     * Constructor for the actual work.
     *
     * @param prefs  The Preferences for this device
     */
    public Yamaha01vDevice(Preferences prefs) {
       this();
       this.prefs = prefs;
           addDriver(new Yamaha01vSceneDriver());
           addDriver(new Yamaha01vSetupDriver());
           addDriver(new Yamaha01vRemoteIntDriver());
           addDriver(new Yamaha01vRemoteMMCDriver());
           addDriver(new Yamaha01vRemoteUserDriver());
           addDriver(new Yamaha01vEqDriver());
           addDriver(new Yamaha01vDynamicsDriver());
           addDriver(new Yamaha01vEffectDriver());
           addDriver(new Yamaha01vPrChangeTabDriver());
           addDriver(new Yamaha01vCtrlChangeTabDriver());
    }
}
