package core;

/**
 * This is an interface of Driver which simply converts a patch, which is
 * imported from a file or MIDI input, into it's associated with to another
 * format.
 * <p>
 * 
 * The Driver supporting the new Driver format must come later in the Driver
 * Chain.
 * 
 * @see Device#addDriver(IDriver)
 * @version $Id$
 */
public interface IConverter extends IDriver {
    /**
     * Convert a Patch into an array of Patches.
     * @see IPatch#dissect() 
     */
    IPatch[] extractPatch(IPatch p);
}
