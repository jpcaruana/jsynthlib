package synthdrivers.KawaiK4;
import core.*;

class K4Model extends ParamModel {
	    private int bitmask;
	    private int mult;

	    public K4Model(Patch p, int o) {
		ofs = o + 8; patch = p; bitmask = 255; mult = 1;
	    }

	    public K4Model(Patch p, int o, int b) {
		ofs = o + 8; patch = p; bitmask = b;
		if ((bitmask & 1) == 1) mult = 1;
		else if ((bitmask & 2) == 2) mult = 2;
		else if ((bitmask & 4) == 4) mult = 4;
		else if ((bitmask & 8) == 8) mult = 8;
		else if ((bitmask & 16) == 16) mult = 16;
		else if ((bitmask & 32) == 32) mult = 32;
		else if ((bitmask & 64) == 64) mult = 64;
		else if ((bitmask & 128) == 128) mult = 128;
	    }

	    public void set(int i) {
		patch.sysex[ofs] = (byte) ((i * mult) + (patch.sysex[ofs] & (~bitmask)));
	    }

	    public int get() {
		return ((patch.sysex[ofs] & bitmask) / mult);
	    }
	}
