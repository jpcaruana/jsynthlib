package core;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
public abstract class PatchTransferHandler extends TransferHandler {
    public static final DataFlavor PATCH_FLAVOR = 
	new DataFlavor(IPatch[].class, "Patch Array");
    public static final DataFlavor TEXT_FLAVOR =
	new DataFlavor("application/x-java-serialized-object; class=java.lang.String","string");
    protected static final DataFlavor[] flavors = {
	PATCH_FLAVOR,TEXT_FLAVOR
    };

    protected abstract IPatch getSelectedPatch(JComponent c);
    protected abstract boolean storePatch(IPatch p, JComponent c);
		
    protected Transferable createTransferable(JComponent c) {

	IPatch p = getSelectedPatch(c);
	if (p == null)
	    return null;

	return new PatchTransferable(p);
    }

    public int getSourceActions(JComponent c) {
	return COPY;
    }

    public boolean importData(JComponent c, Transferable t){
	if (canImport(c, t.getTransferDataFlavors())) {
	    try {
		IPatch p = (IPatch)t.getTransferData(PATCH_FLAVOR);
		
		if (p != null)
		    return storePatch(p, c);
	    } catch (UnsupportedFlavorException ufe) {
	    } catch (Exception ioe) {
	    }

	    try{
		String s = (String)t.getTransferData(TEXT_FLAVOR);
		IPatch p = getPatchFromUrl(s);
		if (p!=null) 
		    return storePatch(p,c);

	    } catch (UnsupportedFlavorException ufe) {
	    } catch (IOException ioe) {
	    }
	}
	// Let user know we tried to paste.
   	Toolkit.getDefaultToolkit().beep();
	return false;
    }
    
    public IPatch getPatchFromUrl(String s)
    {

	try {
	    ErrorMsg.reportStatus("S = " + s);
	    URL u = new URL(s);
	    InputStream in = u.openStream();
	    int b; int i=0;
	    byte []buff = new byte[65536];
	    do
	      {
		b = in.read();
		if (b!=-1)
		  {
		    buff[i]=(byte)b;
		    i++;
		  }
	      }
	    while (b!=-1 && i<65535);
	    byte []sysex = new byte[i];
	    System.arraycopy(buff,0,sysex,0,i);
	in.close();
		// XXX: Factory
	    IPatch p = new Patch(sysex);
	    return p;

	}catch (MalformedURLException e) {ErrorMsg.reportStatus("Malformed URL");}
	catch (IOException ioe) {ErrorMsg.reportStatus("Network I/O Error");}



	return null;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	for (int i = 0; i < flavors.length; i++) {
	   //ErrorMsg.reportStatus(flavors[i].getMimeType());
	   //ErrorMsg.reportStatus(TEXT_FLAVOR.getMimeType());
	   if (PATCH_FLAVOR.match(flavors[i])) {
		return true;
	    }
	    if (TEXT_FLAVOR.match(flavors[i])) {
		return true;
	    }
	}
	return false;
    }

    /* Enable  paste action when copying to clipboard. */
    public void exportToClipboard(JComponent comp, Clipboard clip, int action){
	super.exportToClipboard(comp, clip, action);
	Actions.setEnabled(true, Actions.EN_PASTE);
    }

    /* Transferable containing patch and it's transient information. */
    protected class PatchTransferable implements Transferable, Serializable {
	protected IPatch patch;

	public PatchTransferable(IPatch p) {
	    patch = p;
	}

	public Object getTransferData(DataFlavor df) {

	    if (df.match(flavors[0]))
		return patch;

	    return null;
	}

	public boolean isDataFlavorSupported(DataFlavor df) {
	    return df.match(flavors[0]);
	}
	public DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	}
    }
}

