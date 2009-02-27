package org.jsynthlib.jsynthlib.xml;

import java.util.regex.Pattern;

import org.jsynthlib.plugins.Checksum;
import org.jsynthlib.plugins.Decoder;

public class SysexDesc {
    private XMLParameter[] params;
    private Pattern header;
    private int size;
    private Decoder decoder;
    private Checksum checksum;
    private int id;
    private String name;
    
    public SysexDesc(XMLParameter[] params, Pattern header, int size,
            Decoder decoder, Checksum checksum, String name, int id) {
        this.params = params;
        this.header = header;
        this.size = size;
        this.decoder = decoder;
        this.checksum = checksum;
        this.name = name;
        this.id = id;
    }
    
    protected Pattern getHeader() {
        return header;
    }
    protected XMLParameter getParam(int i) {
        return params[i];
    }
    protected XMLParameter[] getParams() {
        return params;
    }
    protected int getSize() {
        return size;
    }
    protected Checksum getChecksum() {
        return checksum;
    }
    protected Decoder getDecoder() {
        return decoder;
    }
    protected boolean matches(String header) {
        return this.header.matcher(header).lookingAt();
    }
    protected int getId() {
        return id;
    }
    protected String getName() {
        return name;
    }
}
