/*
 * K4Device.java
 *
 * Created on 5. Oktober 2001, 22:00
 */

package synthdrivers.KawaiK4;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class K4Device extends Device
{
 
    /** Creates new K4Device */
    public K4Device ()
    {
        inquiryID="F07E**06024000000400000000000f7";
        manufacturerName="Kawai";
        modelName="K4/K4R";
        setSynthName("K4");
        addDriver (0,new KawaiK4BulkConverter ());
        addDriver (new KawaiK4SingleDriver ());
        addDriver (new KawaiK4BankDriver ());
        addDriver (new KawaiK4MultiDriver ());
        addDriver (new KawaiK4MultiBankDriver ());
        addDriver (new KawaiK4EffectDriver ());
        addDriver (new KawaiK4EffectBankDriver());
        addDriver (new KawaiK4DrumsetDriver());
   
    }

}
