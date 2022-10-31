/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jsonstruct.benchmark.data;

/**
 * GlossEntry.
 * @author Naotsugu Kobayashi
 */
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
