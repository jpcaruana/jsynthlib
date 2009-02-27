package org.jsynthlib.jsynthlib.xml;

import org.xml.sax.SAXException;


class NotDeviceException extends SAXException {
    NotDeviceException() {
        super("This file does not contain a device.");
    }
}