package core;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
public abstract class PatchTransferHandler extends TransferHandler {
    public static final DataFlavor PATCH_FLAVOR = 
	new DataFlavor(Patch[].class, "Patch Array");
    public static final DataFlavor TEXT_FLAVOR =
	new DataFlavor("application/x-java-serialized-object; class=java.lang.String","string");
    protected static final DataFlavor[] flavors = {
	PATCH_FLAVOR,TEXT_FLAVOR
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
		    {
			String s = (String)t.getTransferData(TEXT_FLAVOR);
			p = getPatchFromUrl(s);
			if (p==null) 
			    return false;			
		    }
		return storePatch(p, c);
	    } catch (UnsupportedFlavorException ufe) {
	    } catch (IOException ioe) {
	    }
       
	    try{
		String s = (String)t.getTransferData(TEXT_FLAVOR);
		Patch p = getPatchFromUrl(s);
		if (p==null) 
		    return false;	
		else return storePatch(p,c);
	    
	} catch (UnsupportedFlavorException ufe) {
	} catch (IOException ioe) {
	}
    }
    return false;
    }
    
    public Patch getPatchFromUrl(String s)
    {

	try {
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
	    Patch p = new Patch(sysex);
	    return p;

	}catch (MalformedURLException e) {System.out.println("Malformed URL");}
	catch (IOException ioe) {System.out.println("Network I/O Error");}
	


	return null;
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	for (int i = 0; i < flavors.length; i++) {
	    if (PATCH_FLAVOR.match(flavors[i])) {
		return true;
	    }
	    if (TEXT_FLAVOR.match(flavors[i])) {
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
	    System.out.println(df.getMimeType());

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
