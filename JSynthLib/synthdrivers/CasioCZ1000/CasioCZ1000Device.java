/*
 * Copyright 2004-5 Yves Lefebvre, Bill Zwicky
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

package synthdrivers.CasioCZ1000;

import java.util.prefs.Preferences;

import core.Device;


/**
 * Single patch driver for Casio CZ-101 and CZ-1000.
 * Note : on the casio, if you initiate a dump (from the PC or the Casio itself), 
 * you will get a patch of 263 bytes. you CAN'T send that patch back to the Casio... 
 * The patch must be 264 bytes long.  supportsPatch and createPatch fix up short
 * patches.
 * 
 * @author Brian Klock
 * @author Yves Lefebvre
 * @author Bill Zwicky
 * 
 * @version $Id$
 */
public class CasioCZ1000Device extends Device
{
    private static final String INFO_TEXT =
        "Casio CZ-101/1000." +
        "  CZ-101 is the base unit; the 1000 is the same, but with" +
        " larger keys.  Single driver works, bank driver not" +
        " tested, editor under construction.  Parts were developed" +
        " variously on 101 and 1000, but should work on both." +
        "  No work done at all for the CZ-5000, though it's" +
        " fundamentally the same." +
        "\n    Note that Casio doesn't support 'senders':  the sliders" +
        " in the editor don't update the synth live.  You'll need to" +
        " double-click the patch in the library to send the whole thing" +
        " over." +
        "\n    Note also that in the editor source code, you can enable" +
        " HACK_MODE to reveal full access to all sysex bits and bytes." +
        " There no good place for this switch in the GUI, right now.";

    public CasioCZ1000Device ()
    {
        super ("Casio", "CZ-101/1000",
                null, INFO_TEXT,
                "Yves Lefebvre, Bill Zwicky");
    }

    /** Constructor for for actual work. */
    public CasioCZ1000Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        addDriver(new CasioCZ1000SingleDriver());
        addDriver(new CasioCZ1000BankDriver());
    }
}
