package org.jsynthlib.editorbuilder.widgets;

import org.jsynthlib.editorbuilder.Parameter;

public abstract class ParameterWidget extends AnchoredWidget {
	public ParameterWidget(String name) {
		super(name);
	}
	public final String getType() {
		return super.getType();
	}
	public void setType(String type) {
		super.setType(type);
	}
	public abstract String[] getTypes();
	public abstract Parameter getParam();
	public abstract void setParam(Parameter p);
}
