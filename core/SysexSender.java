package core;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

/**
 * An implementation of SysexWidget.ISender.
 * @see SysexWidget.ISender
 */
public class SysexSender implements SysexWidget.ISender {
    /**
     * Device ID [1-128]. Used by generate(int). Wrong name and should be
     * private. But it's too late, because many synthdriver's subclasses are
     * using this.
     */
    protected byte channel;
    private String sysex;

    /**
     * Creates a new <code>SysexSender</code> instance. A subclass which uses
     * this constructor does not use sysex field. The subclass has to override
     * <code>generate</code> method.
     * 
     * It implies that the subclass does not have to extend this class and can
     * simply implement SysexWidget.ISender.
     */
    public SysexSender() {
    }

    /**
     * Creates a new <code>SysexSender</code> instance which uses default
     * <code>generate</code> method.
     * 
     * @param sysex
     *            A <code>String</code> value like <code>"F041##0123**"</code>.
     *            Each two letters replesents a hex byte data.
     *            <dl>
     *            <dt><code>"**"</code>
     *            <dd>replaced by <code>value</code> passed to the
     *            <code>generate</code> method.
     *            <dt><code>"@@"</code>
     *            <dd>replaced by <code>(channel - 1)</code>.
     *            <dt><code>"##"</code>
     *            <dd>replaced by <code>(channel - 1 + 16)</code>.
     *            </dl>
     */
    public SysexSender(String sysex) {
	this.sysex = sysex;
    }

    /**
     * Return a Sysex data for <code>value</code>.
     * 
     * Subclass which uses <code>SysexSender()</code> constructor instead of
     * <code>SysexSender(String)</code> has to override this method.
     * 
     * @param value
     *            an <code>int</code> value
     * @return a <code>byte[]</code> value
     */
    protected byte[] generate(int value) {
	byte[] b = new byte[sysex.length() / 2];
	for (int i = 0; i < sysex.length(); i += 2) {
	    if (sysex.charAt(i) == '*')
		b[i / 2] = (byte) value;
	    else if (sysex.charAt(i) == '@')
		b[i / 2] = (byte) (channel - 1);
	    else if (sysex.charAt(i) == '#')
		b[i / 2] = (byte) (channel - 1 + 16);
	    else {
		b[i / 2] = (byte) Integer.parseInt(sysex.substring(i, i + 2), 16);
	    }
	}
	return b;
    }

    // SysexWidget.ISender method
    public void send(IPatchDriver driver, int value) {
        channel = (byte) driver.getDevice().getDeviceID();
        byte[] sysex = generate(value);
        SysexMessage m = new SysexMessage();
        try {
            m.setMessage(sysex, sysex.length);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
}
