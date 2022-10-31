package com.mammb.code.jsonstruct.benchmark.data;

public class GlossDef {

    private String para;
    private String[] seeAlso;

    public GlossDef() {
    }

    public GlossDef(String para, String[] seeAlso) {
        this.para = para;
        this.seeAlso = seeAlso;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String[] getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(String[] seeAlso) {
        this.seeAlso = seeAlso;
    }
}
