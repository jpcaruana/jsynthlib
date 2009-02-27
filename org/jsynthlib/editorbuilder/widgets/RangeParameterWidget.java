package org.jsynthlib.editorbuilder.widgets;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SpinnerWidget;
import org.jsynthlib.core.SysexWidget;
import org.jsynthlib.core.VertScrollBarWidget;
import org.jsynthlib.editorbuilder.Parameter;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class RangeParameterWidget extends ParameterWidget {
	protected static final String[] types = new String[] {"H. ScrollBar",
			"V. ScrollBar",
			"Knob",
			"Spinner",
			//							  "Combo Box",
	};
	protected Parameter param;
	protected SysexWidget widget;
	
	public RangeParameterWidget() {
		super("range");
	}
	public RangeParameterWidget(Parameter _param) {
		this();
		if (_param == null)
			throw new NullPointerException();
		param = _param;
		setType("Knob");
	}
	public Parameter getParam() { return param; }
	public void setParam(Parameter p) {
		param = p;
		setType(getType());
	}
	public void setType(String type) {
		if (widget != null)
			remove(widget);
		if (type.equals("H. ScrollBar"))
			widget = new ScrollBarWidget(Parameter.nullPatch(),param);
		else if (type.equals("V. ScrollBar"))
			widget = new VertScrollBarWidget(Parameter.nullPatch(),param);
		else if (type.equals("Knob"))
			widget = new KnobWidget(Parameter.nullPatch(),param);
		else if (type.equals("Spinner"))
			widget = new SpinnerWidget(Parameter.nullPatch(),param);
		else throw new Error("Invalid type "+ type);
		add(widget);
		if (getText() != null)
			widget.setLabel(getText());
		super.setType(type);
	}

	public void setText( String t ) {
		widget.setLabel(t);
		super.setText(t);
	}
	public String[] getTypes() { return types; }
	public String construct() {
		return null;
	}
	public String preinitialize() { return null; }
	public String postinitialize() { return null; }
	protected void startElement(XMLWriter xml) throws SAXException {
		super.startElement(xml);
		if (param != null)
			param.write(xml);
	}
}
