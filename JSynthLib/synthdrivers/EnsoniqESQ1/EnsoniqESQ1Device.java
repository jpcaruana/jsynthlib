/*
 * EnsoniqESQ1Device.java
 *
 * Created on 10. Oktober 2001, 22:07
 */

package synthdrivers.EnsoniqESQ1;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version 0.1
 */
public class EnsoniqESQ1Device extends Device
{
    
    /** Creates new EnsoniqESQ1Device */
    public EnsoniqESQ1Device ()
    {
        manufacturerName="Ensoniq";
        modelName="ESQ-1";
        synthName="ESQ1";
        inquiryID="F07E**06020F0200*************F7";
        addDriver (new EnsoniqESQ1BankDriver ());
        addDriver (new EnsoniqESQ1SingleDriver ());
    }
    
}
