package core;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class ClipboardUtil implements ClipboardOwner {
    protected final static ClipboardUtil instance = new ClipboardUtil();
    protected final static Clipboard c =
	Toolkit.getDefaultToolkit().getSystemClipboard();

    public static void storePatch(Patch p) {
	try {
	    c.setContents(p, instance);
	} catch (IllegalStateException e) {}
    }

    public static Patch getPatch() {
	try {
	    Transferable t = c.getContents(instance);
	    return (Patch)t.getTransferData(t.getTransferDataFlavors()[0]);
	}
	catch (IllegalStateException e) {}
	catch (ClassCastException e) {}
	catch (UnsupportedFlavorException e) {}
	catch (java.io.IOException e) {}
	return null;
    }

    public void lostOwnership(Clipboard clipboard,  Transferable contents) {}

}