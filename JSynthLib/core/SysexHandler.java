package core;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.SysexMessage;

/**
 * A class for efficient and convenient processing of sysex messages
 * in Driver class.  The basic concept is to store the sysex as a byte
 * array, using an index to the special bytes (eg <code>@@</code>,
 * <code>*patchNum*</code>, ..)  to insert values during
 * toSysexMessage.
 * <OL>
 * <LI> Understands space seperated "Sysex" strings of the form:
 *        <pre>"F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7"</pre>
 *      Or dense hex strings of the form:
 *        <pre>"F000001B*B0015**00**0*F7"</pre>
 *      (though this syntax can not handled multiple values such as
 *      <code>*patchNum*</code>, <code>*bankNum*</code>)
 * <LI> On converstion to byte array (prior to being sent as a
 *      sysex message):
 *      <DIR>
 *       <LI><code>*name*</code> - Replaced by the appropriate
 *           value by using a <code>NameValue</code> instance.
 *       <LI><code>**</code> - Replace by THE value (What does this mean?)
 *       <LI><code>@@</code> - Replaced by the deviceID number
 *       <LI><code>##</code> - Replaced by (deviceID number + 16)
 *      </DIR>
 * </OL>
 * Example:
 * <pre>
 * sysexHandler = new SysexHandler("F0 00 00 1B 0B @@ 14 *patchNum* 00 *bankNum* 00 F7");
 * send(sysexHandler.toSysexMessage(getDeviceID(),
 *                                  new NameValue("patchNum", patchNum),
 *                                  new NameValue("bankNum",  bankNum)));
 * </pre>
 * @see NameValue
 * @see Driver#sysexRequestDump
 * @see Driver#requestPatchDump
 */
// Modifications by: phil@muqus.com - 07/2001
public class SysexHandler /*implements Serializable*/ {
    /** Sysex byte array. */
    private byte[] sysex = null;
    /** Vector for "<code>*patchNum*</code>" form value. */
    private Vector vNameValueIndex = new Vector();
    /** Index for "<code>@@</code>". */
    private int channelIndex = -1;
    /** Index for "<code>##</code>". */
    private int channel16Index = -1;

    /**
     * Creates a new <code>SysexHandler</code> instance.
     *
     * @param src a Sysex String
     * @see NameValue
     */
    public SysexHandler(String src) {
	setSysex(src);
    }

    /**
     * Return length of the sysex byte array.
     * @deprecated Don't use this.
     */
    public int length() {
	return sysex.length;
    }

    /**
     * Set Sysex String.
     * @see #SysexHandler
     */
    private void setSysex(String src) {
	//----- Reset instance variables
	channelIndex = -1;
	channel16Index = -1;
	vNameValueIndex.removeAllElements();

	if (src.length() < 3 || src.charAt(2) != ' ') { // src is a dense hex string
	    setSysexFromDenseHexStr(src);
	    return;
	}

	StringTokenizer st = new StringTokenizer(src);
	sysex = new byte[st.countTokens()];

	for (int iByte = 0; st.hasMoreTokens(); iByte++) {
	    String sToken = st.nextToken();

	    switch (sToken.charAt(0)) {
	    case '*':
		vNameValueIndex.addElement(new NameValue(sToken.substring(1, sToken.length() - 1), iByte));
		sysex[iByte] = (byte) 0;
		break;

	    case '@':
		channelIndex = iByte;
		sysex[iByte] = (byte) 0;
		break;

	    case '#':
		channel16Index = iByte;
		sysex[iByte] = (byte) 0;
		break;

	    default:
		try {
		    sysex[iByte] = (byte) Integer.parseInt(sToken, 16);
		} catch (Exception ex) {
		    ErrorMsg.reportError
			("SysexHandler",
			 "Invalid number, " + sToken
			 + " in space separated hex string: " + src);
		    return;
		}
		break;
	    }
	}
    }

    /**
     * Convert dense format hex string to a byte array.
     */
    //   1) Dense hex strings of the form:
    //        "F000001B0B@@15**00**00F7" (though this syntax can not
    //        handled multiple values such as *patchNum*, *bankNum*)
    //   2) The ** values end up with the name ** such that they can be
    //      replaced using:
    //       Either - sysexHandler.toByteArray((byte)deviceID, new
    //       SysexHandler.NameValue("**", patchNum));
    //       Or(simpler)  - sysexHandler.toByteArray((byte) deviceID, patchNum);
    //   3) private as setSysex initialises channelIndex etc..
    private void setSysexFromDenseHexStr(String src) {
	int nBytes = src.length() / 2;
	sysex = new byte[nBytes];

	int iSrc = 0;
	for (int iByte = 0; iByte < nBytes; iByte++, iSrc += 2) {
	    switch (src.charAt(iSrc)) {
	    case '*':
		vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
		break;

	    case '@':
		channelIndex = iSrc;
		break;

	    case '#':
		channel16Index = iSrc;
		break;

	    default:
		if (src.charAt(iSrc + 1) == '*') { // eg 0*
		    vNameValueIndex.addElement(new NameValue(src.substring(iSrc, iSrc + 2), iByte));
		} else {
		    try {
			sysex[iByte] = (byte) Integer.parseInt(src.substring(iSrc, iSrc + 2), 16);
		    } catch (Exception ex) {
			ErrorMsg.reportError
			    ("SysexHandler",
			     "Invalid number, " + src.substring(iSrc, iSrc + 2)
			     + " in dense hex string: " + src);
			return;
		    }
		}
		break;
	    }
	}
    }

    /**
     * Return SysexMessage with replacable values set via data passed
     * as arguments.
     *
     * @param deviceID Device ID
     * @param nameValues a array of <code>NameValue</code> value
     * @return a <code>SysexMessage</code> value
     * @see NameValue
     */
    public SysexMessage toSysexMessage(int deviceID, NameValue[] nameValues) {
	return toSysexMessage(toByteArray((byte) deviceID, nameValues));
    }

    /**
     * Return SysexMessage with replacable values set via data passed
     * as arguments.
     *
     * @param deviceID Device ID
     * @param nameValue a <code>NameValue</code> value
     * @return a <code>SysexMessage</code> value
     * @see NameValue
     */
    public SysexMessage toSysexMessage(int deviceID, NameValue nameValue) {
	NameValue[] nameValues = new NameValue[] {nameValue};
	return toSysexMessage(deviceID, nameValues);
    }

    /**
     * Return SysexMessage with replacable values set via data passed
     * as arguments.
     *
     * @param deviceID Device ID
     * @param nameValue1 a <code>NameValue</code> value
     * @param nameValue2 a <code>NameValue</code> value
     * @return a <code>SysexMessage</code> value
     * @see NameValue
     */
    public SysexMessage toSysexMessage(int deviceID, NameValue nameValue1,
				       NameValue nameValue2) {
	NameValue[] nameValues = new NameValue[] {nameValue1, nameValue2};
	return toSysexMessage(deviceID, nameValues);
    }

    /**
     * A simplified methodology to use when there's only one value to
     * be replaced (so the *patchNum* syntax is wasteful).
     */
    public SysexMessage toSysexMessage(int deviceID, int value) {
	return toSysexMessage(toByteArray((byte) deviceID, value));
    }

    /**
     * A simplified methodology to use when no value to be replaced
     * (so the *patchNum* syntax is wasteful).
     */
    public SysexMessage toSysexMessage(int deviceID) {
	return toSysexMessage(toByteArray((byte) deviceID, 0));
    }

    /** convert byte array into SysexMessage */
    private SysexMessage toSysexMessage(byte[] b) {
	SysexMessage m = new SysexMessage();
	try {
	    m.setMessage(sysex, sysex.length);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	}
	return m;
    }

    /**
     * Return Sysex string as a byte array with replacable values set
     * via data passed as arguments.
     *
     * @param deviceID Device ID
     * @param nameValues a array of <code>NameValue</code> value
     * @return a <code>byte[]</code> value
     * @see NameValue
     * @Xdeprecated Use toSysexArray(int deviceID, NameValue[] nameValues).
     */
    public byte[] toByteArray(byte deviceID, NameValue[] nameValues) {
	// Replace the deviceID number
	if (channelIndex != -1)
	    sysex[channelIndex] = (byte) ((int) deviceID - 1);
	if (channel16Index != -1)
	    sysex[channel16Index] = (byte) ((int) deviceID - 1 + 16);

	// Replace values
	for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();) {
	    NameValue nameValueIndex = (NameValue) en.nextElement();

	    boolean bValueFound = false;
	    for (int i = 0; i < nameValues.length; i++) {
		if (nameValueIndex.getName().equalsIgnoreCase(nameValues[i].getName())) {
		    sysex[nameValueIndex.getValue()] = (byte) nameValues[i].getValue();
		    bValueFound = true;
		    break;
		}
	    }
	    if (!bValueFound) {
		ErrorMsg.reportError("SysexHandler", "No value specified for: " + nameValueIndex.getName());
		return sysex;
	    }
	}
	return sysex;
    }

    /**
     * A simplified methodology to use when there's only one value to
     * be replaced (so the *patchNum* syntax is wasteful).
     * @Xdeprecated Use toSysexArray(int deviceID, int value).
     */
    public byte[] toByteArray(byte deviceID, int value) {
	// Replace the channel number
	if (channelIndex != -1)
	    sysex[channelIndex] = (byte) ((int) deviceID - 1);
	if (channel16Index != -1)
	    sysex[channel16Index] = (byte) ((int) deviceID - 1 + 16);

	// Replace values
	for (Enumeration en = vNameValueIndex.elements(); en.hasMoreElements();)
	    sysex[((NameValue) en.nextElement()).getValue()] = (byte) value;

	return sysex;
    }

    /**
     * Return a byte array where @@, ** etc.. have been replaced by 0.
     * @Xdeprecated Use toSysexArray(0, 0).
     */
    public byte[] toByteArray() {
	return toByteArray((byte) 0, 0);
    }

    /**
     * Send a sysex message to a MIDI output port.
     *
     * @param port MIDI output port number.
     * @param deviceID device ID
     * @Xdeprecated Use toSysexArray(int deviceID) and Driver.send().
     */
    public void send(int port, byte deviceID) {
	send(port, deviceID, 0);
    }

    /**
     * Send a sysex message to a MIDI output port.
     *
     * @param port MIDI output port number.
     * @param deviceID device ID
     * @param value data value
     * @Xdeprecated Use toSysexArray(int deviceID, int value) and Driver.send().
     */
    public void send(int port, byte deviceID, int value) {
	byte[] sysex = toByteArray(deviceID, value);
	send(port, sysex);
    }

    /**
     * Send a sysex message to a MIDI output port.
     *
     * @param port MIDI output port number.
     * @param deviceID device ID
     * @param nameValue1 a <code>NameValue</code> value
     * @see NameValue
     * @Xdeprecated Use toSysexArray(int deviceID, NameValue
     * nameValue1) and Driver.send().
     */
    public void send(int port, byte deviceID, NameValue nameValue1) {
	NameValue[] nameValues = new NameValue[] {nameValue1};
	send(port, deviceID, nameValues);
    }

    /**
     * Send a sysex message to a MIDI output port.
     *
     * @param port MIDI output port number.
     * @param deviceID device ID
     * @param nameValue1 a <code>NameValue</code> value
     * @param nameValue2 a <code>NameValue</code> value
     * @see NameValue
     * @Xdeprecated Use toSysexArray(int deviceID, NameValue
     * nameValue1, NameValue nameValue2) and Driver.send().
     */
    public void send(int port, byte deviceID, NameValue nameValue1, NameValue nameValue2) {
	NameValue[] nameValues = new NameValue[] {nameValue1, nameValue2};
	send(port, deviceID, nameValues);
    }

    /**
     * Send a sysex message to a MIDI output port.
     *
     * @param port MIDI output port number.
     * @param deviceID device ID
     * @param nameValues an array of <code>NameValue</code>.
     * @see NameValue
     * @Xdeprecated Use toSysexArray(int deviceID, NameValue[]
     * nameValues) and Driver.send().
     */
    public void send(int port, byte deviceID, NameValue[] nameValues) {
	byte[] sysex = toByteArray(deviceID, nameValues);
	send(port, sysex);
    }

    /**
     * Convenience method for sending a sysex message. Static so can
     * be accessed from non-class methods.
     *
     * @param port MIDI output port number
     * @param sysex an array of sysex byte data
     * <code>SysexHandler.send(getPort(), sysex)</code>.
     * @Xdeprecated use <code>Driver.send(sysex)</code>.
     */
    public static void send(int port, byte[] sysex) {
	try {
	    ErrorMsg.reportStatus("static SysexHandler->send | port: " + port, sysex);
	    SysexMessage[] a = MidiUtil.byteArrayToSysexMessages(sysex);
	    for (int i = 0; i < a.length; i++)
		MidiUtil.send(MidiUtil.getReceiver(port), a[i]);
	} catch (InvalidMidiDataException e) {
	    ErrorMsg.reportStatus(e);
	} catch (MidiUnavailableException e) {
	    ErrorMsg.reportStatus(e);
	}
    }
} // End Class: SysexHandler
