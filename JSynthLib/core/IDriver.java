package core;

/**
 * This is an interface for Device.driverList. ÊAll of drivers (single
 * driver, bank driver, converter) implement this.
 * 
 * @author ribrdb
 */
public interface IDriver {
    /** Getter for property <code>patchType</code>. */
    public abstract String getPatchType();

    /** Getter for property <code>getAuthors</code>. */
    public abstract String getAuthors();

    /**
     * Compares the header & size of a Patch to this driver to see if
     * this driver is the correct one to support the patch.
     *
     * @param patchString the result of <code>p.getPatchHeader()</code>.
     * @param p a <code>Patch</code> value
     * @return <code>true</code> if this driver supports the Patch.
     * @see #patchSize
     * @see #sysexID
     */
    public abstract boolean supportsPatch(StringBuffer patchString, IPatch p);
    
    /** Setter for property <code>device</code>. */
    public abstract void setDevice(Device d);
    
    /** Getter for property <code>device</code>. */
    public abstract Device getDevice();

}