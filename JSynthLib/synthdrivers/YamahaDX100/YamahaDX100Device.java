/*
 * YamahaTX81zDevice.java
 *
 * Created on 10. Oktober 2001, 21:23
 */

package synthdrivers.YamahaDX100;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class YamahaDX100Device extends Device
{
    
    /** Creates new YamahaTX81zDevice */
    public YamahaDX100Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX21 / DX 27 / DX100";
        synthName="DX100";
        addDriver (new YamahaDX100BankDriver ());
        addDriver (new YamahaDX100SingleDriver ());
    }
    
}
