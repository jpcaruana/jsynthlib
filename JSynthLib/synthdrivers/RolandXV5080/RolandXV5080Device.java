/*
 * RolandXV5080Device.java
 */

package synthdrivers.RolandXV5080;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version
 */
public class RolandXV5080Device extends Device
{
 
    public RolandXV5080Device ()
    {
     manufacturerName="Roland";
	
        modelName="XV5080";
        setSynthName("XV5080");
        addDriver (new RolandXV5080PatchDriver ());
        addDriver (new RolandXV5080PatchBankDriver ());
        addDriver (new RolandXV5080PerfDriver ());
        addDriver (new RolandXV5080PerfBankDriver ());
    
    }

}
