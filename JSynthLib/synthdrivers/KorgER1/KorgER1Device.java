/*
 * 
 * KorgER1Device.java
 *
 * Created on 10. Oktober 2001, 22:18
 */

package synthdrivers.KorgER1;

import core.Device;
/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgER1Device extends Device
{
    
    /** Creates new KorgER1Device */
    public KorgER1Device ()
    {
        manufacturerName="Korg";
        modelName="Electribe ER1";
        synthName="ER1";
        inquiryID="F07E**06024251**************F7";
        addDriver (new KorgER1SingleDriver ());
        
    }
    
}
