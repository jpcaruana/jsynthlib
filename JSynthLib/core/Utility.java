package core;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import javax.swing.JDialog;

/**
 * Various utility functions.
 * @author phil@muqus.com - 07/2001
 * @author Hiroo Hayashi
 * @version $Id$
 */
public class Utility extends Object {

    // don't have to call constructor for Utility class.
    private Utility() {
    }

    //----- Start phil@muqus.com
    /**
     * Returns byte[] resulting from deleting deleteLength bytes from
     * src at srcOffset.
     */
    public static byte[] byteArrayDelete (byte[] src, int srcOffset, int deleteLength) {
	ByteArrayOutputStream os = new ByteArrayOutputStream(src.length - deleteLength);

	os.write(src, 0, srcOffset);
	os.write(src, srcOffset + deleteLength, src.length - (srcOffset + deleteLength));
	return os.toByteArray();
    }

    /**
     * Returns byte[] resulting from inserting insertLength bytes into
     * src at srcOffset.
     */
    public static byte[] byteArrayInsert (byte[] src, int srcOffset,
					  byte[] insert, int insertOffset, int insertLength) {
	ByteArrayOutputStream os = new ByteArrayOutputStream(src.length + insertLength);

	os.write(src, 0, srcOffset);
	os.write(insert, insertOffset, insertLength);
	os.write(src, srcOffset, src.length - srcOffset);
	return os.toByteArray();
    }

    /**
     * Returns byte[] array resulting from inserting insertLength
     * bytes into src at srcOffset, replacing replaceLength bytes.
     */
    public static byte[] byteArrayReplace (byte[] src, int srcOffset, int replaceLength,
					   byte[] insert, int insertOffset, int insertLength) {
	ByteArrayOutputStream os = new ByteArrayOutputStream(src.length + insertLength - replaceLength);

	os.write(src, 0, srcOffset);
	os.write(insert, insertOffset, insertLength);
	os.write(src, srcOffset + replaceLength, src.length - (srcOffset + replaceLength));
	return os.toByteArray();
    }
    //----- End phil@muqus.com

    //----- Start Hiroo Hayashi
    //
    // convert MidiMessage or byte array into String
    //
    /**
     * convert a byte array into a hexa-dump string.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of bytes per line.  If equal or less than
     * 0, no newlines are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes) {
	StringBuffer buf = new StringBuffer();
	if (len == -1 || offset + len > d.length)
	    len = d.length - offset;
	for (int i = 0; i < len; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c));
	    if (bytes > 0
		&& (i % bytes == bytes - 1 && i != len - 1))
		buf.append("\n  ");
	    else if (i != len - 1)
		buf.append(" ");
	}
	return buf.toString();
    }

    /**
     * convert a byte array into a one-line hexa-dump string.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of columns (bytes) per line.
     * @return hexa-dump string.
     */
    public static String hexDumpOneLine(byte[] d, int offset, int len, int bytes) {
	if (len == -1 || len > d.length - offset)
	    len = d.length - offset;

	if (len <= bytes || len < 8)
	    return hexDump(d, offset, len, 0);

	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < bytes - 4; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c) + " ");
	}
	buf.append("..");
	for (int i = len - 3; i < len; i++) {
	    int c = (int) (d[offset + i] & 0xff);
	    buf.append(" ");
	    if (c < 0x10)
		buf.append("0");
	    buf.append(Integer.toHexString(c));
	}
	return buf.toString();
    }
    //----- End Hiroo Hayashi

    /**
     * Place a JDialog window to the center of computer screen.
     */
    static void centerDialog (JDialog dialog) {
        Dimension screenSize = dialog.getToolkit().getScreenSize();
        Dimension size = dialog.getSize();
        screenSize.height = screenSize.height / 2;
        screenSize.width = screenSize.width / 2;
        size.height = size.height / 2;
        size.width = size.width / 2;
        int y = screenSize.height - size.height;
        int x = screenSize.width - size.width;
        dialog.setLocation(x, y);
    }

    // moved from AppConfig.java
    /** Returns the "os.name" system property. */
    //- emenaker 2003.03.13
    public static String getOSName() {
	return (getSystemProperty("os.name"));
    }

    /** Returns the "os.version" system property. */
    public static String getOSVersion() {
	return (getSystemProperty("os.version"));
    }
   
    /** Returns the "java.specification.version" system property. */
    // - emenaker 2003.03.13
    public static String getJavaSpecVersion() {
	return (getSystemProperty("java.specification.version"));
    }

    /** Returns the "java.version" system property. */
    public static String getJavaVersion() {
	return (getSystemProperty("java.version"));
    }

    /** Looks up a system property and returns "" on exceptions. */
    private static String getSystemProperty(String key) {
	try {
	    return (System.getProperty(key));
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return ("");
	}
    }

} // End Class: Utility
