package core;

public class ParamModel { 
 public int ofs;
 public Patch patch;

    public ParamModel() {
    }

    public ParamModel(Patch p, int o) {
	ofs = o;
	patch = p;
    }

    public void set(int i) {
	patch.sysex[ofs] = (byte) i;
    }

    public int get() {
	return patch.sysex[ofs];
    }
}
