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


    //----- Start Joe Emenaker
    /**
     * convert a byte array into a hexa-dump string, with or without spaces between the bytes.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of bytes per line.  If equal or less than
     * 0, no newlines are inserted.
     * @param wantspaces whether or not to insert spaces between bytes
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes, boolean wantspaces) {
    	StringBuffer buf = new StringBuffer();
	    if (len == -1 || offset + len > d.length)
    	    len = d.length - offset;
    	for (int i = 0; i < len; i++) {
	        int c = (d[offset + i] & 0xff);
	        if (c < 0x10)
                buf.append("0");
	        buf.append(Integer.toHexString(c));
	        if (bytes > 0 && (i % bytes == bytes - 1 && i != len - 1))
		        buf.append("\n");
	        else
                if (i != len - 1 && wantspaces)
		            buf.append(" ");
	    }
	    return buf.toString();
    }

    public static String hexDump(byte[] d, int offset, int len, int bytes, int addressLength, boolean characters) {
        String output = "";
        if(offset > d.length) {
            return("offset out of bounds of byte array");
        }
        if(offset + len > d.length) {
            len = d.length - offset;
        }

        if(addressLength > 0) {
            String addressstring = Integer.toHexString(offset);
            while(addressstring.length() < addressLength) {
                addressstring = "0" + addressstring;
            }
            output += addressstring + " - ";
        }
        String hexdigits = hexDump(d, offset, len, bytes, true);
        // If hexdigits is too short, pad with spaces so that the last line lines up with the others.
        if(characters) {
            while(hexdigits.length() < 3*bytes-1) {
                hexdigits += "   ";
            }
        }
        output += hexdigits;
        if(characters) {
            output += " - ";
            for(int i=offset; i<offset+len; i++) {
                if(d[i] >= 32 && d[i] <= 126) {
                    output += new Character((char)d[i]).toString();
                } else {
                    output += ".";
                }
            }
        }
        return(output);
    }

    /**
     * convert a byte array into a string of hex values, optionally including their character
     * equivalents and/or the address/offset of the beginning of the line. With character
     * equivalents, unprintable characters are replaced by spaces. For example:
     *     65 78 61 6D 70 6C 65 01 EF 31 38 F3 F1 - EXAMPLE..18..
     *
     * If <code>addresses</code> is true, then the hex representation of the offset will
     * be put at the beginning of each line. For example:
     *     013A0 - 65 78 61 6D 70 6C 65 01 - EXAMPLE.
     *     013A8 - EF 31 38 F3 F1 65 64 63 - .18..EDC
     *     013B0 - 38 34 36 37 38 31 37 37 - 84678177
     *
     * If <code>len</code> is positive non-zero, only <code>len</code> bytes will be
     * represented on each line.
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of columns (bytes) to put in each line. If less than
     * or equal to zero, no line-breaks are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes, boolean addresses, boolean characters) {
        // Is offset beyond the end of array?
        if(offset >= d.length) {
            return("");
        }
        // Is offset+len beyond the end of the array?
        if(offset+len >= d.length || len < 0) {
            len = d.length - offset; // Set len to get remaining bytes
        }
        // If bytes <=0, set it to len so that we get the remaining bytes on one line
        if(bytes <= 0) {
            bytes = len;
        }

        // How many digits to use for address?
        int addresslen = 0;
        // Keep adding 2 digits until we can repesent the highest number we need
        if(addresses) {
            int maxnumber = 1;
            while(maxnumber < offset+len) {
                addresslen += 2;
                maxnumber *= 256;
            }
        }

        String output = "";
        if(len <= bytes) {
            output = hexDump(d, offset, len, bytes, addresslen, characters);
        } else {
            // The bytes won't fit. Split them up and call hexDump for each one
            for(int i=0; i<len; i+=bytes) {
                output = output + hexDump(d, offset+i, bytes, bytes, addresslen, characters) + "\n";
            }
        }
        return(output);
    }

    //----- End Joe Emenaker

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
	    return hexDump(d,offset,len,bytes,true);
    }

    /**
     * convert a byte array into a one-line hexa-dump string. If it's longer than
     * <code>len</code>, it will have the inner portion removed and replaced with
     * dots, for example: "00 01 03 04 05 .. 7d 7e 7f"
     *
     * @param d a <code>byte[]</code> array to be converted.
     * @param offset array index from which dump starts.
     * @param len number of bytes to be dumped.  If -1, dumps to the
     * end of the array.
     * @param bytes number of columns (bytes) to put in the one-line string.
     * @return hexa-dump string.
     */
    public static String hexDumpOneLine(byte[] d, int offset, int len, int bytes) {
    	if (len == -1 || len > d.length - offset)
	        len = d.length - offset;

    	if (len <= bytes || len < 8)
	        return hexDump(d, offset, len, 0);

        return(hexDump(d,offset,bytes-4,0) + ".." + hexDump(d,offset + len - 3, 3, 0));
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

    /**
     * Revalidate Library. Internally this calls <code>revalidateDriver()</code>
     * method of each frame.
     */
    static void revalidateLibraries() {
        JSLFrame[] jList = PatchEdit.getDesktop().getAllFrames();
        if (jList.length > 0) {
            PatchEdit.showWaitDialog();
            for (int i = 0; i < jList.length; i++) {
                if (jList[i] instanceof LibraryFrame)
                    ((LibraryFrame) (jList[i])).revalidateDrivers();
                else if (jList[i] instanceof BankEditorFrame)
                    ((BankEditorFrame) (jList[i])).revalidateDriver();
                else if (jList[i] instanceof PatchEditorFrame)
                    ((PatchEditorFrame) (jList[i])).revalidateDriver();
            }
            PatchEdit.hideWaitDialog();
        }
    }

} // End Class: Utility
