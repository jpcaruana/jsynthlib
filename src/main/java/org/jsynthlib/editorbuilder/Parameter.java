package org.jsynthlib.editorbuilder;
import org.jsynthlib.core.IPatch;
import org.jsynthlib.jsynthlib.xml.XMLParameter;
import org.jsynthlib.plugins.Decoder;
import org.jsynthlib.utils.Writable;
import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;


public class Parameter extends XMLParameter implements  Writable {
	private static DummyPatch dummypatch;
	
	protected PGSNode sysex;
	
	private static DummyDecoder dummycodec;
	
	public Parameter () {
		super(nullDecoder());
	}
	public Parameter(String name) {
		this();
		setName(name);
	}
	public String toString() { return getName(); }
	
	public static IPatch nullPatch() {
		if (dummypatch == null)
			dummypatch = new DummyPatch();
		return dummypatch;
	}
	public static Decoder nullDecoder() {
		if (dummycodec == null)
			dummycodec = new DummyDecoder();
		return dummycodec;
	}
	
	/**
	 * @return Returns the group.
	 */
	public String getGroupName() {
		return sysex.getParent().toString();
	}
	
	public String getSysexName() {
		return sysex.getName();
	}
	public void setSysex(PGSNode sysex) {
		this.sysex = sysex;
	}
	
	public String getId() {
		String id = super.getId();
		if (id != null)
			return id;
		return getGroupName() + " - " + getSysexName() + " - " + getName();
	}
	
	public int get(IPatch _p) {
		return getDefault();
	}
	public String getString(IPatch _p) {
	    return "";
	}
	public void setType(int type) {
		super.setType(type);
	}
	
	public void write(XMLWriter xml) throws SAXException {
		xml.startElement("param");
		xml.writeProperty("group", getGroupName());
		xml.writeProperty("sysex", getSysexName());
		xml.writeProperty("name", getName());
		xml.endElement("param");
	}
}
