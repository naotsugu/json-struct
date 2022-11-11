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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonStructIgnoreTest.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonStructIgnoreTest {

    @Test
    void testIgnorePrimitive() {

        var d = Json.from("""
            {"str":"a1","ignore":true}""", Data1.class);
        assertEquals("a1", d.str);
        assertFalse(d.ignore); // ignore -> default value

        var str = Json.stringifyOf(new Data1("a1", true));
        assertEquals("""
            {"str":"a1"}""", str);

    }
    @JsonStruct
    public record Data1(String str, @JsonStructIgnore boolean ignore) { }


    @Test
    void testIgnoreObject() {

        var d2 = Json.from("""
            {"str":"a1","ignore":"ignore"}""", Data2.class);
        assertEquals("a1", d2.str);
        assertNull(d2.ignore); // ignore -> default value

        var str = Json.stringifyOf(
            new Data2("a1", "ignore"));
        assertEquals("""
            {"str":"a1"}""", str);

    }
    @JsonStruct
    public record Data2(String str, @JsonStructIgnore String ignore) { }


    @Test
    void testIgnoreCollection() {

        var d3 = Json.from("""
            {"str":"a1","ignore":["11","12"],"list2":["21","22"]}""", Data3.class);
        assertEquals("a1", d3.str);
        assertNull(d3.ignore); // ignore -> default value
        assertIterableEquals(List.of("21", "22"), d3.list2);

        var str = Json.stringifyOf(
            new Data3("a1", List.of("11", "12"), List.of("21", "22")));
        assertEquals("""
            {"str":"a1","list2":["21","22"]}""", str);

    }
    @JsonStruct
    public record Data3(String str, @JsonStructIgnore List<String> ignore, List<String> list2) { }


    @Test
    void testIgnoreClass() {

        var d4 = Json.from("""
            {"str1":"a","str2":"b"}""", Data4.class);
        assertNull(d4.getStr1()); // ignore -> default value
        assertEquals("b", d4.getStr2());

        var str = Json.stringifyOf(new Data4("a", "b"));
        assertEquals("""
            {"str1":"a"}""", str);
    }
    @JsonStruct
    public static class Data4 {
        private final String str1;
        private final String str2;

        public Data4(@JsonStructIgnore String str1, String str2) {
            this.str1 = str1;
            this.str2 = str2;
        }
        public String getStr1() { return str1;}
        @JsonStructIgnore
        public String getStr2() { return str2;}
    }

}
