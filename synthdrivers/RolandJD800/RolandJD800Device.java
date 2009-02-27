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

package synthdrivers.RolandJD800;

import java.util.prefs.Preferences;
import core.Device;

/**
 * Device class for Roland JD800.
 * @author Robert Wirski
 */
public class RolandJD800Device extends Device {
    private static final String INFO_TEXT =
        "This driver supports librarian functions for a single patch, system area, "+
        "and a bank operation. It does not support the Multi Mode as in author's "+
        "opinion the unit does not perform well when compared to the Single Mode. "+
        "If you need the Multi Mode contact me, please so we could discuss "+
        "details. Write to wirski@op.pl. Do not forget to set correct Device ID (17 to 32).";

    /**
     * Constructor for DeviceListWriter.
     */
    public RolandJD800Device() {
        super(  "Roland",
                "JD800",
                "",
                INFO_TEXT,
                "Robert Wirski");
    }
    
    
    /**
     * Constructor for the actual work.
     *
     * @param prefs  The Preferences for this device
     */
    public RolandJD800Device(Preferences prefs) {
       this();
       this.prefs = prefs;
           addDriver(new RolandJD800SystemArea());
           addDriver(new RolandJD800SinglePatchDriver());
           addDriver(new RolandJD800BankDriver());
    }
}
