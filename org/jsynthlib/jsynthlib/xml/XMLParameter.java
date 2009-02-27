package org.jsynthlib.jsynthlib.xml;

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
    private int length = 1;
    private int sysex_index;
    private Decoder decoder;
    private int def= 0;

    public XMLParameter(Decoder decoder) {
        this.decoder = decoder;
    }

    // XXX: Overriden by EditorBuilder
    public /*final*/ int get(IPatch _p) {
        XMLPatch p = (XMLPatch)_p;
        if (!p.isCached(this))
            p.cache(this, new ParamCacheEntry(decoder.decode(this,p.getMessage(sysex_index)),true));

        return p.getCached(this).getInt();
    }

    // should this check range?
    public final void set(IPatch p, int val) {
        if (type == CONSTANT)
            val = def;
        ((XMLPatch)p).cache(this, new ParamCacheEntry(val, false));
    }

    public String getString(IPatch _p) {
        XMLPatch p = (XMLPatch)_p;
        if (!p.isCached(this))
            p.cache(this, new ParamCacheEntry(decoder.decodeString(this,p.getMessage(sysex_index)),true));
        return p.getCached(this).getString();
    }

    public final void set(IPatch p, String stringval) {
        ((XMLPatch)p).cache(this, new ParamCacheEntry(stringval, false));
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
    public int getType() {
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
    
    public final void store(XMLPatch p) {
        ParamCacheEntry e = p.getCached(this);
        if (e == null || e.isClean())
            return;
        if (type == STRING) {
            decoder.encodeString(e.getString(), this, p.getMessage(sysex_index));
        } else {
            decoder.encode(e.getInt(), this, p.getMessage(sysex_index));
        }
        e.setClean(true);
    }
    public byte[] getMessage(XMLPatch p) {
        return p.getMessage(sysex_index);
    }
    public String encode(XMLPatch p) {
        return decoder.encode(get(p), this);
    }

    /* (non-Javadoc)
     * @see core.SysexWidget.IParameter#send(core.IPatch)
     */
    public void send(IPatch p) {
        ((XMLDriver) p.getDriver()).sendParameter(p, this);
    }
}
