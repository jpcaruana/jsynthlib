/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.YamahaTX81z;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class YamahaTX81zDevice extends Device
{
    
    /** Creates new YamahaTX81zDevice */
    public YamahaTX81zDevice ()
    {
        manufacturerName="Yamaha";
        modelName="TX81z";
        synthName="TX81z";
        
        addDriver (new YamahaTX81zBankDriver ());
        addDriver (new YamahaTX81zSingleDriver ());
        
    }
    
}
