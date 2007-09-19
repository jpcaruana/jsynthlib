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

package synthdrivers.NovationSBS;

import java.util.prefs.Preferences;
import core.Device;

/**
 * Device class for Novation Super Bass Station.
 * @author Robert Wirski
 */
public class NovationSBSDevice extends Device {
    private static final String INFO_TEXT =
        "Novation Super Bass Station Librarian";

    /**
     * Constructor for DeviceListWriter.
     */
    public NovationSBSDevice() {
        super(  "Novation",
                "Super Bass Station",
                "",
                INFO_TEXT,
                "Robert Wirski");
    }
    
    
    /**
     * Constructor for the actual work.
     *
     * @param prefs  The Preferences for this device
     */
    public NovationSBSDevice(Preferences prefs) {
       this();
       this.prefs = prefs;
           addDriver(new NovationSBSProgramDriver());
    }
}
