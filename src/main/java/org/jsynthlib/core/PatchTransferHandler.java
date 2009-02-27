package org.jsynthlib.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.*;

public abstract class PatchTransferHandler extends TransferHandler {
    public static final DataFlavor PATCHES_FLAVOR =
        new DataFlavor(PatchesAndScenes.class, "Patch Array");
//        new DataFlavor(IPatch[].class, "Patch Array");

    public static final DataFlavor PATCH_FLAVOR =
        new DataFlavor(IPatch[].class, "Patch Array");

    public static final DataFlavor SCENE_FLAVOR =
        new DataFlavor(Scene[].class, "Scene Array");

    public static final DataFlavor TEXT_FLAVOR =
        new DataFlavor(String.class, "String");

    private DataFlavor[] flavorsAccepted = new DataFlavor[] {
        PATCHES_FLAVOR,
        PATCH_FLAVOR,
        SCENE_FLAVOR,
        TEXT_FLAVOR,
    };

    protected abstract boolean storePatch(IPatch p, JComponent c);

    protected boolean storeScene(Scene s, JComponent c) {
        // Default behavior is to just get the patch data
        return storePatch(s.getPatch(), c);
    }

    public int getSourceActions(JComponent c) {
        return COPY;
    }

    protected Transferable createTransferable(JComponent c) {
        PatchesAndScenes patchesAndScenes = new PatchesAndScenes();
        if(c instanceof JTable) {
            JTable table = (JTable) c;
            AbstractLibraryFrame.PatchTableModel pm = (AbstractLibraryFrame.PatchTableModel) table.getModel();
            int[] rowIdxs = table.getSelectedRows();
            for(int i=0; i<rowIdxs.length; i++) {
                IPatch patch = pm.getPatchAt(rowIdxs[i]);
                patchesAndScenes.add(patch);
            }
        } else {
            ErrorMsg.reportStatus("PatchTransferHandler.createTransferable doesn't recognize the component it was given");
        }
        return(patchesAndScenes);
    }

    // Used by LibraryFrame and BankEditorFrame.
    // SceneFrame overrides this.
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                if (t.isDataFlavorSupported(PATCHES_FLAVOR)) {
                    Vector patches = (Vector) t.getTransferData(PATCHES_FLAVOR);
                    for(int i=0; i<patches.size(); i++) {
                        Object obj = patches.elementAt(i);
                        if(obj instanceof IPatch) {
                            IPatch patch = (IPatch) obj;
                            /**
                             * Once we get the patch, we need to clone it for the recipient of the paste.
                             * Otherwise, it would be possible for the user to make multiple pastes from
                             * a single cut/copy and each window could be altering the *same* object.
                             * - Emenaker - 2006-02-26
                             */
                            ErrorMsg.reportStatus("Cloning: " + patch);
                            IPatch newPatch = (IPatch) patch.clone();
                            // Serialization loses a transient field, driver.
                            newPatch.setDriver();
                            if(! storePatch(newPatch, c)) {
                                return(false);
                            }
                            continue; // for(int i=0; i<patches.size(); i++)
                        }

                        if(obj instanceof Scene) {
                            Scene scene = (Scene) obj;
                            /**
                             * Once we get the patch, we need to clone it for the recipient of the paste.
                             * Otherwise, it would be possible for the user to make multiple pastes from
                             * a single cut/copy and each window could be altering the *same* object.
                             * - Emenaker - 2006-02-26
                             */
                            ErrorMsg.reportStatus("Cloning: " + scene);
                            Scene newScene = (Scene) scene.clone();
                            // Serialization loses a transient field, driver.
                            if(! storeScene(newScene, c)) {
                                return(false);
                            }
                            continue; // for(int i=0; i<patches.size(); i++)
                        }
                        ErrorMsg.reportStatus("PatchTransferHandler.importData was passed an unrecognized object: " + obj);
                        continue; // for(int i=0; i<patches.size(); i++)
                    }
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

    public boolean canImport(JComponent c, DataFlavor[] flavorsOffered) {
        for (int i = 0; i < flavorsOffered.length; i++) {
            //ErrorMsg.reportStatus("PatchTransferHandler.canImport(" + flavorsOffered[i].getMimeType() + ")");
            //ErrorMsg.reportStatus(TEXT_FLAVOR.getMimeType());
            for(int j=0; j < flavorsAccepted.length; j++) {
                if (flavorsAccepted[j].match(flavorsOffered[i])) {
                    //ErrorMsg.reportStatus("PatchTransferHandler CAN import");
                    return true;
                }
            }
        }
        //ErrorMsg.reportStatus("PatchTransferHandler can't import");
        return false;
    }

    /* Enable paste action when copying to clipboard. */
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        super.exportToClipboard(comp, clip, action);
        Actions.setEnabled(true, Actions.EN_PASTE);
    }
}

