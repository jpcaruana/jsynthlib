package synthdrivers.RolandMT32;
import core.*;

class MT32Model extends ParamModel {
	    private int bitmask;
	    private int mult;

	    public MT32Model(Patch p, int o) {
		ofs = o + 8; patch = p;   // o + 8: offset in message vs. address
	    }

	    public MT32Model(Patch p, int o, int b) {
		ofs = o + 8; patch = p;   // o + 8: offset in message vs. address
	    }

	    public void set(int i) {
		patch.sysex[ofs] = (byte) i;
	    }

	    public int get() {
		return patch.sysex[ofs];
	    }
	}
