package core;

import java.awt.datatransfer.Transferable;
import java.io.Serializable;

import javax.sound.midi.SysexMessage;
// Is Transferable necessary?
public interface IPatch extends Cloneable, Transferable, Serializable {

	/**
	 * Set <code>deviceNum</code> and <code>driverNum</code> field by
	 * guessing from <code>sysex</code> by using
	 * <code>Driver.suportsPatch</code> method.
	 * @return <code>true</code> if a driver is found,
	 * <code>false</code> otherwise.
	 */
	public boolean chooseDriver();
	/** Getter for property date. */
	public String getDate();
	/** Setter for property date. */
	public void setDate(String date);
	/** Getter for property author. */
	public String getAuthor();
	/** Setter for property author. */
	public void setAuthor(String author);
	/** Getter for property comment. */
	public String getComment();
	/** Setter for property comment. */
	public void setComment(String comment);
	/** Return Device for this patch. */
	public Device getDevice();
	/** Return Driver for this patch. */
	public Driver getDriver();
	/** Set driver. */
	public void setDriver(Driver driver);
	public IPatch[] dissect();
	/**
	 * Return a hexadecimal string for Driver.supportsPatch at most 16
	 * byte sysex data.
	 * @see Driver#supportsPatch
	 */
	public StringBuffer getPatchHeader();
	public String getName();
	public void setName(String name);
	public SysexMessage[] getMessages();
	
	public Object clone();
	public void useSysexFromPatch(IPatch p);
}