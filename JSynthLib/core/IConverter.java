package core;

/**
 * This is an interface of a driver which simply converts a patch, which is
 * imported from a file or MIDI input, into it's associated with to another
 * format.
 * 
 * @see IDriver
 * @see Device#addDriver(IDriver)
 * @version $Id$
 */
public interface IConverter extends IDriver {
    // no methods
}
