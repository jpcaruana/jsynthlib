package core;

/**
 * This includes methods only for Single Drivers.
 * @author ribrdb
 */
public interface ISingleDriver extends IPatchDriver {
    /** Play note. 
     * @param patch a <code>Patch</code> value, which isn't used! !!!FIXIT!!!
     * @Xdeprecated Use playPatch().
     */
    public abstract void playPatch(IPatch patch);

    /**
     * Sends a patch to the synth's edit buffer.<p>
     *
     * Override this in the subclass if parameters or warnings need to
     * be sent to the user (aka if the particular synth does not have
     * a edit buffer or it is not MIDI accessable).
     */
    public abstract void sendPatch(IPatch patch);
}