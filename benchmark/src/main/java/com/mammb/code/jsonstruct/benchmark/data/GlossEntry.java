package com.mammb.code.jsonstruct.benchmark.data;

public class GlossEntry {
    private String id;
    private String sortAs;
    private String glossTerm;
    private String acronym;
    private String abbrev;
    private GlossDef def;

    public GlossEntry() {
    }

    public GlossEntry(String id, String sortAs, String glossTerm, String acronym, String abbrev, GlossDef def) {
        this.id = id;
        this.sortAs = sortAs;
        this.glossTerm = glossTerm;
        this.acronym = acronym;
        this.abbrev = abbrev;
        this.def = def;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        this.sortAs = sortAs;
    }

    public String getGlossTerm() {
        return glossTerm;
    }

    public void setGlossTerm(String glossTerm) {
        this.glossTerm = glossTerm;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public GlossDef getDef() {
        return def;
    }

    public void setDef(GlossDef def) {
        this.def = def;
    }
}
