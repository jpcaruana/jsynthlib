//
//  Line6BassPodDevice.java
//  JSynthLib
//
//  Created by Jeff Weber on Sat Jun 26 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6BassPod;

import core.Device;
import java.util.prefs.Preferences;

/**
*
* @author Jeff Weber
* @version $Id$
*/
public class Line6BassPodDevice extends Device
{
    /** Constructor for DeviceListWriter. */
    public Line6BassPodDevice ()
    {
        super(Constants.MANUFACTURER_NAME,
              Constants.DEVICE_NAME,
              Constants.INQUIRY_ID,
              Constants.INFO_TEXT,
              Constants.AUTHOR);
    }
    
    /** Constructor for for actual work. */
    public Line6BassPodDevice(Preferences prefs) {
        this();
        this.prefs = prefs;
        
        addDriver(new Line6BassPodConverter());
        addDriver(new Line6BassPodSingleDriver());
        addDriver(new Line6BassPodBankDriver());
        addDriver(new Line6BassPodEdBufDriver());
    }    
}
