package com.mammb.code.jsonstruct.benchmark.data;

public class GlossDiv {
    private String title;
    private GlossEntry[] list;

    public GlossDiv() {
    }

    public GlossDiv(String title, GlossEntry[] list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GlossEntry[] getList() {
        return list;
    }

    public void setList(GlossEntry[] list) {
        this.list = list;
    }
}
