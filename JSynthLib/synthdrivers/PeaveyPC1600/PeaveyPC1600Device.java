/*
 * PC1600Device.java
 */

package synthdrivers.PeaveyPC1600;

import core.*;
/**
 *
 * @author  Brian Klock
 * @version
 */
public class PeaveyPC1600Device extends Device
{
 
    public PeaveyPC1600Device ()
    {
     manufacturerName="Peavey";
	
        modelName="PC1600";
        setSynthName("PC1600");
        addDriver (new PeaveyPC1600SingleDriver ());
        addDriver (new PeaveyPC1600BankDriver ());
    
    }

}
