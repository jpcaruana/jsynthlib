/*
 * KorgWavestationDevice.java
 *
 * Created on 10.Feb.2002
 */

package synthdrivers.KorgWavestation;

import core.Device;
/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class KorgWavestationDevice extends Device {
    
    /** Creates new KorgWavestationDevice */
    public KorgWavestationDevice() {
        manufacturerName="Korg";
        modelName="Wavestation";
        setSynthName("Wavestation");
        authors="Gerrit Gehnen";
        infoText="This is an experimental driver. It is not tested on a real device yet!";
        
        inquiryID="F07E**06024228000100********F7";
        addDriver(new KorgWavestationSinglePatchDriver());
        addDriver(new KorgWavestationSinglePerformanceDriver());
        addDriver(new KorgWavestationBankPatchDriver());
        addDriver(new KorgWavestationBankPerformanceDriver());
        addDriver(new KorgWavestationSystemSetupDriver());
        addDriver(new KorgWavestationWaveSequenceDriver());
        addDriver(new KorgWavestationMultiModeSetupDriver());
        addDriver(new KorgWavestationMicroTuneScaleDriver());
        addDriver(new KorgWavestationPerformanceMapDriver());
    }
    
}
