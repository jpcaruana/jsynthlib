/*
 * ImportMidiFile.java
 *
 */

package core;
import javax.sound.midi.*;
/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 */

public class ImportMidiFile {
    
    static boolean doImport(java.io.File file) throws java.io.IOException {
        Sequence seq;
        Track[] tr;
        
        try {
            seq=MidiSystem.getSequence(file);
        }catch (Exception ex) {
            // If we fall in an exception the file was not a Midifile....
            return false;
        }
        tr=seq.getTracks();
        //System.out.println("Track Count "+tr.length);
        
        for (int j=0;j<tr.length;j++) {
            // System.out.println("Track "+j+":size "+tr[j].size());
            for (int i=0;i<tr[j].size();i++) {
                if (tr[j].get(i).getMessage() instanceof SysexMessage) {
                    // System.out.println("Track "+j+" Event "+i+" SYSEX!!");
                    Patch q=new Patch(tr[j].get(i).getMessage().getMessage());
                    Patch[] patarray=q.dissect();
                    for (int k=0;k<patarray.length;k++) {
                        PatchEdit.Clipboard=patarray[k];
                    ((LibraryFrame)JSLDesktop.getSelectedFrame()).PastePatch();
                }
            }
        }
        }
        return true;
        
    }
}
