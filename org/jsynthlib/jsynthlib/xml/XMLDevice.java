package org.jsynthlib.jsynthlib.xml;

import java.util.prefs.Preferences;

//import javax.sound.midi.MidiMessage;

import core.Device;
//import core.IDriver;

public class XMLDevice extends Device {
    
    
    /**
     * @param manufacturerName
     * @param modelName
     * @param inquiryID
     * @param infoText
     * @param authors
     */
    public XMLDevice(String manufacturerName, String modelName,
            String inquiryID, String infoText, String authors) {
        super(manufacturerName, modelName, inquiryID, infoText, authors);
    }
    public void setPreferences(Preferences p) {
        prefs = p;
    }
//    public void addDriver(IDriver driver) {
//        super.addDriver(driver);
//    }
    
    /* Make public */
//    public void send(MidiMessage message) {
//        super.send(message);
//    }
    // Show as xml device by default
    public String getSynthName() {
        return prefs.get("synthName", getModelName() + " (XML)");
    }
}
