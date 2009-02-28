package org.jsynthlib.xml;

/**
 * @author ribrdb
 */
public class SysexGroup {
    private String name;
    private SysexDesc[] descs;

    public SysexGroup(String s, SysexDesc[] d) {
        name = s;
        descs = d;
    }
    
    public String getName() {
        return name;
    }
    public int size() {
        return descs.length;
    }
    public SysexDesc get(int i){
        return descs[i];
    }
}
