package org.jsynthlib.editorbuilder;
import org.jsynthlib.jsynthlib.xml.XMLPatch;

import core.IPatchDriver;



public class DummyPatch extends XMLPatch {
    public DummyPatch() {
        super(null, null, null, 0);
    }
    
    public IPatchDriver getDriver() {
        return null;
    }
}
