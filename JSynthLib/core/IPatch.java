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
	public IPatchDriver getDriver();
	/** Set driver. */
	public void setDriver(IPatchDriver driver);
    /**
     * Dissect an <code>IPatch</code> which has a <code>Converter</code>
     * driver into an array of <code>IPatch</code>.  Each patch in the
     * original patch must be for a same Device, but may be for some
     * different Drivers.
     *
     * @return a <code>Patch[]</code> value
     * @see Converter
     */
	public IPatch[] dissect();
	/**
	 * Return a hexadecimal string for Driver.supportsPatch at most 16
	 * byte sysex data.
	 * @see IDriver#supportsPatch
	 */
	public StringBuffer getPatchHeader();
	/**
	 * Returns the patch's name. This is not necessarily the name
	 * stored on the synth.
	 */
	public String getName();
	/** Set the patches name. */
	public void setName(String name);
	/** Get an array of sysex messages representing this patch. */
	public SysexMessage[] getMessages();
	/** Get a byte array representing this patch. */
	public byte[] getByteArray();
	/** Change this patch to contain the same data as p.
	 * Used for backing up edited patches.
	 * @param p Patch whose data we should use.
	 */
	public void useSysexFromPatch(IPatch p);
	
	public Object clone();
}