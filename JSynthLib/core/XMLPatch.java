
package core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.midi.SysexMessage;

/**
 * @author ribrdb
 */
public class XMLPatch implements IPatch {
	private SysexMessage[] messages;
	private String date, author, comment;
	private String name;
	private transient HashMap parameters;
	private transient Driver driver;
	private transient Device device;
	private transient XMLDecoder decoder;
	
	/* (non-Javadoc)
	 * @see core.IPatch#chooseDriver()
	 */
	public boolean chooseDriver() {
		return false;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getDate()
	 */
	public String getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#setDate(java.lang.String)
	 */
	public void setDate(String _date) {
		date = _date;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getAuthor()
	 */
	public String getAuthor() {
		return author;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#setAuthor(java.lang.String)
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getDevice()
	 */
	public Device getDevice() {
		return device;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getDriver()
	 */
	public Driver getDriver() {
		return driver;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#setDriver(core.Driver)
	 */
	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#dissect()
	 */
	public IPatch[] dissect() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getPatchHeader()
	 */
	public StringBuffer getPatchHeader() {
		StringBuffer patchstring = new StringBuffer("F0");
		byte[] data = null;
		try {
			data = messages[0].getData();
		} catch (NullPointerException ex) {
			return null;
		}
		
		int end = Math.min(16, data.length);
		for (int i = 1; i < end; i++) {
		    if ((int) (data[i] & 0xff) < 0x10)
			patchstring.append("0");
		    patchstring.append(Integer.toHexString((int) (data[i] & 0xff)));
		}
		return patchstring;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see core.IPatch#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name= name; 
	}

	public SysexMessage[] getMessages() {
		return messages;
	}
	
	public byte[] getByteArray() {
		SysexMessage[] msgs = getMessages();
		if (msgs == null)
			return null;
		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
		for (int i = 0; i < msgs.length; i++)
			fileOut.write(msgs[i].getMessage(),0,msgs[i].getLength());
		return fileOut.toByteArray();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] df = { PatchTransferHandler.PATCH_FLAVOR };
        return df;
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return this;
	}

	public void setParameter(String message, String name, Object value) {
		decoder.setParameter(message, name, value);
	}
	public void setParameter(String group, String message, String name,
			Object value) {
		decoder.setParameter(group, message, name, value);
	}
	public Object getParameter(String name) {
		return decoder.getParameter(name);
	}
	public void setParameter(String name, Object value) {
		decoder.setParameter(name, value);
	}
	public Object getParameter(String group, String message, String name) {
		return decoder.getParameter(group, message, name);
	}
	public Object getParameter(String message, String name) {
		return decoder.getParameter(message, name);
	}
	public Object clone() {
		throw new RuntimeException("Unimplemented");
	}
	public void useSysexFromPatch(IPatch p) {
		if (p.getClass() != XMLPatch.class) {
			throw new IllegalArgumentException();
		}
		messages = ((XMLPatch)p).messages;
	}
}
