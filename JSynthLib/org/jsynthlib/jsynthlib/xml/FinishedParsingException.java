package org.jsynthlib.jsynthlib.xml;

import org.xml.sax.SAXException;


class FinishedParsingException extends SAXException {
	FinishedParsingException() {
		super("Finished with this file.  Move along...");
	}
}