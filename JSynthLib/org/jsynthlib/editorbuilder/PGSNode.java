package org.jsynthlib.editorbuilder;

import java.util.HashSet;

import javax.swing.tree.DefaultMutableTreeNode;


public class PGSNode extends DefaultMutableTreeNode {
    private String name;
    private HashSet names = new HashSet();
    
    public void addGroup(PGSNode group) {
        add(group);
    }
    public void addSysex(PGSNode syx) {
        add(syx);
    }
    private void addParameter(Parameter p) {
        if (p.getType() == Parameter.CONSTANT)
            return;
        // Ignore duplicate parameters
        if (names.contains(p.getName()))
            return;
        names.add(p.getName());
        p.setSysex(this);
        add(new DefaultMutableTreeNode(p));
    }
    public void addRange(Parameter p) {
        addParameter(p);
    }
    public void addLookup(Parameter p) {
        addParameter(p);
    }
    public void addString(Parameter p) {
        addParameter(p);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public String toString() {
        return getName();
    }
}
