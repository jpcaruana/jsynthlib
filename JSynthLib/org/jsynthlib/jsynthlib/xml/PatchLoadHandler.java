package org.jsynthlib.jsynthlib.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.jsynthlib.plugins.Checksum;
import org.jsynthlib.plugins.Decoder;
import org.jsynthlib.utils.AdvDefaultHandler;
import org.jsynthlib.utils.Generator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import core.ErrorMsg;

/**
 * @author ribrdb
 */
public class PatchLoadHandler extends AdvDefaultHandler {
    private PatchBuilder curpatch = null;
    private Decoder curdec = null;
    
    public PatchLoadHandler() {
        super("patch");
        generators.put("decoder", new DecoderGenerator());
        generators.put("checksum", new ChecksumGenerator());
        generators.put("sequence", new Generator(Sequence.class));
        generators.put("sysex", new SysexGenerator());
        generators.put("group", new Generator() {
            public Object generate(String element, String type) throws SAXException {
                return new GroupBuilder();
            }            
        });
        generators.put("patch", new PatchGenerator());
        generators.put("values", new Generator(LinkedList.class));
        ParamGenerator p = new ParamGenerator();
        generators.put("string", p);
        generators.put("constant", p);
        generators.put("lookup", p);
        generators.put("range", p);
    }
    
    private class ParamGenerator extends Generator {
        public Object generate(String element, String type) throws SAXException {
            if (curdec == null) {
                throw new SAXParseException("Parameters may only be contained by sysex messages with decoders.",locator);
            }
            return curdec.newParameter(element);
        }
    }
    private class SysexGenerator extends Generator {
        int id = 0;
        
        public Object generate(String element, String type) throws SAXException {
            return new SysexBuilder(id++);
        }
    }
    public class SysexBuilder {
        private String name;
        private LinkedHashMap params = new LinkedHashMap();
        private Pattern header;
        private int size = -1;
        private Decoder decoder;
        private Checksum checksum;
        private int id;
        
        public SysexBuilder(int id) {
            this.id = id;
        }
        public void setChecksum(Checksum checksum) {
            this.checksum = checksum;
        }
        public void setDecoder(Decoder decoder) throws CloneNotSupportedException {
            this.decoder = (Decoder) decoder.clone();
            curdec = this.decoder;
        }
        public void setHeader(String header) {
            this.header = Pattern.compile(header, Pattern.CASE_INSENSITIVE);
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setSize(String size) {
            this.size = Integer.decode(size).intValue();
        }
        public void addParameter(XMLParameter p) throws SAXParseException {
            decoder.finishParameter(p);
            p.setSysexIndex(id);
            params.put(p.getName(), p);
        }
        public void addString(XMLParameter p) throws SAXParseException {
            addParameter(p);
        }
        public void addConstant(XMLParameter p) throws SAXParseException {
            addParameter(p);
        }
        public void addLookup(XMLParameter p) throws SAXParseException {
            addParameter(p);
        }
        public void addRange(XMLParameter p) throws SAXParseException {
            addParameter(p);
        }
        public SysexDesc getSysex() throws SAXException {
            XMLParameter[] pa = (XMLParameter[])params.values().toArray(new XMLParameter[0]);
            if (size != -1) {
                if (decoder != null) {
                    if (decoder.getSize() + 2 > size)
                        throw new SAXParseException("Parameters are larger than specified size of sysex", locator);
                    else if (size < decoder.getSize() + 2)
                        ErrorMsg.reportStatus("Sysex " + name + " larger than parameters.");
                }
            } else {
                if (decoder == null)
                    throw new SAXParseException("Size must be specified for a sysex message if it has no decoder.",locator);
                size = decoder.getSize() + 2;
            }
            SysexDesc d = new SysexDesc(pa, header, size, decoder, checksum,name, id);
            curpatch._addSysexDesc(d);
            curdec = null;
            return d;
        }
    }
    public class GroupBuilder {
        private String name;
        private ArrayList descs = new ArrayList();
        public void setName(String s) {
            name = s;
        }
        public void addSysex(SysexDesc s) {
            descs.add(s);
        }
        public SysexGroup getGroup() {
            return new SysexGroup(name, (SysexDesc[])descs.toArray(new SysexDesc[0]));
        }
    }
    public static class PatchBuilder {
        private String name;
        private int size = 0;
        private ArrayList descs = new ArrayList();
        private ArrayList groups = new ArrayList();
        
        public void setName(String s) {
            name = s;
        }
        public void addGroup(SysexGroup g) {
            groups.add(g);
        }
        void _addSysexDesc(SysexDesc d) {
            descs.add(d);
            size += d.getSize();
        }
        public XMLPatch getPatch() {
            SysexDesc[] _descs = (SysexDesc[])descs.toArray(new SysexDesc[0]);
            SysexGroup[] _groups = (SysexGroup[])groups.toArray(new SysexGroup[0]);
            HashMap params = new HashMap();
            for (int i = 0; i < _groups.length; i++) {
                String gname = _groups[i].getName();
                for (int j = _groups[i].size() - 1; j >= 0; j--) {
                    SysexDesc d = _groups[i].get(j);
                    String sname = gname + '\u0000' + d.getName() + '\u0000';
                    XMLParameter[] p = d.getParams();
                    for (int k = p.length - 1; k >= 0; k--) {
                        if (p[k].getId() != null)
                            params.put(p[k].getId(), p[k]);
                        params.put(sname + p[k].getName(), p[k]);
                    }
                }
            }
            return new XMLPatch(params,_groups, _descs, size);
        }
    }
    private class PatchGenerator extends Generator {
        public Object generate(String element, String type) throws SAXException {
            if (curpatch != null) {
                throw new SAXParseException("Patches cannot contain patches",locator);
            }
            curpatch = new PatchBuilder();
            return curpatch;
        }
    }
}
