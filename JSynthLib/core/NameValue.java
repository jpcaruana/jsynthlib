package core;
import java.io.*;

/**
 * Describe class <code>NameValue</code> here.
 *
 * @author phil@muqus.com - 07/2001
 * @version 1.0
 * @see SysexHandler
 */
public class NameValue extends Object implements Serializable {
    /** name of the value. */
    protected String sName;
    /** Value. */
    protected int value;

    /**
     * Creates a new <code>NameValue</code> instance.
     *
     * @param sName a <code>String</code> value
     * @param value an <code>int</code> value
     */
    public NameValue(String sName, int value) {
	this.setName(sName);
	this.setValue(value);
    }

    /** A getter of sName. */
    public String getName() {
	return this.sName;
    }

    /** A setter of sName. */
    public void setName(String sName) {
	this.sName = sName;
    }

    /** A getter of value. */
    public int getValue() {
	return this.value;
    }

    /** A setter of value. */
    public void setValue(int value) {
	this.value = value;
    }
} // End Class: NameValue
