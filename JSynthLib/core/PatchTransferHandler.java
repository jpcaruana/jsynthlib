package core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public abstract class PatchTransferHandler extends TransferHandler {
    public static final DataFlavor PATCH_FLAVOR =
        new DataFlavor(IPatch[].class, "Patch Array");

    public static final DataFlavor SCENE_FLAVOR =
        new DataFlavor(Scene[].class, "Scene Array");

    public static final DataFlavor TEXT_FLAVOR =
        new DataFlavor(String.class, "String");

    protected static final DataFlavor[] flavors = {
            PATCH_FLAVOR, SCENE_FLAVOR, TEXT_FLAVOR
    };

    protected abstract boolean storePatch(IPatch p, JComponent c);

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    // Used by LibraryFrame and BankEditorFrame.
    // SceneFrame overrides this.
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                if (t.isDataFlavorSupported(PATCH_FLAVOR)) {
                    IPatch p = (IPatch) t.getTransferData(PATCH_FLAVOR);
                    // Serialization loses a transient field, driver.
                    p.setDriver();
                    return storePatch(p, c);
                } else if (t.isDataFlavorSupported(TEXT_FLAVOR)) {
                    String s = (String) t.getTransferData(TEXT_FLAVOR);
                    IPatch p = getPatchFromUrl(s);
                    if (p != null)
                        return storePatch(p, c);
                }
            } catch (UnsupportedFlavorException e) {
                ErrorMsg.reportStatus(e);
            } catch (IOException e) {
                ErrorMsg.reportStatus(e);
            }
        }
        // Let user know we tried to paste.
        Toolkit.getDefaultToolkit().beep();
        return false;
    }

    protected IPatch getPatchFromUrl(String s) {

        try {
            ErrorMsg.reportStatus("S = " + s);
            URL u = new URL(s);
            InputStream in = u.openStream();
            int b;
            int i = 0;
            byte[] buff = new byte[65536];
            do {
                b = in.read();
                if (b != -1) {
                    buff[i] = (byte) b;
                    i++;
                }
            } while (b != -1 && i < 65535);
            in.close();
            byte[] sysex = new byte[i];
            System.arraycopy(buff, 0, sysex, 0, i);
            return (DriverUtil.createPatch(sysex));
        } catch (MalformedURLException e) {
            ErrorMsg.reportError("Data Paste Error", "Malformed URL", e);
        } catch (IOException e) {
            ErrorMsg.reportError("Data Paste Error", "Network I/O Error", e);
        }
        return null;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            //ErrorMsg.reportStatus(flavors[i].getMimeType());
            //ErrorMsg.reportStatus(TEXT_FLAVOR.getMimeType());
            if (PATCH_FLAVOR.match(flavors[i])
                    || TEXT_FLAVOR.match(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    /* Enable paste action when copying to clipboard. */
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        super.exportToClipboard(comp, clip, action);
        Actions.setEnabled(true, Actions.EN_PASTE);
    }
}

