package core;

/**
 * Every time a widget moves, its Sender gets told. The job of the
 * Sender is to return a Sysex data (an array of <code>byte</code>) by
 * using <code>generate(int)</code> method.  The Sysex string will be
 * sent to the synth informing it of the change. Usually a Single
 * Editor will have one or more Sender. Sometimes more than one is
 * used because a synth uses more than one method to transfer the
 * data.
 */
public class SysexSender {
    /** channel (Device ID) [1-128]. Set by SysexWidgets.*/
    protected byte channel;
    private String sysex;

    /**
     * Creates a new <code>SysexSender</code> instance.
     */
    public SysexSender() {
    }

    /**
     * Creates a new <code>SysexSender</code> instance which uses
     * default <code>generate</code> method.
     *
     * @param sysex a <code>String</code> value like
     * <code>"F041##0123**"</code>. Each two letters replesents a hex
     * byte data.  <code>"**"</code> is replaced by <code>value</code>
     * passed to the <code>generate</code> method.  <code>"@@"</code> is
     * replaced by <code>(channel - 1)</code>.  <code>"##"</code> is
     * replaced by <code>(channel - 1 + 16)</code>.
     */
    public SysexSender(String sysex) {
	this.sysex = sysex;
    }

    /**
     * Return a Sysex data for <code>value</code>.
     *
     * @param value an <code>int</code> value
     * @return a <code>byte[]</code> value
     */
    public byte[] generate(int value) {
	byte[] b = new byte[sysex.length() / 2];
	for (int i = 0; i < sysex.length(); i += 2) {
	    if (sysex.charAt(i) == '*')
		b[i / 2] = (byte) value;
	    else if (sysex.charAt(i) == '@')
		b[i / 2] = (byte) (channel - 1);
	    else if (sysex.charAt(i) == '#')
		b[i / 2] = (byte) (channel - 1 + 16);
	    else {
		Integer in = new Integer(0);
		b[i / 2] = (byte) Integer.parseInt(sysex.substring(i, i + 2), 16);
	    }
	}
	return b;
    }
}
