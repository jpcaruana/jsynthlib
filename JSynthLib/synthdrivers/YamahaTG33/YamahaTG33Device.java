/*
 * YamahaTG33Device.java
 *
 * Created on 10. Oktober 2001, 22:16
 */

package synthdrivers.YamahaTG33;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class YamahaTG33Device extends Device
{
    
    /** Creates new YamahaTG33Device */
    public YamahaTG33Device ()
    {
        manufacturerName="Yamaha";
        modelName="TG33/SY22";
        synthName="TG33";
        addDriver (new YamahaTG33BankDriver ());
        addDriver (new YamahaTG33SingleDriver ());
    }
    
}
