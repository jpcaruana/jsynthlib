/*
 * GenericDevice.java
 */

package synthdrivers.Generic;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version
 */
public class GenericDevice extends Device
{
 
    public GenericDevice ()
    {
        manufacturerName="Generic";
        modelName="Device";
        setSynthName("???");
        addDriver (new Driver());
   
    }

}
