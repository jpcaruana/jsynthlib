/*
 * Copyright 2004 Joachim Backhaus
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

package synthdrivers.QuasimidiQuasar;

import core.*;

import java.util.prefs.Preferences;
/**
 * Device class for the Quasimidi Quasar
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class QuasimidiQuasarDevice extends Device {
	private static final String INFO_TEXT
    = "Currently only supporting the librarian features for single perfomances and performance banks.";
    
    /**
    * Constructor for DeviceListWriter.
    */
    public QuasimidiQuasarDevice() {
    	super(	"Quasimidi",
    			"Quasar",
        		"F07E**06023F20**************F7",
        		INFO_TEXT,
        		"Joachim Backhaus");
    }


    /**
    * Constructor for the actual work.
    *
    * @param prefs	The Preferences for this device
    */
    public QuasimidiQuasarDevice(Preferences prefs) {
    	this();

    	this.prefs = prefs;

		QuasimidiQuasarSingleDriver quasarSingleDriver = new QuasimidiQuasarSingleDriver();

        addDriver(quasarSingleDriver);
        addDriver(new QuasimidiQuasarBankDriver(quasarSingleDriver));
    }

}
