package core;

import java.awt.datatransfer.Transferable;
import java.io.Serializable;

import javax.sound.midi.SysexMessage;

// Is Transferable necessary?
public interface IPatch extends Cloneable, Transferable, Serializable {

    /**
     * Guess and set Driver for the patch by using
     * {@link IDriver#supportsPatch IDriver.supportsPatch}method.
     * 
     * @return <code>true</code> if a driver is found, <code>false</code>
     *         otherwise.
     */
    boolean chooseDriver();

    /** Getter for date. */
    String getDate();

    /** Setter for date. */
    void setDate(String date);

    /** Getter for author of the patch. */
    String getAuthor();

    /** Setter for author of the patch. */
    void setAuthor(String author);

    /** Getter for comment. */
    String getComment();

    /** Setter for comment. */
    void setComment(String comment);

    /** Return Device for this patch. */
    Device getDevice();

    /** Return Driver for this patch. */
    IPatchDriver getDriver();

    /** Set driver. */
    void setDriver(IPatchDriver driver);

    /**
     * Return a hexadecimal string for
     * {@link IDriver#supportsPatch IDriver.suppportsPatch}at most 16 byte sysex
     * data.
     * 
     * @see IDriver#supportsPatch
     */
    String getPatchHeader();

    /**
     * Returns the patch's name. This is not necessarily the name stored on the
     * synth.
     */
    String getName();

    /** Set the patches name. */
    void setName(String name);

    /** calculate checksum of the patch. */
    void calculateChecksum();
    
    /**
     * Returns a Patch Editor Window for this Patch. Returns <code>null</code>
     * if there is no editor.
     * XXX throw an Exception?
     */
    JSLFrame edit();

    /**
     * Sends a patch to a set location on a synth.
     */
    void store(int bankNum, int patchNum);

    // only for Single Patch (Do we need ISinglePatch?)
    /** Play note. */
    void play();

    /**
     * Sends a patch to the synth's edit buffer.<p>
     */
    void send();
    // end of Single Patch
    
    // only for Bank Patch (Do we need IBankPatch?)
    /**
     * Check a patch if it is for the bank patch and put it into the
     * bank.
     */
    void put(IPatch singlePatch, int patchNum);

    /** Delete a patch. */
    void delete(int patchNum);

    /** Gets a patch from the bank, converting it as needed. */
    IPatch get(int patchNum);

    /** Get the name of the patch at the given number <code>patchNum</code>. */
    String getName(int patchNum);

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    void setName(int patchNum, String name);
    // end of Bank Patch

    /** Get an array of sysex messages representing this patch. */
    SysexMessage[] getMessages();

    /** Get a byte array representing this patch. */
    byte[] getByteArray();

    /**
     * Change this patch to contain the same data as p. Used for backing up
     * edited patches.
     * 
     * @param p
     *            Patch whose data we should use.
     */
    void useSysexFromPatch(IPatch p);

    Object clone();
}