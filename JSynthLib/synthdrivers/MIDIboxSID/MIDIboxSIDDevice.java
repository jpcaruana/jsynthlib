/*
 * JSynthlib-Device for MIDIbox SID
 * =====================================================================
 * @author  Thorsten Klose
 * file:    MIDIboxSIDDevice.java
 * date:    2002-11-30
 * @version $Id$
 *
 * Copyright (C) 2002  Thorsten.Klose@gmx.de
 *                     http://www.uCApps.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package synthdrivers.MIDIboxSID;
import java.util.prefs.Preferences;

import core.Device;

public class MIDIboxSIDDevice extends Device
{
    private static final String infoText="This driver has been created for MIDIbox SID, a non-commercial DIY "+
        "synthesizer based on the famous Commodore SID soundchip."+
        "\n"+
        "More informations about the features can be found under http://www.uCApps.de/midibox_sid.html";


    /** Creates new MIDIboxSIDDevice */
    public MIDIboxSIDDevice ()
    {
	super ("MIDIbox","SID","F000007E46000FF7",infoText,"Thorsten Klose");
    }

    /** Constructor for for actual work. */
    public MIDIboxSIDDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

        //setSynthName("MIDIbox SID");

        addDriver(new MIDIboxSIDSingleDriver());
        addDriver(new MIDIboxSIDBankDriver());
    }

}

