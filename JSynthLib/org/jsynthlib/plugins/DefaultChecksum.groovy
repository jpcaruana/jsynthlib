package org.jsynthlib.plugins;



import org.jsynthlib.jsynthlib.xml.XMLPatch

class DefaultChecksum extends Checksum {
    @Property int start
    @Property int end
    @Property int address
    
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
        int sum = 0;
        for (i in start .. (end < 0 ? msg.size() + end - 1: end))
            sum += msg[i]
        msg[address < 0 ? msg.size() + address - 1: address] = (byte)(-sum).and(0x7f)
    }

    public void main(args) {
        PluginRegistry.registerChecksum("twos complement", DefaultChecksum.class)
	}
}