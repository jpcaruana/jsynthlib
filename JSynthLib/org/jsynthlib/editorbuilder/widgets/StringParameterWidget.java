package org.jsynthlib.editorbuilder.widgets;
import org.jsynthlib.editorbuilder.Parameter;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

import core.PatchNameWidget;
public class StringParameterWidget extends ParameterWidget {
	
	protected Parameter param;
	protected PatchNameWidget widget;
	protected String label;
	
	private static final String types[] = new String[] {"Patch Name"};
	
	public StringParameterWidget() { 
		super("string");
	}
	public StringParameterWidget(Parameter _param) {
		this();
		if (_param == null)
			throw new NullPointerException();
		setParam(_param);
	}
	
	public String[] getTypes() { return new String[] {"Patch Name"}; }

	public void setText( String t ) {
		super.setText(t);
		widget.setLabel(label);
	}
	
	public Parameter getParam() {
		return param;
	}
	
	public void setParam(Parameter p) {
		this.param = p;
		if (widget != null)
			remove(widget);
		widget = new PatchNameWidget(Parameter.nullPatch(), p);
		add(widget);
		if (label != null)
			widget.setLabel(label);
	}
	protected void startElement(XMLWriter xml) throws SAXException {
		super.startElement(xml);
		if (param != null)
			param.write(xml);
	}

}
