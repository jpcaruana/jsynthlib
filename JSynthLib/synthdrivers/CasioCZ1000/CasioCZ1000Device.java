/*
 * CasioCZ1000PC1600Device.java
 */

package synthdrivers.CasioCZ1000;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version $Id$
 */
public class CasioCZ1000Device extends Device
{
 
    public CasioCZ1000Device ()
    {
	super ("Casio","CZ1000",null,null,"Yves Lefebvre");;
        setSynthName("CZ");
        addDriver (new CasioCZ1000SingleDriver ());
        addDriver (new CasioCZ1000BankDriver ());
    
    }

}
