package core;

/**
 * This is an interface of a driver which simply converts a patch, which is
 * imported from a file or MIDI input, into it's associated with to another
 * format.
 * 
 * @version $Id$
 * @see IDriver
 */
public interface IConverter {
    /**
     * Create an array of patches from a byte array of SysexMessage for the
     * driver. Used for a byte array read from a Sysex file.
     * <p>
     * Converter class is an implementation of this class. It is an independent
     * driver implementing IDriver. An implementation of ISingleDriver (ex.
     * Driver class) may also implement this.
     * 
     * @param sysex
     *            a byte array of SysexMessage.
     * @return an array of <code>IPatch</code> value.
     * @see DriverUtil#createPatches(byte[])
     * @see DriverUtil#createPatches(byte[], Device)
     */
    IPatch[] createPatches(byte[] sysex);
}
