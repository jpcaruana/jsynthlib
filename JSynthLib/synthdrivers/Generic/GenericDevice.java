/*
 * GenericDevice.java
 */

package synthdrivers.Generic;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class GenericDevice extends Device
{
 
    public GenericDevice ()
    {
	super ("Generic","Device",null,null,"Brian Klock");
        setSynthName("???");
        addDriver (new Driver("Sysex","Brian Klock"));
    }

}
