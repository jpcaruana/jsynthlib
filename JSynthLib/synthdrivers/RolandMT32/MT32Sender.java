package synthdrivers.RolandMT32;
import core.SysexSender;

class MT32Sender extends SysexSender {
  /* Data set DT1 12h
   *
   *  00-04 F0 41 10 16 12  // staty of sysex + header
   *  05-07 aa aa aa        // address
   *  08    dd              // data
   *  09    cc              // checksum
   *  0A    F7              // end of sysex
   */
    private int parameter;
	    private int source;
	    private byte[] b = new byte[11];

	    public MT32Sender(int param, int src) {
		parameter = param; source = src;
                b[0]=(byte)0xF0;  b[1]=(byte)0x41;  b[2]=(byte)0x10;  b[3]=(byte)0x16;  b[4]=(byte)0x12; 
                b[5]=(byte)0x04;  b[6]=(byte)0x00;  b[7]=(byte)source;   
                b[8]=(byte)parameter;
                b[10]=(byte)0xF7;
	    }

	    public MT32Sender(int param) {
		parameter = param; source = 0;
                b[0] = (byte)0xF0;  b[1]=(byte)0x41;  b[2]=(byte)0x10;  b[3]=(byte)0x16;  b[4]=(byte)0x12; 
                b[5] = (byte)0x04;  
                b[6] = (byte)(parameter/128);
                b[7] = (byte)(parameter&127);
                b[10]= (byte)0xF7;
            }

	    public byte[] generate (int value) {
		//b[7] = (byte) ((value / 128) + (source * 2));
		b[8] = (byte) (value & 127); //b[2] = (byte) (channel - 1);
                b[9] = (byte)((0 - (b[5] + b[6] + b[7] + b[8])) & 0x7F);
		return b;
	    }
}