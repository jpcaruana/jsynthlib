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
        Parameter p = new Parameter(this, size)
        p.setSize(default_size) // Set default size
        t = 0
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
        if (p === null)
            throw new Exception("Uh oh")
        size += p.getSize();
    }

    public void setSize(String s) {
        default_size = Integer.decode(s)
    }

    public int decode(XMLParameter param, byte[] msg) {
        return _decode(param.offset, param.size, param.signed, msg) + param.getBase()
    }
    
    private int _decode(int offset, int size, boolean signed, byte[] msg) {
        Integer shift, offset
        if (big_endian) {
            shift = word_size*(param.getSize() - 1)
            offset = -word_size
        } else {
            shift = 0
            offset = word_size
        }
        value = 0
        for (i in offset .. offset + size - 1) {
            // grr! looks like groovy doesn't support | or & yet
            value += (msg[i] << shift)
            shift += offset
        }
        return value    
    }
    
    // XXX: Add support for a mapping table for weird encodings.
    public String decodeString(XMLParameter p, byte[] msg) {
        if (p.size == 1) {
            return new String(msg, p.getOffset(), p.getLength(), p.getCharset())
        } else {
        		o = p.getOffset()
        		s = p.getSize()
            StringBuffer b = new StringBuffer()
            for (i in 0 .. p.length - 1) {
                b.append((char)_decode(o + s*i, s, msg))
            }
        }
    }

    public void encode(int value, XMLParameter param, byte[] msg) {
        b = _encode(value - param.getBase(), param.getSize())
        for (i in 0 .. param.getSize() - 1)
            msg[i + param.getOffset()] = b[i]
    }
    
    private Byte[] _encode(int value, int size) {
        shift = offset = 0
        Byte[] b = new Byte[size]
        mask = 1 << (word_size + 1)
        if (big_endian) {
            shift = 0
            offset = word_size
        } else {
            shift = word_size*(size - 1)
            offset = -word_size
        }
        for (i in 0 .. size - 1) {
            b[i] = (value % (mask << shift)) >> shift
            shift += word_size
        }
        return b
    }
    
    public String encode(int value, XMLParameter param) {
        retval = new StringBuffer()
        b = _encode(value + param.getBase(), param.getSize())
        for (i in 0 .. param.getSize() - 1) {
            if (b[i] < 0x10)
                retval.append("0")
            retval.append(Integer.toHexString((int)b[i]))
        }
        return retval.toString()
    }

    public void encodeString(String value, XMLParameter p, byte[] msg) {
        i = 0
        size = p.getSize()
        offset = p.getOffset()
        while (i < p.length && i < value.length() ) {
            b = _encode(value.charAt(i), size)
            for (j in 0 .. size - 1)
                msg[offset + i*size + j] = b[i]
            i += 1
        }
        while (i < p.getLength()) {
            b = _encode(32, size) // " "
            for (j in 0 .. size - 1)
                msg[offset + i*size + j] = b[i]
            i += 1        
        }
    }
    
    public int getSize() {
        return size;
    }

	public void main(args) {
	   c = SimpleDecoder.class.getConstructor(new Class[] { Integer.class,
	                                                        Boolean.class
	                                                      });
	   PluginRegistry.registerDecoder("BE 7bit words",c, new Object[] {7,true});
	   PluginRegistry.registerDecoder("LE 7bit words",c, new Object[] {7,false});
	   PluginRegistry.registerDecoder("BE Nibbles",c, new Object[] {4,true});
	   PluginRegistry.registerDecoder("LE Nibbles",c, new Object[] {4,false});
	}
	
}
class Parameter extends XMLParameter {
	public int offset;
	protected int size;
	protected String charset
	protected boolean signed = false
	protected int base = 0
	   
	Parameter(decoder, offset) {
        super(decoder)
	    this.offset = offset
	}
	   
    public void setSize(int size) {
	   this.size = size
    }
    public void setSize(String s) {
        size = Integer.decode(s)
    }
    public void setCharset(String s) {
        charset = s
    }
    public void setOffset(String s) {
        base = Integer.decode(s)
    }
}
