/*
 * Copyright 2004 Peter Hageus
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

package synthdrivers.AlesisDMPro;

import core.*;
import java.util.prefs.Preferences;
/**
 * Created on 25. December2001, 14:00
 * @author  Peter Hageus
 * @version 1.0
 */
public class AlesisDMProDevice extends Device
{
    private static final String INFO_TEXT
    = "Known issues: Pitchslider defaults to wrong position when starting editor.\n"
    + "Name of drums aren't displayed until user selects a new category.\n"
    + "Triggers are not implemented, and I probably never will. \n"
    + "Single drum editing is on todo-list";

    /** Creates new AlesisDMProDevice */
    public AlesisDMProDevice ()
    {
	super("Alesis", "DM Pro", "F07E7F060200000E19000000*F7",
	      INFO_TEXT, "Peter Hageus (peter.hageus@comhem.se)");
    }

    /** Constructor for for actual work. */
    public AlesisDMProDevice(Preferences prefs) {
	this();
	this.prefs = prefs;

      	addDriver (new AlesisDMProDrumKitDriver());
        addDriver (new AlesisDMProEffectDriver());
    }
}
