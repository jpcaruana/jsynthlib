package core;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;

public abstract class PatchTransferHandler extends TransferHandler {
    public static final DataFlavor PATCH_FLAVOR = 
	new DataFlavor(Patch[].class, "Patch Array");
    protected static final DataFlavor[] flavors = {
	PATCH_FLAVOR,
    };

    protected abstract Patch getSelectedPatch(JComponent c);
    protected abstract boolean storePatch(Patch p, JComponent c);
		
    protected Transferable createTransferable(JComponent c) {
	Patch p = getSelectedPatch(c);
	if (p == null)
	    return null;

	return new PatchTransferable(p);
    }
		
    public int getSourceActions(JComponent c) {
	return COPY;
    }
		
    public boolean importData(JComponent c, Transferable t) {
	if (canImport(c, t.getTransferDataFlavors())) {
	    try {
		Patch p = (Patch)t.getTransferData(PATCH_FLAVOR);
		if (p == null)
		    return false;
		return storePatch(p, c);
	    } catch (UnsupportedFlavorException ufe) {
	    } catch (IOException ioe) {
	    }
	}
	return false;
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	for (int i = 0; i < flavors.length; i++) {
	    if (PATCH_FLAVOR.match(flavors[i])) {
		return true;
	    }
	}
	return false;
    }

    /* Transferable containing patch and it's transient information. */
    protected class PatchTransferable implements Transferable, Serializable {
	protected Patch patch;

	public PatchTransferable(Patch p) {
	    patch = p;
	}

	public Object getTransferData(DataFlavor df) {
	    if (!df.match(flavors[0]))
		return null;
	    return patch;
	}

	public boolean isDataFlavorSupported(DataFlavor df) {
	    return df.match(flavors[0]);
	}
	public DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	}
    }
}

class PatchListTransferHandler extends PatchTransferHandler {
    protected Patch getSelectedPatch(JComponent c) {
	try {
	    JTable t = (JTable)c;
	    AbstractPatchListModel m = 
		(AbstractPatchListModel)t.getModel();
	    return m.getPatchAt(t.getSelectedRow());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }
    protected boolean storePatch(Patch p, JComponent c) {
	try {
	    p.chooseDriver();
	    JTable t = (JTable)c;
	    AbstractPatchListModel m =
		(AbstractPatchListModel)t.getModel();
	    m.addPatch(p);
	    return true;
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return false;
	}
    }
}

class PatchGridTransferHandler extends PatchTransferHandler {
    protected Patch getSelectedPatch(JComponent c) {
	try {
	    JTable t = (JTable)c;
	    PatchGridModel m = 
		(PatchGridModel)t.getModel();
	    return m.getPatchAt(t.getSelectedRow(), t.getSelectedColumn());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }
    protected boolean storePatch(Patch p, JComponent c) {
	try {
	    p.chooseDriver();
	    JTable t = (JTable)c;
	    PatchGridModel m =
		(PatchGridModel)t.getModel();
	    m.setPatchAt(p, t.getSelectedRow(), t.getSelectedColumn());
	    return true;
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return false;
	}
    }
}