package org.jsynthlib.plugins;

import org.jsynthlib.jsynthlib.xml.XMLParameter;


/**
 * Interface for encoding/decoding sysex data.
 * 
 * Implementing classes need to be able to create XMLParameters
 * containing whatever information is necessary for encoding or
 * decoding that parameter, although they do not need to worry
 * about storing these XMLParameters.
 * @author ribrdb
 */
public abstract class Decoder implements Cloneable {
    /**
     * Create a new XMLParameter subclass.
     * @param type Currently one of string, lookup, range, or constant
     * @return The XMLParameter
     */
    abstract public XMLParameter newParameter(String type);
    
    /**
     * Called after all information has been loaded into a parameter
     * from the xml file. Perform any actions necessary before processing
     * next parameter.  For example, add the parameter's size to the total
     * size in the decoder.
     */
    abstract public void finishParameter(XMLParameter param);    
    /*
	public Object getParameter(String name);
	public Object getParameter(String message, String name);
	public Object getParameter(String group, String message, String name);
	
	public void setParameter(String name, Object value);
	public void setParameter(String message, String name, Object value);
	public void setParameter(String group, String message, String name, Object value);
    */ 
    /**
     * Get the value of the specified parameter from the specified message.
     */
    abstract public int decode(XMLParameter param, byte[] m);
    abstract public String decodeString(XMLParameter param, byte[] m);
    /**
     * Store the value of the psecified parameter to a sysex message.
     */
    abstract public void encode(int value, XMLParameter param, byte[] m);
    abstract public void encodeString(String value, XMLParameter param, byte[] m);
    /**
     * Get a hex string representing the encoding of the specified
     * value for a parameter. For example, a Nibble decoder might
     * return "0101" for a parameter that is two nibbles long and a
     * value of 17.
     * @param value Value to encode
     * @param param Description of the parameter who's value we're encoding.
     * @return Hex string of the encoded value.
     */
    abstract public String encode(int value, XMLParameter param);

    /**
     * Size (in bytes) of the sysex message data, not including the
     * leading 0xF0 or ending 0xF7.
     * @author ribrdb
     */
    abstract public int getSize();
    /**
     * Method called when loading plugins.
     * Implementing classes must call PluginRegister.registerDecoder to 
     * register themselves.
     * @param args Not used.
     */
    abstract public void main(Object args);
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
