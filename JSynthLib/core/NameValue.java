package core;

/**
 * Use SysexHandler.NameValue instead of this class. This class exists only for
 * backward compatibility.
 * @deprecated Use SysexHandler.NameValue instead of this class.
 * @see SysexHandler
 * @see SysexHandler.NameValue
 */
public class NameValue extends SysexHandler.NameValue {
    /**
     * Creates a new <code>NameValue</code> instance.
     * 
     * @param sName
     *            a <code>String</code> value
     * @param value
     *            an <code>int</code> value
     */
    public NameValue(String sName, int value) {
        super(sName, value);
    }
}