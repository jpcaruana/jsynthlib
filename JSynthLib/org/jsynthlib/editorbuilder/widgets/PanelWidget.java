package org.jsynthlib.editorbuilder.widgets;
import javax.swing.BorderFactory;

import org.jsynthlib.utils.AutoSpringLayout;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class PanelWidget extends ContainerWidget {
	private boolean asRoot;
	
	public PanelWidget() {
		this("Panel");
	}
	
	public PanelWidget(String title) {
		super("panel");
		setLayout(new AutoSpringLayout());
		setText(title);
	}
	public void setText(String title) {
		if (!asRoot)
			setBorder(BorderFactory.createTitledBorder(title));
		super.setText(title);
	}
	
	public void setRoot(boolean asRoot) {
		this.asRoot = asRoot;
		if (asRoot)
			setBorder(null);
		else
			setText(getText());
	}
	
	public boolean isRoot() {
		return asRoot;
	}
	
	protected void writePosition(XMLWriter xml) throws SAXException {
		if (!asRoot) {
			super.writePosition(xml);
			xml.writeProperty("border", "true");
		}
	}
}
