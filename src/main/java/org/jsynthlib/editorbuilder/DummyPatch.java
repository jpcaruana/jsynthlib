package org.jsynthlib.editorbuilder;
import org.jsynthlib.core.IPatchDriver;
import org.jsynthlib.xml.XMLPatch;




public class DummyPatch extends XMLPatch {
    public DummyPatch() {
        super(null, null, null, 0);
    }
    
    public IPatchDriver getDriver() {
        return null;
    }
}
