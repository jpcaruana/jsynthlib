/*
 * EmuProteusDevice.java
 *
 * Created on 10. Oktober 2001, 22:04
 */

package synthdrivers.EmuProteusMPS;

import core.*;
/**
 *
 * @author  Administrator
 * @version
 */
public class EmuProteusDevice extends Device
{
    
    /** Creates new EmuProteusDevice */
    public EmuProteusDevice ()
    {
        manufacturerName="Emu";
        modelName="Proteus MPS";
        synthName="MPS";
        inquiryID="F07E**06021804040800*********F7";
        addDriver(new EmuProteusMPSBankDriver());
        addDriver(new EmuProteusMPSSingleDriver());
        
    }
    
}
