package core;

/**
 * This is an interface for Device.driverList. All of drivers (single driver,
 * bank driver, and converter) implement this.
 * 
 * @author ribrdb
 * @version $Id$
 * @see IPatchDriver
 * @see ISingleDriver
 * @see IBankDriver
 * @see IConverter
 */
public interface IDriver {
    /**
     * return type of patch which the driver handles. eg. "Single", "Bank",
     * "Drumkit", etc.
     */
    String getPatchType();

    /** return the names of the authors of this driver. */
    String getAuthors();

    /** Set <code>Device</code> with which this driver go. */
    void setDevice(Device device);

    /** Return <code>Device</code> with which this driver go.. */
    Device getDevice();

    /**
     * Compares the header & size of a Patch to this driver to see if this
     * driver is the correct one to support the patch.
     *
     * @param patchString
     *            the result of
     *            {@link IPatch#getPatchHeader() IPatch.getPatchHeader()}.
     * @param sysex
     *            a byte array of sysex message
     * @return <code>true</code> if this driver supports the Patch.
     */
    boolean supportsPatch(String patchString, byte[] sysex);

    /**
     * Create a patch from a byte array for the driver.
     * @param sysex a byte array of sysex data.
     * @return a array of <code>IPatch</code> object.
     * @see DriverUtil#createPatch(byte[])
     */
    // called by Patch.valueOf and CrossBreeder.generateNewPatch
    IPatch[] createPatch(byte[] sysex);
}
