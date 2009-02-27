package org.jsynthlib.plugins;



import org.jsynthlib.jsynthlib.xml.XMLPatch

public class BasicChecksum extends Checksum {
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
        int sum = sum(p, msg)
	byte value = transform(sum)
	msg[getAddress(p,msg)] = value
    }
    protected int sum(XMLPatch p, byte[] msg) {
        int sum = 0;
        for (i in start .. calcOffset(end, msg))
            sum += msg[i]
        return sum
    }
    protected byte transform(int sum) {
        return (byte)sum.and(0x7f)
    }
	protected int calcOffset(int p, byte[] msg) {
		if (p < 0)
			return msg.size() + p - 1
		else
			return p
	}
    protected int getAddress(XMLPatch p, byte[] msg) {
		return calcOffset(address, msg)
    }

    public void main(args) {
        PluginRegistry.registerChecksum("ones complement", BasicChecksum.class)
    }
}