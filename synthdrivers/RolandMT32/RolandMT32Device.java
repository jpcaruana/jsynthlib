/*
 * Copyright 2004,2005 Fred Jan Kraan
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

/**
 * Device class for Roland MT32.
 * 
 * @version $Id$
 */

package synthdrivers.RolandMT32;
import core.Device;
import java.util.prefs.Preferences;

public class RolandMT32Device extends Device {
    private static final String INFO_TEXT
    = "The driver for this synthesiser is created with an absolute minimal knowledge " +
      "of the JSynthLib and MT-32 architecture. Basically for each MT32 memory region " +
      "a driver and an editor is build. If more than one data set is available in this " +
      "region, a bank driver is build too.\n" +
      "A separate document describes the different data sets and their relations. The " +
      "great number of different drivers and editors is a bit daunting, but remember that " +
      "the MT32 is a multi timbral instrument which can produce sounds on nine midi " +
      "channels simultaneously\n";
      

    /** Constructor for DeviceListWriter. */
    public RolandMT32Device() {
	super("Roland", "MT32", "F041**16",
	      INFO_TEXT, "Fred Jan Kraan");
    }

    /** Constructor for for actual work. */
    public RolandMT32Device(Preferences prefs) {
	this();
	this.prefs = prefs;

        addDriver(new RolandMT32PatchTempDriver());
        addDriver(new RolandMT32PatchTempBankDriver());
        addDriver(new RolandMT32PatchMemoryDriver());
        addDriver(new RolandMT32PatchMemoryBankDriver());
        addDriver(new RolandMT32RhythmSetupTempDriver());
        addDriver(new RolandMT32TimbreTempDriver());
        addDriver(new RolandMT32TimbreTempBankDriver());
        addDriver(new RolandMT32TimbreMemoryDriver());
        addDriver(new RolandMT32TimbreMemoryBankDriver());
        addDriver(new RolandMT32SystemDriver());
        addDriver(new RolandMT32DisplayDriver());
        
        setDeviceID(10);

    }
}
