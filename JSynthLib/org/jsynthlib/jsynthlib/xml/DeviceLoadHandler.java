package org.jsynthlib.jsynthlib.xml;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jsynthlib.utils.AdvDefaultHandler;
import org.jsynthlib.utils.Generator;

import core.IDriver;

/**
 * @author ribrdb
 */
public class DeviceLoadHandler extends AdvDefaultHandler {
    public DeviceLoadHandler() {
        super("device", "patch", new PatchLoadHandler());
        generators.put("device", new Generator(DeviceBuilder.class));
        generators.put("driver", new Generator(DriverBuilder.class));
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

    public static class DriverBuilder {
        private String model;
        private String authors;
        private String manufacturer;
        private String[] pnums;
        private String[] wpnums;
        private String[] bnums;
        private String[] wbnums;
        private XMLPatch patch;
        
        public DriverBuilder() {}
        
        public XMLDriver getDriver() {
            //FIXME: load method implementation
            XMLDriver d = new XMLDriver(pnums, wpnums, bnums, wbnums, patch,null);
            d.setAuthors(authors);
            d.setModelName(model);
            d.setManufacturerName(manufacturer);
            return d;
        }
        
        public void setName(String s) {
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
    }

}
