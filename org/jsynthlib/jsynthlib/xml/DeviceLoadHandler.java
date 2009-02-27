package org.jsynthlib.jsynthlib.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import org.jsynthlib.core.IDriver;
import org.jsynthlib.plugins.PluginRegistry;
import org.jsynthlib.utils.AdvDefaultHandler;
import org.jsynthlib.utils.Generator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author ribrdb
 */
public class DeviceLoadHandler extends AdvDefaultHandler {
    public DeviceLoadHandler() {
        super("device", "patch", new PatchLoadHandler());
        generators.put("device", new Generator(DeviceBuilder.class));
        generators.put("driver", new DriverGenerator());
        generators.put("banknames", new Generator(LinkedList.class));
        generators.put("patchnames", new Generator(LinkedList.class));
        generators.put("decoder", new DecoderGenerator());
        generators.put("checksum", new ChecksumGenerator());
        generators.put("sequence", new Generator(Sequence.class));
        
    }
    
    public XMLDevice getDevice() {
        return (XMLDevice)getOutput();
    }
    
    public static class DeviceBuilder {
        private String manufacturer;
        private String inquiryid;
        private String infotext;
        private String authors;
        private String model;

        public DeviceBuilder() {}
        
        private ArrayList drivers = new ArrayList();
        
        public void setAuthors(String authors) {
            this.authors = authors;
        }
        public void setInfotext(String infotext) {
            this.infotext = infotext;
        }
        public void setInquiryid(String inquiryid) {
            this.inquiryid = inquiryid;
        }
        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }
        public void setName(String name) {
            this.model = name;
        }
        public void addDriver(XMLDriver driver) {
            drivers.add(driver);
        }
        public XMLDevice getDevice() {
            XMLDevice d = new XMLDevice(manufacturer, model, inquiryid,
                    infotext, authors);
            IDriver[] drivers = (IDriver[])this.drivers.toArray(new IDriver[0]);
            for (int i = 0; i < drivers.length; i++) {
                d.addDriver(drivers[i]);
            }
            return d;
        }
    }

    private class DriverGenerator extends Generator {
        public Object generate(String element, String type) throws SAXException {
            return new DriverBuilder();
        }
    }
    public class DriverBuilder {
        private String name;
        private String model;
        private String authors;
        private String manufacturer;
        private String[] pnums;
        private String[] wpnums;
        private String[] bnums;
        private String[] wbnums;
        private XMLPatch patch;
        private Class imp = XMLDriverImplementation.class;
        private String editor;
        
        public DriverBuilder() { }
        
        public XMLDriver getDriver() throws SAXParseException {
            XMLDriverImplementation i = getImp();
            XMLDriver d = new XMLDriver(pnums, wpnums, bnums, wbnums, patch, i);
            d.setPatchType(name);
            d.setAuthors(authors);
            d.setModelName(model);
            d.setManufacturerName(manufacturer);
            if (editor != null)
                d.setEditor(new File(base_path, editor));
            return d;
        }
        
        public void setEditor(String s) {
            editor = s;
        }
        public void setName(String s) {
            name = s;
        }
        public void setModel(String s) {
            model = s;
        }
        public void setManufacturer(String s) {
            manufacturer = s;
        }
        public void setAuthors(String s) {
            authors = s;
        }
        public void setBanknames(LinkedList l) {
            bnums = AdvDefaultHandler.list2sa(l);
        }
        public void setPatchnames(LinkedList l) {
            pnums = AdvDefaultHandler.list2sa(l);
        }
        public void setWpatchnames(LinkedList l) {
            wpnums = AdvDefaultHandler.list2sa(l);
        }
        public void setWbanknames(LinkedList l) {
            wbnums = AdvDefaultHandler.list2sa(l);
        }
        public void setPatch(XMLPatch p) {
            patch = p;
        }
        public void setClass(String f) throws SAXParseException {
            try {
                imp = PluginRegistry.groovyLoader().parseClass(new File(base_path, f));
            } catch (Exception e) {
                throw new SAXParseException("Error parsing driver class", locator, e);
            }
        }
        private XMLDriverImplementation getImp() throws SAXParseException {
            try {
                return (XMLDriverImplementation) imp.newInstance();
            } catch (Exception e) {
                throw new SAXParseException("Error instantiating driver class", locator, e);
            }
                
        }
    }

}
