/*
 * ImportMidiFile.java
 *
 */

package core;

import javax.sound.midi.*;

/**
 * 
 * @author Gerrit Gehnen
 * @version $Id$
 */

public class ImportMidiFile {

    static boolean doImport(java.io.File file) throws java.io.IOException {
        Sequence seq;
        Track[] tr;

        try {
            seq = MidiSystem.getSequence(file);
        } catch (Exception ex) {
            // If we fall in an exception the file was not a Midifile....
            return false;
        }
        tr = seq.getTracks();
        //ErrorMsg.reportStatus("Track Count "+tr.length);

        for (int j = 0; j < tr.length; j++) {
            //ErrorMsg.reportStatus("Track "+j+":size "+tr[j].size());
            for (int i = 0; i < tr[j].size(); i++) {
                if (tr[j].get(i).getMessage() instanceof SysexMessage) {
                    //ErrorMsg.reportStatus("Track "+j+" Event "+i+" SYSEX!!");
                    IPatch[] patarray = DriverUtil.createPatch(tr[j].get(i)
                            .getMessage().getMessage());
                    for (int k = 0; k < patarray.length; k++) {
                        ((PatchBasket) JSLDesktop.getSelectedFrame())
                                .pastePatch(patarray[k]);
                    }
                }
            }
        }
        return true;
    }
}