package org.jsynthlib.editorbuilder.widgets;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class RootPanelWidget extends PanelWidget {


    public RootPanelWidget() {
        this(100,100);
    }

    public RootPanelWidget(int w, int h) {
        this("", w, h);
    }


    public RootPanelWidget(String title, int w, int h) {
        this("root", title, w, h);
    }
    public RootPanelWidget(String name, String title, int w, int h) {
        super(name, title, w, h);
        setBordered(false);
    }
    protected void writePosition(XMLWriter xml) throws SAXException {
        // do nothing
    }
}
