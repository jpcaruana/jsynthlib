package org.jsynthlib.core;
import javax.swing.*;

/**
 * This class handles error or warning conditions and debug messages.
 * <p>
 * 
 * <code>reportWarning</code> and <code>reportError</code> methods show
 * warning/error message dialogs to the users.
 * <p>
 * 
 * <code>reportStatus</code> methods show debug information to the console if
 * a <code>(debugLevel & mask)</code> is not equal to 0.
 * <code>debugLevel</code> is a bit mask specified by <code>-D</code>
 * command line option. <code>mask</code> is an argument of the
 * <code>reportStatus</code> method.  At this point the following bit masks are defined.
 * <p>
 * 
 * <pre>
 *      0x1 debug message
 *      0x2 Stack Trace
 *      0x4 MIDI debug message
 *      0x8 JSLFrame debug message
 * </pre>
 * 
 * In general <code>System.out.print</code> or <code>System.out.println</code>
 * should not be used in JSynthLib. An example of the exception is
 * <code>main</code> method for debugging.
 * 
 * @author ???
 * @version $Id$
 */
public class ErrorMsg {
    /** debug message level set by -D command line argument. */
    private static int debugLevel = 0;
    /** debug message. */
    static final int DEBUG_MSG		= 0x0001;
    /** show dump stack on Error and Warning message. */
    static final int DUMP_STACK		= 0x0002;
    /** show debug message for MIDI. */
    static final int MIDI		= 0x0004;
    /** show debug message for JSLFrame. */
    static final int FRAME		= 0x0008;

    /**
     * @param debugLevel The debug level to set.
     */
    static void setDebugLevel(int debugLevel) {
        ErrorMsg.debugLevel = debugLevel;
    }

    /**
     * Show a message in an error dialog window.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     */
    public static void reportError(String errorTitle, String errorMSG) {
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE);
	if ((debugLevel & DEBUG_MSG) != 0)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if ((debugLevel & DUMP_STACK) != 0)
	    Thread.dumpStack();
    }

    /**
     * Show a message in an error dialog window with an
     * <code>Exception</code> information.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     * @param e an <code>Exception</code> value
     */
    public static void reportError(String errorTitle, String errorMSG,
				   Exception e) {
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE, e);
	if ((debugLevel & DEBUG_MSG) != 0) {
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	    System.out.println("ERR> [Exception] " + e.getMessage());
	}
	if ((debugLevel & DUMP_STACK) != 0)
	    e.printStackTrace(System.out);
    }

    /**
     * Show a message in a warning dialog window.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     */
    public static void reportWarning(String errorTitle, String errorMSG) {
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if ((debugLevel & DEBUG_MSG) != 0)
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	if ((debugLevel & DUMP_STACK) != 0)
	    Thread.dumpStack();
    }

    /**
     * Show a message in a warning dialog window with an
     * <code>Exception</code> information.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     * @param e an <code>Exception</code> value
     */
    public static void reportWarning(String errorTitle, String errorMSG,
				     Exception e) {
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if ((debugLevel & DEBUG_MSG) != 0) {
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	    System.out.println("WRN> [Exception] " + e.getMessage());
	}
	if ((debugLevel & DUMP_STACK) != 0)
	    e.printStackTrace(System.out);
    }

    /**
     * Print a debug message to the console when
     * <code>(debugLevel & mask)</code> is not equal to 0.
     * 
     * @param mask
     *            debug level bit mask.
     * @param msg
     *            debug message string.
     */
    public static void reportStatus(int mask, String msg) {
	if ((debugLevel & mask) != 0)
	    System.out.println(msg);
    }

    /**
     * Print a debug message to the console.
     *
     * @param msg a <code>String</code> value
     */
    public static void reportStatus(String msg) {
        reportStatus(DEBUG_MSG, msg);
    }

    /**
     * Print an <code>Exception</code> information and/or the stack
     * trace to the console.
     *
     * @param e an <code>Exception</code> value
     */
    public static void reportStatus(Exception e) {
	if ((debugLevel & DEBUG_MSG) != 0)
	    System.out.println("[Exception] " + e.getMessage());
	if ((debugLevel & DUMP_STACK) != 0)
	    e.printStackTrace(System.out);
    }

    //----- Start phil@muqus.com

    /**
     * Print byte array as a pretty printed hex dump to the console.
     *
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(byte[] data) {
	reportStatus(null, data);
    }

    /**
     * Print a debug message and byte array as a pretty printed hex
     * dump to the console.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(String sMsg, byte[] data) {
	if (sMsg != null)
	    reportStatus(sMsg);

	if ((debugLevel & DEBUG_MSG) != 0)
	    reportStatus(Utility.hexDump(data, 0, data.length, 20));
    }

    /**
     * Print a debug message and byte array as a pretty printed hex
     * dump to the console.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     * @param offset an index of <code>data</code> from which hex dump
     * starts.
     * @param len the length of hex dump.
     */
    public static void reportStatus(String sMsg,
				    byte[] data, int offset, int len) {
        if ((debugLevel & DEBUG_MSG) != 0)
            reportStatus(Utility.hexDump(data, offset, len, 20));
    }
    //----- End phil@muqus.com
}
