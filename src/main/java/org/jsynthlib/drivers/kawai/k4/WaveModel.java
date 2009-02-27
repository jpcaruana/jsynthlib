package org.jsynthlib.drivers.kawai.k4;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexWidget;


class WaveModel implements SysexWidget.IParamModel {
    private Patch patch;
    private int source;

    public WaveModel(Patch p, int s) {
	patch = p; source = s;
    }

    public void set(int i) {
	patch.sysex[34 + 8 + source]
	    = (byte) ((patch.sysex[34 + 8 + source] & 254) + (byte) (i / 128));
	patch.sysex[38 + 8 + source] = (byte) (i % 128);
    }

    public int get() {
	return (((patch.sysex[34 + 8 + source] & 1) * 128)
		+ (patch.sysex[38 + 8 + source]));
    }
}
