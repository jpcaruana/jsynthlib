//
//  Line6Pod20Device.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.Device;
import java.util.prefs.Preferences;

/**
*
* @author Jeff Weber
* @version $Id$
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
        
//        addDriver(new Line6Pod20Converter());
        addDriver(new Line6Pod20SingleDriver());
        addDriver(new Line6Pod20BankDriver());
        addDriver(new Line6Pod20EdBufDriver());
    }    
}
