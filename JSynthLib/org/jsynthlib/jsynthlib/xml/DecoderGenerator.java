package org.jsynthlib.jsynthlib.xml;

import org.jsynthlib.plugins.PluginRegistry;
import org.jsynthlib.plugins.Decoder;
import org.jsynthlib.utils.Generator;
import org.xml.sax.SAXException;

class DecoderGenerator extends Generator {
    public Object generate(String element, String type) throws SAXException {
        Decoder d = null;
        try {
            // get decoder based on type
            d = PluginRegistry.getDecoder(type);
        } catch (Exception e) { }
        if (d == null)
            throw new SAXException("Decoder of type \"" + type
                    +"\" does not exist");
        return d;
    }
}