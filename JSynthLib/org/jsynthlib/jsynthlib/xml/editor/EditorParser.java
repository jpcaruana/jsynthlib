package org.jsynthlib.jsynthlib.xml.editor;

import java.util.LinkedList;

import org.jsynthlib.utils.AdvDefaultHandler;
import org.jsynthlib.utils.Generator;
import org.xml.sax.SAXException;


public class EditorParser extends AdvDefaultHandler {
	public EditorParser() {
		super("editor");
		addGenerator("editor", EditorDescription.class);
		addGenerator("widgets", LinkedList.class);
		addGenerator("size", WidgetSize.class);
		addGenerator("position", new Generator() {
			public Object generate(String element, String type)
					throws SAXException {
				if ("absolute".equals(type))
					return new AbsolutePosition();
				else if ("relative".equals(type))
					return new RelativePosition();
				else
					throw new SAXException("Unrecognized position type " + type);
			}
		});
		addGenerator("strut", StrutDescription.class);
		addGenerator("label", LabelDescription.class);
		addGenerator("button", ButtonDescription.class);
		addGenerator("panel", PanelDescription.class);
		addGenerator("param", ParameterKey.class);
		
		Generator paramGen = new Generator() {
			public Object generate(String element, String type)
					throws SAXException {
				return new SysexWidgetDescription(element, type);
			}
		};
		addGenerator("range", paramGen);
		addGenerator("lookup", paramGen);
		addGenerator("string", paramGen);
		
		Generator aGen = new Generator() {
			public Object generate(String element, String type)
					throws SAXException {
				return new Anchor(element);
			}
		};
		addGenerator("top", aGen);
		addGenerator("bottom", aGen);
		addGenerator("left", aGen);
		addGenerator("right", aGen);
	}
	public EditorDescription getEditor() {
		return (EditorDescription)getOutput();
	}
}
