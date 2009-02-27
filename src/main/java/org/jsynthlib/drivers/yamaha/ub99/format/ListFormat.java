package org.jsynthlib.drivers.yamaha.ub99.format;

public class ListFormat implements IFormat {
    private String[] list;

    public ListFormat(String[] list) {
        this.list = list;
    }

    public String fmtString(int v) {
        return list[v];
    }
}
