/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.drivers.line6.pod20;

import java.util.prefs.Preferences;

import org.jsynthlib.core.Device;

/** Device file for Line6 Pod
* 
* @author Jeff Weber
*/
public class Line6Pod20Device extends Device
{
    /** Constructor for DeviceListWriter. */
    public Line6Pod20Device ()
    {
        super(Constants.MANUFACTURER_NAME,
              Constants.DEVICE_NAME,
              Constants.INQUIRY_ID,
              Constants.INFO_TEXT,
              Constants.AUTHOR);
    }
    
    /** Constructor for for actual work. */
    public Line6Pod20Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        
        addDriver(new Line6Pod20Converter());
        addDriver(new Line6Pod20SingleDriver());
        addDriver(new Line6Pod20BankDriver());
        addDriver(new Line6Pod20EdBufDriver());
    }    
}
