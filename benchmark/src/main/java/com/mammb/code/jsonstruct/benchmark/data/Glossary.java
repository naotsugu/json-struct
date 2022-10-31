package com.mammb.code.jsonstruct.benchmark.data;

import com.mammb.code.jsonstruct.JsonStruct;

@JsonStruct
public class Glossary {
    private String title;
    private GlossDiv div;

    public Glossary() {
    }

    public Glossary(String title, GlossDiv div) {
        this.title = title;
        this.div = div;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GlossDiv getDiv() {
        return div;
    }

    public void setDiv(GlossDiv div) {
        this.div = div;
    }
}
