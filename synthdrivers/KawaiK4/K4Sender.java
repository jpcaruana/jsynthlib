package synthdrivers.KawaiK4;

import org.jsynthlib.core.SysexSender;

class K4Sender extends SysexSender {
    private int source;

    private byte[] b = {
            (byte) 0xF0, 0x40, 0, 0x10, 0x00,
            0x04, 0, 0, 0, (byte) 0xF7
    };

    public K4Sender(int parameter, int source) {
        this.source = source;
        b[6] = (byte) parameter;
    }

    public K4Sender(int parameter) {
        this.source = 0;
        b[6] = (byte) parameter;
    }

    public byte[] generate(int value) {
        b[2] = (byte) (channel - 1);
        b[7] = (byte) ((value / 128) + (source * 2));
        b[8] = (byte) (value & 127);
        return b;
    }
}
