/*
 *  WaldorfPulseDevice.java
 *
 *  Copyright (c) Scott Shedden, 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jsynthlib.drivers.waldorf.pulse;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

public class WaldorfPulseDevice extends Device
{
    public WaldorfPulseDevice()
    {
        super("Waldorf",
              "Pulse/Pulse+", null,
              "This driver supports patch editting for the Waldorf Pulse "+
              "and Pulse+. Parameters supported by the Pulse+ "+
              "only are marked with a plus icon.\n"+
              "Stacked pulses are not supported at present.\n",
              "Scott Shedden");
    }

    /** Constructor for for actual work. */
    public WaldorfPulseDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("Pulse");
        addDriver(new WaldorfPulseSingleDriver());
    }
}
