/*
 * PC1600Device.java
 */

package synthdrivers.PeaveyPC1600;

import core.*;
/**
 *
 * @author  Phil Shepherd
 * @version $Id$
 */
public class PeaveyPC1600Device extends Device
{
 
    public PeaveyPC1600Device ()
    {
     super ("Peavey","PC1600",null,null,"Phil Shepherd");
        addDriver (new PeaveyPC1600SingleDriver ());
        addDriver (new PeaveyPC1600BankDriver ());
    
    }

}
