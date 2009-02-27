package synthdrivers.AlesisQS;

import org.jsynthlib.core.SysexSender;

/**
 * Sender for AlesisQS keyboards.  Uses Midi function 0x10 - "MIDI Editing" -
 * to send changes for one parameter to the keyboard.  Sends in the format
 * F0 00 00 0E 0E 10 <0mmfffff> <0ssppppp> <0ccccddv> <0vvvvvvvv> F7
 * (value is 8 bit 2's complement)
 * @author Zellyn Hunter (zjh, zellyn@zellyn.com)
 * @version $Id$
 */

public class MidiEditSender extends SysexSender
{
	private byte[] sysex = null;

	/**
	 * Create a new MidiEditSender for a specific parameter.
	 * @param mode 0=Global, 1=Mix, 2=Program, 3=Effects
	 * @param function 0-16, depending on mode
	 * @param sound Sound 1-4 (0-3) if mode=2, effect bus 1-4 (0-3) if mode=3
	 * @param page 0-23, depending on mode and function
	 * @param channel 1-16 (0-15).  Ignored unless mode = 1 or 2
	 * @param pot Data entry pot number 1-4 (0-3)
	 */
	public MidiEditSender(int mode, int function, int sound, int page,
						  int channel, int pot)
	{
		if (mode<0 || mode>3)
			throw new IllegalArgumentException("Mode must be 0-3");
		if (function<0 || function>16)
			throw new IllegalArgumentException("Function must be 0-16");
		if (sound<0 || sound>3)
			throw new IllegalArgumentException("Sound must be 0-3");
		if (page<0 || page>23)
			throw new IllegalArgumentException("Page must be 0-23");
		if (channel<0 || channel>15)
			throw new IllegalArgumentException("Channel must be 0-15");
		if (pot<0 || pot>3)
			throw new IllegalArgumentException("Pot must be 0-3");

		// * F0 00 00 0E 0E 10 <0mmfffff> <0ssppppp> <0ccccddv> <0vvvvvvvv> F7
		this.sysex = new byte[11];
		this.sysex[0]  = (byte)0xF0;
		this.sysex[1]  = 0x00;
		this.sysex[2]  = 0x00;
		this.sysex[3]  = 0x0E;
		this.sysex[4]  = 0x0E;
		this.sysex[5]  = 0x10;
		this.sysex[6]  = (byte) ((mode<<5) | function);
		this.sysex[7]  = (byte) ((sound<<5) | page);
		this.sysex[8]  = (byte) ((channel<<3) | (pot<<1)); // [0]  = value[7]
		this.sysex[9]  = 0x00;                             // [0-6]= value[0-6]
		this.sysex[10] = (byte)0xF7;
	}

	/**
	 * Return the sysex required to set this Midi parameter to the given
	 * value.
	 * @param value the value, in 8-bit 2's complement
	 */
	public byte[] generate(int value) {
		if (value<-128 || value>127)
			throw new IllegalArgumentException("Value must be in the range "
											   + "[-128,127]");
		this.sysex[8] = (byte)((this.sysex[8] & 0xFE) | ((value & 0xFF) >> 7));
		this.sysex[9] = (byte) (value & 0x7F);
		return this.sysex;
	}
} 
