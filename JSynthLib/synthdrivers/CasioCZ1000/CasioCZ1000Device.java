/*
 * CasioCZ1000PC1600Device.java
 */

package synthdrivers.CasioCZ1000;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version
 */
public class CasioCZ1000Device extends Device
{
 
    public CasioCZ1000Device ()
    {
     manufacturerName="Casio";
	
        modelName="CZ1000";
        setSynthName("CZ");
        addDriver (new CasioCZ1000SingleDriver ());
        addDriver (new CasioCZ1000BankDriver ());
    
    }

}
