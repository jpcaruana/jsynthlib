/*
 * KawaiK5000Device.java
 *
 * Created on 10. Oktober 2001, 21:46
 */

package synthdrivers.KawaiK5000;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class KawaiK5000Device extends Device
{
    
    /** Creates new KawaiK5000Device */
    public KawaiK5000Device ()
    {
        manufacturerName="Kawai";
        modelName="K5000";
        synthName="K5k";
        inquiryID="F07E**06024000000A***********F7";
        addDriver (new KawaiK5000BankDriver ());
        addDriver (new KawaiK5000ADDSingleDriver ());
        addDriver (new KawaiK5000CombiDriver ());
        addDriver (new KawaiK5000CombiBankDriver ());


    }
    
}
