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

package synthdrivers.TCElectronicGMajor;
import core.Device;
import java.util.prefs.Preferences;

public class TCElectronicGMajorDevice extends Device {
    private static final String INFO_TEXT="The librarian and editor are for the TC Electronic G-Major version 1.27 firmware.\n"
                                        + "At the moment there is no fader support.\n"
                                        + "It is fully functional, except for the bank driver. I have trouble getting the timing right.\n"
                                        + "Not all patches in the G-Major are overwritten, when you do a bank dump.\n\n"
                                        + "Have fun!\n"
                                        + "Ton Holsink\n"
                                        + "a.j.m.holsink@chello.nl";

    /** Constructor for DeviceListWriter. */
    public TCElectronicGMajorDevice() {
        super("TC Electronic", "G-Major", "F07E00060200201F480000000000010BF7", INFO_TEXT, "Ton Holsink");
    }

    /** Constructor for for actual work. */
    public TCElectronicGMajorDevice(Preferences prefs) {
        this();
        this.prefs = prefs;
        // add drivers
        TCElectronicGMajorSingleDriver singleDriver = new TCElectronicGMajorSingleDriver();
        addDriver(singleDriver);
        addDriver(new TCElectronicGMajorBankDriver(singleDriver));
    }

}
