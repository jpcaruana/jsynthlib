package core;

/**
 * This is an interface for Device.driverList. ÊAll of drivers (single driver,
 * bank driver, converter) implement this.
 * 
 * @author ribrdb
 */
public interface IDriver {
    /**
     * return type of patch which the driver handles. eg. "Single", "Bank",
     * "Drumkit", etc.
     */
    public abstract String getPatchType();

    /** return the names of the authors of this driver. */
    public abstract String getAuthors();

    /**
     * Compares the header & size of a Patch to this driver to see if this
     * driver is the correct one to support the patch.
     * 
     * @param patchString
     *            the result of
     *            {@link IPatch#getPatchHeader() IPatch.getPatchHeader()}.
     * @param patch
     *            a <code>Patch</code> value
     * @return <code>true</code> if this driver supports the Patch.
     */
    public abstract boolean supportsPatch(StringBuffer patchString, IPatch patch);

    /** Set <code>Device</code> with which this driver go. */
    public abstract void setDevice(Device device);

    /** Return <code>Device</code> with which this driver go.. */
    public abstract Device getDevice();
}