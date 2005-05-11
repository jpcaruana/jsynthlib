package org.jsynthlib.jsynthlib.xml;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;

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
    		GroovyShell s = PluginRegistry.groovyShell();
		Closure c = (Closure)s.evaluate("return { " + variable + " -> " + value + " }");
		String result[] = new String[end - start + 1];
		for (int i = start; i <= end; i++)
			result[i - start] = c.call(new Object[] { new Integer(i) }).toString();
		return result;
    }
}
