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
/*
 * Device class for the Yamaha TG-100
 *
 * YamahaTG100Device.java
 *
 * Created on 11. July 2004, 14:27
 */

package synthdrivers.YamahaTG100;

import core.*;

import java.util.prefs.Preferences;
/**
 *
 * @author  Joachim Backhaus
 * @version $Id$
 */
public class YamahaTG100Device extends Device {

    private static final String INFO_TEXT
    = "Currently only supporting the librarian features.";
    
    /**
    * Constructor for DeviceListWriter.
    */
    public YamahaTG100Device() {
        super(  "Yamaha",
                "TG100",
                "F07E**06020001050000040001002100F7",
                INFO_TEXT,
                "Joachim Backhaus");
                
        // Got the following from "auto-scan"
        // f0 7e 7f 06 02 00 01 05 00 00 04 00 01 00 21 00 f7
    }


    /**
    * Constructor for the actual work.
    *
    * @param prefs  The Preferences for this device
    */
    public YamahaTG100Device(Preferences prefs) {
        this();

        this.prefs = prefs;

        addDriver(new YamahaTG100AllConverter() );
        addDriver(new YamahaTG100SingleDriver() );
        addDriver(new YamahaTG100BankDriver() );
    }   
}
