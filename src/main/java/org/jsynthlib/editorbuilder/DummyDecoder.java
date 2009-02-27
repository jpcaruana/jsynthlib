package org.jsynthlib.editorbuilder;

import org.jsynthlib.plugins.Decoder;
import org.jsynthlib.xml.XMLParameter;


public class DummyDecoder extends Decoder {

	public XMLParameter newParameter(String type) {
		return null;
	}

	public void finishParameter(XMLParameter param) {

	}

	public int decode(XMLParameter param, byte[] m) {
		return 0;
	}

	public String decodeString(XMLParameter param, byte[] m) {
		return null;
	}

	public void encode(int value, XMLParameter param, byte[] m) {

	}

	public void encodeString(String value, XMLParameter param, byte[] m) {

	}

	public String encode(int value, XMLParameter param) {
		return null;
	}

	public int getSize() {
		return 0;
	}

	public void main(Object args) {
	}

}
