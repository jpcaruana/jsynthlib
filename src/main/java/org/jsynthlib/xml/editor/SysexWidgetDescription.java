package org.jsynthlib.xml.editor;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.jsynthlib.core.ComboBoxWidget;
import org.jsynthlib.core.IPatch;
import org.jsynthlib.core.KnobLookupWidget;
import org.jsynthlib.core.KnobWidget;
import org.jsynthlib.core.PatchNameWidget;
import org.jsynthlib.core.ScrollBarLookupWidget;
import org.jsynthlib.core.ScrollBarWidget;
import org.jsynthlib.core.SpinnerWidget;
import org.jsynthlib.core.SysexWidget;
import org.jsynthlib.core.VertScrollBarWidget;
import org.jsynthlib.xml.XMLParameter;
import org.jsynthlib.xml.XMLPatch;



public class SysexWidgetDescription extends WidgetDescription {

	private static final Map classMap;
	static {
		classMap = new HashMap();
		classMap.put("string", PatchNameWidget.class);
		classMap.put("lookup", lookupMap());
		classMap.put("range", rangeMap());
	}
	
	private ParameterKey param;
	private Constructor cons;
	
	public SysexWidgetDescription(String element, String type) {
		super(element);
		Class c = null;
		Object o = classMap.get(element);
		if (o instanceof Class) {
			c = (Class)o;
		} else if (o instanceof Map) {
			c = (Class)((Map)o).get(type);
		}
		cons = findConstructor(c);
	}
	
	private static Map lookupMap() {
		HashMap m = new HashMap();
		m.put("Combo Box", ComboBoxWidget.class);
		m.put("ScrollBar", ScrollBarLookupWidget.class);
		m.put("Knob", KnobLookupWidget.class);
		return m;
	}
	
	private static Map rangeMap() {
		HashMap m = new HashMap();
		m.put("H. ScrollBar",ScrollBarWidget.class);
		m.put("V. ScrollBar",VertScrollBarWidget.class);
		m.put("Knob",KnobWidget.class);
		m.put("Spinner",SpinnerWidget.class);
		return m;
	}

	/* (non-Javadoc)
	 * @see org.jsynthlib.jsynthlib.xml.WidgetDescription#create(java.util.Map)
	 */
	public Component create(Map widgets, XMLPatch patch) throws Exception {
		SysexWidget w = createWidget(patch);
		if (getTitle() != null)
			w.setLabel(getTitle());
		widgets.put(getId(), w);
		return null;
	}

	private SysexWidget createWidget(XMLPatch patch) throws Exception {
		return (SysexWidget) cons.newInstance(new Object[] {patch, getParameter(patch)});
	}

	private Constructor findConstructor(Class c) {
		try {
			return c.getConstructor(new Class[] { IPatch.class, SysexWidget.IParameter.class });
		} catch (Exception e) {
			return null;
		} 
	}
	private XMLParameter getParameter(XMLPatch patch) {
		return patch.getParameter(param.getGroup(), param.getSysex(), param.getName());
	}

	public ParameterKey getParam() {
		return param;
	}
	public void setParam(ParameterKey param) {
		this.param = param;
	}


}
