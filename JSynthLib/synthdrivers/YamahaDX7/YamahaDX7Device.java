/*
 * JSynthlib-Device for Yamaha DX7 Mark-I (with Firmware IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * email:   Torsten.Tittmann@t-online.de
 * file:    YamahaDX7Device.java
 * date:    15.01.2002
 * @version 0.1
 */

package synthdrivers.YamahaDX7;
import core.*;

public class YamahaDX7Device extends Device
{
    
    /** Creates new YamahaDX7Device */
    public YamahaDX7Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX7";
        synthName="DX7";
        addDriver (new YamahaDX7BankDriver ());
        addDriver (new YamahaDX7SingleDriver ());
    }
    
}
