package synthdrivers.KawaiK4;

import org.jsynthlib.core.ParamModel;
import org.jsynthlib.core.Patch;

class K4Model extends ParamModel {
    private int bitmask;
    private int mult;

    public K4Model(Patch p, int offset) {
        super(p, offset + 8);
        bitmask = 255;
        mult = 1;
    }

    public K4Model(Patch p, int offset, int bitmask) {
        super(p, offset + 8);
        this.bitmask = bitmask;
        if ((bitmask & 1) == 1)
            mult = 1;
        else if ((bitmask & 2) == 2)
            mult = 2;
        else if ((bitmask & 4) == 4)
            mult = 4;
        else if ((bitmask & 8) == 8)
            mult = 8;
        else if ((bitmask & 16) == 16)
            mult = 16;
        else if ((bitmask & 32) == 32)
            mult = 32;
        else if ((bitmask & 64) == 64)
            mult = 64;
        else if ((bitmask & 128) == 128)
            mult = 128;
    }

    public void set(int i) {
        patch.sysex[ofs] = (byte) ((i * mult) + (patch.sysex[ofs] & (~bitmask)));
    }

    public int get() {
        return ((patch.sysex[ofs] & bitmask) / mult);
    }
}