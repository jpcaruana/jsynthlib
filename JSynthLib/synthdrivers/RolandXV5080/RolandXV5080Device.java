/*
 * RolandXV5080Device.java
 */

package synthdrivers.RolandXV5080;

import core.*;
/**
 *
 * @author  Phil Shepherd
 * @version $Id$
 */
public class RolandXV5080Device extends Device
{
 
    public RolandXV5080Device ()
    {
     super ("Roland","XV5080",null,null,"Phil Shepherd");
        addDriver (new RolandXV5080PatchDriver ());
        addDriver (new RolandXV5080PatchBankDriver ());
        addDriver (new RolandXV5080PerfDriver ());
        addDriver (new RolandXV5080PerfBankDriver ());
    
    }

}
