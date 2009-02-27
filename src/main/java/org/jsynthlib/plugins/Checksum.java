package org.jsynthlib.plugins;

import org.jsynthlib.xml.XMLPatch;

/**
 * @author ribrdb
 */
public abstract class Checksum {
    abstract public void checksum(XMLPatch p, byte[] m);
    
    abstract public void main(Object args); 
}
