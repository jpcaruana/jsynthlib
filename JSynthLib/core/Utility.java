package core;
import java.io.ByteArrayOutputStream;

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

} // End Class: Utility
