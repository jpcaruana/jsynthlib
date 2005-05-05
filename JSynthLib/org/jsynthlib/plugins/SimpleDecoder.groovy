package org.jsynthlib.plugins;



import org.jsynthlib.jsynthlib.xml.XMLParameter

/** 
 * Basic decoder for handling n bits per sysex byte.
 *
 * @author ribrdb
 */
class SimpleDecoder extends Decoder {

    private int word_size
    private boolean big_endian
    private int cur_offset = 0
    private int size = 0
    private int default_size
    
    /**
     * This appears to be required for loading the plugin.
     */ 
    public SimpleDecoder() {
    }
    
    public SimpleDecoder(Integer size, Boolean be) {
        word_size = size
        big_endian = be
        this.size = 0
        default_size = word_size < 7 ? 2 : 1
    }
    
    public XMLParameter newParameter(String type) { 
        SimpleDecoderParameter p = new SimpleDecoderParameter(this, cur_offset)
        p.setSize(default_size) // Set default size
        int t = 0
        switch (type) {
        case "constant":
            t = XMLParameter.CONSTANT
            break
        case "string":
            t = XMLParameter.STRING
            break
        case "lookup":
            t = XMLParameter.LOOKUP
            break
        case "range":
            t = XMLParameter.RANGE
            break
        default:
            throw new Exception("Simple decoder doesn't support parameter type ${type}");
        }
        p.setType(t)
        return p
    }

    public void finishParameter(XMLParameter p) {
        if (p == null)
            throw new Exception("Uh oh")
        if (p.type == XMLParameter.STRING)
            cur_offset = p.offset + p.length*p.size
        else
            cur_offset = p.offset + p.size
        if (cur_offset > size)
            size = cur_offset
    }

    public void setSize(String s) {
        default_size = Integer.decode(s)
    }

    public int decode(XMLParameter param, byte[] msg) {
        return _decode(param.offset, param.size, param.signed, msg) + param.getBase()
    }
    
    private int _decode(int offset, int size, boolean signed, byte[] msg) {
        Integer shift = 0
        Integer s_offset = 0
        if (big_endian) {
            shift = word_size*(size - 1)
            s_offset = -word_size
        } else {
            shift = 0
            s_offset = word_size
        }
        int value = 0
        for (i in offset .. offset + size - 1) {
            // grr! looks like groovy doesn't support | or &
            // or shifting a byte
            value += ((int)msg[i] << shift)
            shift += s_offset
        }
        return value    
    }
    
    // XXX: Add support for a mapping table for weird encodings.
    public String decodeString(XMLParameter p, byte[] msg) {
        if (p.size == 1) {
            return new String(msg, p.getOffset(), p.getLength(), p.getCharset())
        } else {
            int o = p.getOffset()
            int s = p.getSize()
            StringBuffer b = new StringBuffer()
            for (i in 0 .. p.length - 1) {
                b.append((char)_decode(o + s*i, s, msg))
            }
        }
    }

    public void encode(int value, XMLParameter param, byte[] msg) {
        byte[] b = _encode(value - param.getBase(), param.getSize())
        for (i in 0 .. param.getSize() - 1)
            msg[i + param.getOffset()] = b[i]
    }
    
    private byte[] _encode(int value, int size) {
        int shift = 0, offset = 0
        byte[] b = new byte[size]
        int mask = 1 << (word_size + 1)
        if (big_endian) {
            shift = word_size*(size - 1)
            offset = -word_size
        } else {
            shift = 0
            offset = word_size
        }
        for (i in 0 .. size - 1) {
            b[i] = (value % (mask << shift)) >> shift
            shift += offset
        }
        return b
    }
    
    public String encode(int value, XMLParameter param) {
        StringBuffer retval = new StringBuffer()
        byte[] b = _encode(value + param.getBase(), param.getSize())
        for (i in 0 .. param.getSize() - 1) {
            if (b[i] < 0x10)
                retval.append("0")
            retval.append(Integer.toHexString((int)b[i]))
        }
        return retval.toString()
    }

    public void encodeString(String value, XMLParameter p, byte[] msg) {
        int i = 0
        int size = p.getSize()
        int offset = p.getOffset()
        while (i < p.length && i < value.length() ) {
            byte[] b = _encode((int)value.charAt(i), size)
            for (j in 0 .. size - 1)
                msg[offset + i*size + j] = b[j]
            i += 1
        }
        while (i < p.getLength()) {
            byte[] b = _encode(32, size) // " "
            for (j in 0 .. size - 1)
                msg[offset + i*size + j] = b[j]
            i += 1        
        }
    }
    
    public int getSize() {
        return size;
    }

	public void main(args) {
	   Class[] args = [ Integer.class, Boolean.class ]
	   def c = SimpleDecoder.class.getConstructor(args);
	   Object[] pargs = [7, true]
	   PluginRegistry.registerDecoder("BE 7bit words",c, pargs);
	   pargs[1] = false
	   PluginRegistry.registerDecoder("LE 7bit words",c, pargs);
	   pargs[0] = 4
	   PluginRegistry.registerDecoder("LE Nibbles",c, pargs);
	   pargs[1] = true
	   PluginRegistry.registerDecoder("BE Nibbles",c, pargs);
	}
	
}
// Groovy doesn't seem to support inner classes
class SimpleDecoderParameter extends XMLParameter {
    @Property public int offset;
    @Property public int size;
    @Property protected String charset
    @Property protected boolean signed = false
    @Property protected int base = 0
	   
    SimpleDecoderParameter(decoder, offset) {
        super(decoder)
        this.offset = offset
    }
	   
    public void setSize(int size) {
       this.size = size
    }
    public void setSize(String s) {
        this.size = Integer.decode(s)
    }
    public void setCharset(String s) {
        charset = s
    }
    public void setAddress(String s) {
    	offset = Integer.decode(s)
    }
    // This is the amount added to the value for display
    public void setOffset(String s) {
        base = Integer.decode(s)
    }
}
