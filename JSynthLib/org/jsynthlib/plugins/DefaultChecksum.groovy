package org.jsynthlib.plugins;



import org.jsynthlib.jsynthlib.xml.XMLPatch

class DefaultChecksum extends Checksum {
    int start
    int end
    int address
    
    public void setStart(String s) {
        start = Integer.decode(s)
    }
    public void setEnd(String s) {
        end = Integer.decode(s)
    }
    public void setAddress(String s) {
        address = Integer.decode(s)
    }
    
    public void checksum(XMLPatch p, byte[] msg) {
        sum = 0;
        for (i in start .. (end < 0 ? msg.length - end : end))
            sum += msg[i]
        msg[address < 0 ? msg.length - address : address] = (-sum).and(0x7f)
    }

    public void main(args) {
        PluginRegistry.registerChecksum("twos complement", DefaultChecksum.class)
	}
}