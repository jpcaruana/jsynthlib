// $Id$

/* This class handles error conditions and debug messages. It shows
   error message dialogs to the user and can also log debug
   information if a flag is set.

   The Meaning for the debug variable flag is: (Each # does all of
   preceding as well)

   0 = No Debugging Info at all
   1 = Just print Stack Trace for Exceptions
   2 = Print Debug Status Messages & Error Messages
   3 = Print Stack Trace for all for non-exception Errors
*/
package core;
import javax.swing.*;

public class ErrorMsg {
    /** are we in debugging mode? */
    public static int debug;

    public static void reportError(String errorTitle, String errorMSG) {
	//JOptionPane.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
	//errorMSG, errorTitle, JOptionPane.ERROR_MESSAGE);
	ErrorDialog.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    public static void reportError(String errorTitle, String errorMSG,
				   Exception e) {
	//JOptionPane.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
	//errorMSG, errorTitle, JOptionPane.ERROR_MESSAGE);
	ErrorDialog.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE, e);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	reportStatus(e);
    }

    public static void reportWarning(String errorTitle, String errorMSG) {
	//JOptionPane.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
	//errorMSG, errorTitle, JOptionPane.WARNING_MESSAGE);
        ErrorDialog.showMessageDialog(PatchEdit.instance/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if (debug > 1)
	    System.out.println("WAR> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    public static void reportStatus(String msg) {
	if (debug > 1)
	    System.out.println("DBG>" + msg);
    }

    public static void reportStatus(Exception e) {
	if (debug > 0) {
	    System.out.println("DBG> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
    }

    //----- Start phil@muqus.com

    //---------------------------------------------------------------------
    // ErrorMsg->reportStatus(byte[])
    // Notes: Output byte array as a pretty printed hex dump
    //---------------------------------------------------------------------
    public static void reportStatus(byte[] data) {
	reportStatus("No message", data);
    }

    //---------------------------------------------------------------------
    // ErrorMsg->reportStatus(String, byte[])
    //---------------------------------------------------------------------
    public static void reportStatus(String sMsg, byte[] data) {
	//===== Output Hex dump
	reportStatus(sMsg);

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

    //----------------------------------------------------------------------
    // ErrorMsg->reportStatus(String, byte[], int, int)
    //----------------------------------------------------------------------
    public static void reportStatus(String sMsg,
				    byte[] data, int offset, int len) {
	byte[] subData = new byte[len];
	System.arraycopy(data, offset, subData, 0, len);
	reportStatus(sMsg, subData);
    }
    //----- End phil@muqus.com
}
