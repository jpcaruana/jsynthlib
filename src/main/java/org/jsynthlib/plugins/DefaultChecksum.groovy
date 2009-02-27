package org.jsynthlib.plugins;



import org.jsynthlib.jsynthlib.xml.XMLPatch

public class DefaultChecksum extends BasicChecksum {
    public byte transform(int sum) {
        return (byte)(-sum).and(0x7f)
    }

    public void main(args) {
        PluginRegistry.registerChecksum("twos complement", DefaultChecksum.class)
    }
}