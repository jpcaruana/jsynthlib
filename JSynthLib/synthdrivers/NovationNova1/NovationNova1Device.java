/*
 * $Id$
 * NovationNovaDevice.java
 *
 * Created on 10. Oktober 2001, 22:09
 */

package synthdrivers.NovationNova1;

import core.*;
public class NovationNova1Device extends Device
{
    
    /** Creates new NovationNovaDevice */
    public NovationNova1Device ()
    {
        manufacturerName="Novation";
        modelName="Nova1";
        synthName="Nova1";
        inquiryID="F07E**06020020290100210020000000F7";
	addDriver (new NovationNova1BankDriver ());
        addDriver (new NovationNova1SingleDriver ());
        addDriver (new NovationNova1SinglePerformanceDriver ());
    }
    
}
