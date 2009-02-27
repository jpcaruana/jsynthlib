package org.jsynthlib.core;
import java.awt.datatransfer.*;
import javax.swing.*;

public class ProxyImportHandler extends TransferHandler {
    protected JComponent comp;
    protected TransferHandler proxy;

    public ProxyImportHandler(JComponent c, TransferHandler th) {
	comp = c;
	proxy = th;
    }

    public boolean importData(JComponent c, Transferable t) {
	return proxy.importData(comp, t);
    }

    public int getSourceActions(JComponent c) {
	return NONE;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	return proxy.canImport(comp, flavors);
    }

}