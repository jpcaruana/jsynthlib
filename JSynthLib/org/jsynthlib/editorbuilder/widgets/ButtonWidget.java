package org.jsynthlib.editorbuilder.widgets;
import javax.swing.JButton;

public class ButtonWidget extends AnchoredWidget {
	private JButton button;
	
	protected String classname;
	
	public ButtonWidget() {
		this("Button");
	}
	
	public ButtonWidget(String text) {
		super("button");
		button = new JButton(text);
		setText(text);
		classname = "javax.swing.JInternalFrame";
		add(button);
	}
	public String getClassName(){ return classname; }
	public void setClassName( String _c ) { classname = _c; }
	
	public void setText(String t) {
		super.setText(t);
		button.setText(t);
	}
	
}
