package org.jsynthlib.jsynthlib.xml;


class ParamCacheEntry {
    private boolean clean;
    private int idata;
    private String sdata;
    public ParamCacheEntry(String data, boolean clean) {
        this.clean = clean;
        this.sdata = data;
    }
    public ParamCacheEntry(int data, boolean clean) {
        this.clean = clean;
        this.idata = data;
    }
    public int getInt() {
        return idata;
    }
    public String getString() {
        return sdata;
    }
    public boolean isClean() {
        return clean;
    }
    public void setClean(boolean b) {
        clean = b;
    }
}