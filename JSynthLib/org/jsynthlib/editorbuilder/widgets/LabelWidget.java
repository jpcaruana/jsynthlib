package org.jsynthlib.editorbuilder.widgets;

import javax.swing.JLabel;

public class LabelWidget extends AnchoredWidget {
	
	private JLabel label;
	
	public LabelWidget() {
		this("Label");
	}
	
	public LabelWidget(String text) {
		super("label");
		label = new JLabel();
		setText(text);
		add(label);
	}
	
	public void setText(String text) {
		label.setText(text);
		super.setText(text);
	}
}
