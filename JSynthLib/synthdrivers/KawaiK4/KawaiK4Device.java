/*
 * KawaiK4Device.java
 *
 */

package synthdrivers.KawaiK4;

import core.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */
public class KawaiK4Device extends Device {
    
    /** Creates new K4Device */
    public KawaiK4Device() {
        inquiryID="F07E**06024000000400000000000f7";
        manufacturerName="Kawai";
        modelName="K4/K4R";
        setSynthName("K4");
        infoText="There are no known issues or problems with this synthesizer. Librarian and Editing functions are "+
                 "available for all K4 datatypes, Singles, Single Banks, Multis, MultiBanks, Effects, EffectBanks, and "+
                 "Drumkits. Note that the K4r does not have the effects section that is present on the K4.";
        
        addDriver(0,new KawaiK4BulkConverter());
        addDriver(new KawaiK4SingleDriver());
        addDriver(new KawaiK4BankDriver());
        addDriver(new KawaiK4MultiDriver());
        addDriver(new KawaiK4MultiBankDriver());
        addDriver(new KawaiK4EffectDriver());
        addDriver(new KawaiK4EffectBankDriver());
        addDriver(new KawaiK4DrumsetDriver());
        
    }
    
}
