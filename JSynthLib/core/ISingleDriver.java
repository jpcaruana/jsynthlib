package core;

/**
 * This includes methods only for Single Drivers.
 * @author ribrdb
 * @version $Id$
 * @see IDriver
 * @see IPatchDriver
 */
public interface ISingleDriver extends IPatchDriver {
    /** 
     * Play a note.
     * @param patch a <code>Patch</code>
     */
    void play(IPatch patch);

    /**
     * Sends a patch to the synth's edit buffer.
     * <p>
     * Override this in the subclass if parameters or warnings need to be sent
     * to the user (aka if the particular synth does not have a edit buffer or
     * it is not MIDI accessable). Checksum must be calculated by this method.
     */
    void send(IPatch patch);
}
