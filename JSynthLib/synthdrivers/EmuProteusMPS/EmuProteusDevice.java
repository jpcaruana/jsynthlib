/*
 * EmuProteusDevice.java
 *
 * Created on 10. Oktober 2001, 22:04
 */

package synthdrivers.EmuProteusMPS;

import core.*;
/**
 *
 * @author
 * @version $Id$
 */
public class EmuProteusDevice extends Device
{
    
    /** Creates new EmuProteusDevice */
    public EmuProteusDevice ()
    {
        manufacturerName="Emu";
        modelName="Proteus MPS";
        synthName="MPS";
        inquiryID="F07E**06021804040800********F7";
        infoText="This synthesizer lacks a MIDI addressable patch buffer. Therefore, when you send or play a patch "+
	         "from within JSynthLib, the patch at location 100 on the Proteus will be overwritten. JSynthLib "+
	         "treats this location as an edit buffer.";

        addDriver(new EmuProteusMPSBankDriver());
        addDriver(new EmuProteusMPSSingleDriver());
        
    }
    
}
