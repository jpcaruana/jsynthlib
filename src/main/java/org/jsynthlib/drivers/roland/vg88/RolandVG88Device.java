/*
 * Copyright 2006 Nacho Alonso
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

 /*
 * RolandVG88Device.java
 */
 
package org.jsynthlib.drivers.roland.vg88;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;
 
 /**
 * @author  Nacho Alonso
 * @version $Id$
 */

 public class RolandVG88Device extends Device {

    public RolandVG88Device () {
     super ("Roland","VG88",null,null,"Nacho Alonso");
    }

    /** Constructor for for actual work. */
    public RolandVG88Device(Preferences prefs) {
	this();
	this.prefs = prefs;

	RolandVG88SingleDriver singleDriver = new RolandVG88SingleDriver();

	addDriver(singleDriver);
	addDriver(new RolandVG88BankDriver(singleDriver));
	addDriver(new RolandVG88SysDatDriver(singleDriver));      
    }
}
