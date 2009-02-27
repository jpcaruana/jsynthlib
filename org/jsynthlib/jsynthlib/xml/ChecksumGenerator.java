package org.jsynthlib.jsynthlib.xml;

import org.jsynthlib.plugins.Checksum;
import org.jsynthlib.plugins.PluginRegistry;
import org.jsynthlib.utils.Generator;
import org.xml.sax.SAXException;


class ChecksumGenerator extends Generator {
    public Object generate(String element, String type) throws SAXException {
        Checksum c = null;
        try {
            // get decoder based on type
            c = PluginRegistry.getChecksum(type);
        } catch (Exception e) { }
        if (c == null)
            throw new SAXException("Checksum of type \"" + type
                    +"\" does not exist");
        return c;
    }
}