package org.jsynthlib.jsynthlib.xml;

import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.jsynthlib.plugins.PluginRegistry;

/**
 * @author ribrdb
 */
public class Sequence {
    private String variable;
    private int start;
    private int end;
    private String value;
    
    public void setVariable(String v) {
        variable = v;
    }
    public void setStart(String s) {
        start = Integer.decode(s).intValue();
    }
    public void setEnd(String s) {
        end = Integer.decode(s).intValue();
    }
    public void setValue(String v) {
        value = v;
    }
    public String[] getSequence() throws CompilationFailedException, IOException {
        String code = 
            "string_sequence = java.lang.reflect.Array.newInstance(String.class, " + (end - start + 1) + ")\n" +
            "for (" + variable + " in " + start + ".." + end + " ) {\n"
            + "text = <<<EOL\n"
            + value
            + "\nEOL\n"
            + "string_sequence[" + variable + " - " + start+ "] = text.toString()\n" +
            "}\n" +
            "return string_sequence";
        Object o = PluginRegistry.groovyShell().evaluate(code);
        return (String[])o;
    }
}
