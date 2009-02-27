/*
 * Copyright 2005 Ton Holsink
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

package org.jsynthlib.drivers.yamaha.ub99;
import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

public class YamahaUB99Device extends Device {
    private static final String INFO_TEXT="The librarian and editor are for the Yamaha UB99 Magicstomp Guitar Effects Processor version 1.08 firmware.\n\n"
                                        + "Have fun!\n"
                                        + "Ton Holsink\n"
                                        + "a.j.m.holsink@chello.nl";

    /** Constructor for DeviceListWriter. */
    public YamahaUB99Device() {
        super("Yamaha", "UB99 Magic Stomp", null, INFO_TEXT, "Ton Holsink");
    }

    /** Constructor for actual work. */
    public YamahaUB99Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        // add drivers
        YamahaUB99Driver singleDriver = new YamahaUB99Driver();
        addDriver(singleDriver);
        addDriver(new YamahaUB99Converter(singleDriver));
        addDriver(new YamahaUB99BankDumpConverter(singleDriver));
        addDriver(new YamahaUB99BankDriver(singleDriver));
    }

}
