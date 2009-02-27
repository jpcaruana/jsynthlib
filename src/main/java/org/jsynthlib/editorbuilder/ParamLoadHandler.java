package org.jsynthlib.editorbuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jsynthlib.utils.AdvDefaultHandler;
import org.jsynthlib.utils.Generator;
import org.jsynthlib.xml.Sequence;
import org.xml.sax.SAXException;


public class ParamLoadHandler extends AdvDefaultHandler {
	private static final Map types = new HashMap();
	static {
		types.put("string", new Integer(Parameter.STRING));
		types.put("range", new Integer(Parameter.RANGE));
		types.put("lookup", new Integer(Parameter.LOOKUP));
		types.put("constant", new Integer(Parameter.CONSTANT));
	}
	
	
	public ParamLoadHandler() {
		super("patch");
		ignoreExtraDefaults = true;
		
		Generator pgsGen = new Generator(PGSNode.class);
		Generator paramGen = new Generator() {
			public Object generate(String element, String unused) throws SAXException {
				Parameter result = new Parameter();
				Integer type = (Integer)types.get(element);
				result.setType(type.intValue());
				return result;
			}
		};
		
		generators.put("sequence", new Generator(Sequence.class));
		generators.put("sysex", pgsGen);
		generators.put("group", pgsGen);
		generators.put("patch", pgsGen);
		generators.put("values", new Generator(LinkedList.class));
		generators.put("string", paramGen);
		generators.put("constant", paramGen);
		generators.put("lookup", paramGen);
		generators.put("range", paramGen);

		
	}
	
	public DefaultMutableTreeNode getParamNode() {
		return (DefaultMutableTreeNode)getOutput();
	}
}
