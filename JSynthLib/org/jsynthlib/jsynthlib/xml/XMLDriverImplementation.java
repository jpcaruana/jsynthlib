package org.jsynthlib.jsynthlib.xml;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import core.Parameter;

/* This is the class that will be extended by the scripting language */
public class XMLDriverImplementation {
    public static final MidiMessage midiMessage(String str) throws InvalidMidiDataException {
        if (str.length() % 2 == 1)
            throw new IllegalArgumentException("Message must have even number of characters");
        if (str.length() < 2)
            throw new IllegalArgumentException("Message must be at least two characters long");
        byte[] data = new byte[str.length()/2];
        for (int index = 0, start = 0; index < data.length; start += 2) {
            data[index++] = Integer.decode(str.substring(start, start+2)).byteValue(); 
        }
        if (data[0] == (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE
            || data[0] == (byte) SysexMessage.SYSTEM_EXCLUSIVE) {
            SysexMessage msg = new SysexMessage();
            msg.setMessage(data, data.length);
            return msg;
        } else {
            int status = data[0], d1 = 0, d2 = 0;
            switch(data.length) {
            case 3:
                d2 = data[2];
            case 2:
                d1 = data[1];
            }
            ShortMessage msg = new ShortMessage();
            msg.setMessage(status, d1, d2);
            return msg;
        }
    }
    
    public void playPatch(XMLPatch p) {
        /* Do nothing by default */
    }
    public void sendPatch(XMLPatch p) {
        /* Do nothing by default */
    }
    public void storePatch(XMLPatch p, int bank, int patch) {
        /* Do nothing by default */
    }
    public void sendParameter(XMLPatch patch, Parameter param) {
        /* Do nothing by default */
    }
    public void requestPatchDump(int bank, int patch) {
        /* Do nothing by default */
    }
}
