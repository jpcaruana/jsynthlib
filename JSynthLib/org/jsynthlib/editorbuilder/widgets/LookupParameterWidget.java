package org.jsynthlib.editorbuilder.widgets;
import org.jsynthlib.editorbuilder.Parameter;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

import core.ComboBoxWidget;
import core.KnobLookupWidget;
import core.ScrollBarLookupWidget;
import core.SysexWidget;

public class LookupParameterWidget extends ParameterWidget {
    protected static final String[] types = new String[] {"Combo Box",
    		"ScrollBar",
		"Knob",
    };
    
    protected Parameter param;
    protected SysexWidget widget;
    
    public LookupParameterWidget() {
    	super("lookup");
    }
    public LookupParameterWidget(Parameter _param) {
    	this();
    	if (_param == null)
    		throw new NullPointerException();
    	param = _param;
    	setType("Combo Box");
    }
    public Parameter getParam() { return param; }
    public void setParam(Parameter p) {
    	param = p;
    	setType(getType());
    }
    public void setText( String t ) {
    	super.setText(t);
    	widget.setLabel(t);
    }
    public String[] getTypes() { return types; }
    public void setType(String type) {
    	super.setType(type);
    	if (widget != null)
    		remove(widget);
    	if (type.equals("Combo Box"))
    		widget = new ComboBoxWidget(Parameter.nullPatch(),param);
    	else if (type.equals("ScrollBar"))
    		widget = new ScrollBarLookupWidget(Parameter.nullPatch(),param);
    	else if (type.equals("Knob"))
    		widget = new KnobLookupWidget(Parameter.nullPatch(),param);
    	else throw new Error("Invalid type "+ type);
    	add(widget);
    	if (getText() != null)
    		widget.setLabel(getText());
    }
    protected void startElement(XMLWriter xml) throws SAXException {
    	super.startElement(xml);
    	if (param != null)
    		param.write(xml);
    }
}