package synthdrivers.Generic;

import core.PatchEditorFrame;
import core.Patch;
import java.awt.*;
import synthdrivers.AlesisSR16.DataModel;

import javax.sound.midi.SysexMessage;

/**
 * This class serves as a initial substitute for a patch editor. It is to be used for verifying that
 * your driver is properly getting and decoding the sysex message from the device (and that the bytes
 * in the sysex message are what you expect them to be).
 *
 * To use it, add this to your driver:
 *
 *    protected JSLFrame editPatch(Patch p) {
 *        return (new synthdrivers.Generic.HexDumpEditorFrame(p));
 *    }
 *
 * and then you can use the "Edit" menu option on the patch in your library to inspect the sysex message.
 */
public class HexDumpEditorFrame extends SingleTextAreaFrame {

    static final int bytesperline = 16;

    public HexDumpEditorFrame(byte[] bytes) {
        this();
        appendBytes(bytes);
    }

    public HexDumpEditorFrame(DataModel dm) {
        this(dm.getDecodedBytes());
    }

    public HexDumpEditorFrame(Patch p) {
        this();
        SysexMessage[] messages = p.getMessages();
        for(int i=0; i<messages.length; i++) {
            append("Message " + i + ":\n");
            appendBytes(messages[i].getMessage());
        }
    }

    public HexDumpEditorFrame() {
        super("Hex dump of sysex message",bytesperline * 4 + 12);
    }

    public void appendBytes(byte[] bytes) {
        append(core.Utility.hexDump(bytes,0,-1,bytesperline, true, true));
    }
}
