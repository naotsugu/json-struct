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
package com.mammb.code.jsonstruct;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JsonStructCyclicTest.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonStructCyclicTest {

    @Test
    void testCyclic() {
        var d3 = new Data("3", null);
        var d2 = new Data("2", d3);
        var d1 = new Data("1", d2);
        var d0 = new Data("0", d1);

        var str = Json.stringify(d0);
        assertEquals("""
            {"name":"0","data":{"name":"1","data":{"name":"2","data":null}}}""", str);
    }


    @Test
    void testCyclic2() {
        var d0 = new Data("0", null);

        var str = Json.stringify(d0);
        assertEquals("""
            {"name":"0","data":null}""", str);
    }

    @JsonStruct
    public static class Data {
        String name;
        Data data;
        public Data(String name, Data data) {
            this.name = name;
            this.data = data;
        }
        public String getName() { return name; }

        public Data getData() { return data;}
    }

}
