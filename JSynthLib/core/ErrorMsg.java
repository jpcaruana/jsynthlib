package core;
import javax.swing.*;

/**
 * This class handles error conditions and debug messages. It shows
 * error message dialogs to the user and can also log debug
 * information if a flag is set.<p>
 *
 * The Meaning for the <code>debug</code> variable flag is: (Each
 * number does all of preceding as well.)<p>
 * <pre>
 * 0 = No Debugging Info at all
 * 1 = Just print Stack Trace for Exceptions
 * 2 = Print Debug Status Messages & Error Messages
 * 3 = Print Stack Trace for all for non-exception Errors
 * </pre>
 * @author ???
 * @version $Id$
 */
public class ErrorMsg {
    /** @see ErrorMsg */
    public static int debug;

    /**
     * Report an error.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     */
    public static void reportError(String errorTitle, String errorMSG) {
// 	JOptionPane.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
// 				      errorMSG, errorTitle, JOptionPane.ERROR_MESSAGE);
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Report an error.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     * @param e an <code>Exception</code> value
     */
    public static void reportError(String errorTitle, String errorMSG,
				   Exception e) {
// 	JOptionPane.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
// 				      errorMSG, errorTitle, JOptionPane.ERROR_MESSAGE);
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE, e);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if (debug > 0) {
	    System.out.println("ERR> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Report a warning.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     */
    public static void reportWarning(String errorTitle, String errorMSG) {
// 	JOptionPane.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
// 				      errorMSG, errorTitle, JOptionPane.WARNING_MESSAGE);
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if (debug > 1)
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Report a warning.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     * @param e an <code>Exception</code> value
     */
    public static void reportWarning(String errorTitle, String errorMSG,
				     Exception e) {
// 	JOptionPane.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
// 				      errorMSG, errorTitle, JOptionPane.WARNING_MESSAGE);
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if (debug > 1)
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	if (debug > 0) {
	    System.out.println("WRN> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Report a debug message.
     *
     * @param msg a <code>String</code> value
     */
    public static void reportStatus(String msg) {
	if (debug > 1)
	    System.out.println("DBG>" + msg);
    }

    /**
     * Report a debug message.
     *
     * @param e an <code>Exception</code> value
     */
    public static void reportStatus(Exception e) {
	if (debug > 0) {
	    System.out.println("DBG> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
    }

    //----- Start phil@muqus.com

    /**
     * Output byte array as a pretty printed hex dump.
     *
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(byte[] data) {
	reportStatus(null, data);
    }

    /**
     * Output a debug message and byte array as a pretty printed hex
     * dump.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(String sMsg, byte[] data) {
	if (debug < 2)
	    return;

	if (sMsg != null)
	    reportStatus(sMsg);

	//===== Output Hex dump
	for (int i = 0; i < data.length; i++) {
	    String sHex = Integer.toHexString((int) (data[i] & 0xFF));
	    System.out.print(((sHex.length() == 1) ? "0" : "") + sHex);
	    if (i % 20 == 19)
		System.out.println();
	    else
		System.out.print((i % 10 == 9) ? "   " : " ");
	}
	System.out.println();
    }

    /**
     * Output a debug message and byte array as a pretty printed hex
     * dump.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     * @param offset an index of <code>data</code> from which hex dump
     * starts.
     * @param len the length of hex dump.
     */
    public static void reportStatus(String sMsg,
				    byte[] data, int offset, int len) {
	byte[] subData = new byte[len];
	System.arraycopy(data, offset, subData, 0, len);
	reportStatus(sMsg, subData);
    }
    //----- End phil@muqus.com
}
