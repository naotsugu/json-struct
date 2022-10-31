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
 * GlossDef.
 * @author Naotsugu Kobayashi
 */
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
