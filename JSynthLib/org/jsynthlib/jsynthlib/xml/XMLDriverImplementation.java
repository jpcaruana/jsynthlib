package org.jsynthlib.jsynthlib.xml;

import java.util.regex.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import core.AppConfig;
import core.Device;
import core.ErrorMsg;

/* This is the class that will be extended by the scripting language */
public class XMLDriverImplementation {
    private static String hexchars = "0123456789ABCDEF";
    private static Pattern whitespace = Pattern.compile("\\s");
    public static final MidiMessage midiMessage(String str) throws InvalidMidiDataException {
        str = whitespace.matcher(str).replaceAll("");
        if (str.length() % 2 == 1)
            throw new IllegalArgumentException("Message must have even number of characters");
        if (str.length() < 2)
            throw new IllegalArgumentException("Message must be at least two characters long");
        byte[] data = new byte[str.length()/2];
        for (int index = 0, start = 0; index < data.length; start += 2) {
            data[index++] = (byte)Integer.parseInt(str.substring(start, start+2),16);
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
    public static final String hex(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("Argument must be non-negative");
        }
        StringBuffer sb = new StringBuffer();
        do {
            sb.append(hexchars.charAt(x & 0xF));
            x >>= 4;
        } while (x > 0);
        if ((sb.length() & 1) == 1)
            sb.append('0');
        return sb.reverse().toString();
    }
    public void playPatch(XMLPatch p) {
        try {
            Thread.sleep(100);
            ShortMessage msg = new ShortMessage();
            Device d = p.getDevice();
            int channel = d.getChannel() - 1;
            msg.setMessage(ShortMessage.NOTE_ON, channel,
                    AppConfig.getNote(),
                    AppConfig.getVelocity());
            p.getDevice().send(msg);

            Thread.sleep(AppConfig.getDelay());

            msg.setMessage(ShortMessage.NOTE_ON, channel,
                    AppConfig.getNote(),
                    0);  // expecting running status
            d.send(msg);
            } catch (Exception e) {
                ErrorMsg.reportStatus(e);
            }
    }
    public void sendPatch(XMLPatch p) {
        /* Do nothing by default */
    }
    public void storePatch(XMLPatch p, int bank, int patch) {
        /* Do nothing by default */
    }
    public void sendParameter(XMLPatch patch, XMLParameter param) {
        /* Do nothing by default */
    }
    public void requestPatchDump(XMLDevice device, int bank, int patch) {
        /* Do nothing by default */
    }
}
