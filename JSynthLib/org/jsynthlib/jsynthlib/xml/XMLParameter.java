package org.jsynthlib.jsynthlib.xml;

import java.util.HashMap;
import java.util.LinkedList;

import org.jsynthlib.plugins.Decoder;
import org.jsynthlib.utils.AdvDefaultHandler;

import core.IPatch;
import core.SysexWidget;

public class XMLParameter implements SysexWidget.IParameter {
    public static final int RANGE = 0;
    public static final int LOOKUP = 1;
    public static final int STRING = 2;
    public static final int CONSTANT = 3;
    
    private String id;
    private int type;
    private String name;
    private int min;
    private int max;
    private String[] values = null;
    private int length = -1;
    private int sysex_index;
    private Decoder decoder;
    private int def= 0;

    public XMLParameter(Decoder decoder) {
        this.decoder = decoder;
    }

    public int get(IPatch _p) {
        XMLPatch p = (XMLPatch)_p;
        HashMap c = p.getCache();
        if (!c.containsKey(this)) {
            c.put(this,new Integer(decoder.decode(this,p.getMessage(sysex_index))));
        }
        return ((Integer)c.get(this)).intValue();
    }

    // should this check range?
    public void set(IPatch p, int val) {
        if (type == CONSTANT)
            val = def;
        ((XMLPatch)p).getCache().put(this, new Integer(val));
    }

    public String getString(IPatch _p) {
        XMLPatch p = (XMLPatch)_p;
        HashMap c = p.getCache();
        if (!c.containsKey(this))
            c.put(this,decoder.decodeString(this,p.getMessage(sysex_index)));
        return (String)c.get(this);
    }

    public void set(IPatch p, String stringval) {
        ((XMLPatch)p).getCache().put(this, stringval);
    }

    protected String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getLength() {
        return length;
    }
    protected void setLength(int length) {
        this.length = length;
    }
    public void setLength(String l) {
        length = Integer.decode(l).intValue();
    }
    public int getMax() {
        return max;
    }
    protected void setMax(int max) {
        this.max = max;
    }
    public void setMax(String max) {
        this.max = Integer.decode(max).intValue();
    }
    public int getMin() {
        return min;
    }
    protected void setMin(int min) {
        this.min = min;
    }
    public void setMin(String min) {
        this.min = Integer.decode(min).intValue();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    protected int getType() {
        return type;
    }
    protected void setType(int type) {
        this.type = type;
    }
    public String[] getValues() {
        return values;
    }
    protected void setValues(String[] values) {
        this.values = values;
    }
    public void setValues(LinkedList l) {
        values = AdvDefaultHandler.list2sa(l);
    }
    protected void setSysexIndex(int i) {
        sysex_index = i;
    }
    protected int getSysexIndex() {
        return sysex_index;
    }

    public void setDefault(String d) {
        def = Integer.decode(d).intValue();
    }
    public int getDefault() {
        return def;
    }
    
    public void store(XMLPatch p) {
        if (type == STRING) {
            decoder.encodeString(getString(p), this, p.getMessage(sysex_index));
        } else {
            decoder.encode(get(p), this, p.getMessage(sysex_index));
        }
    }
}
