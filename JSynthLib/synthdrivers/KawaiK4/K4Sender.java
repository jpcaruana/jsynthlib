package synthdrivers.KawaiK4;
import core.SysexSender;

class K4Sender extends SysexSender {
	    private int parameter;
	    private int source;
	    private byte[] b = new byte[10];

	    public K4Sender(int param, int src) {
		parameter = param; source = src;
		b[0] = (byte) 0xF0; b[1] = (byte) 0x40; b[3] = (byte) 0x10;
		b[4] = 0; b[5] = 0x04; b[6] = (byte) parameter; b[9] = (byte) 0xF7;
	    }

	    public K4Sender(int param) {
		parameter = param; source = 0;
		b[0] = (byte) 0xF0; b[1] = (byte) 0x40; b[3] = (byte) 0x10;
		b[4] = 0; b[5] = 0x04; b[6] = (byte) parameter; b[9] = (byte) 0xF7;
	    }

	    public byte[] generate (int value) {
		b[7] = (byte) ((value / 128) + (source * 2));
		b[8] = (byte) (value & 127); b[2] = (byte) (channel - 1);
		return b;
	    }
	}


