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

import com.mammb.code.jsonstruct.testdata.Book;
import com.mammb.code.jsonstruct.testdata.Food;
import com.mammb.code.jsonstruct.testdata.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JsonEmptyTest.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonEmptyTest {

    @Test
    void testNull() {
        var jsonStr = """
            {"name":null}""";
        var d = Json.objectify(jsonStr, Book.class);
        assertEquals(jsonStr, Json.stringify(d));
    }


    @Test
    void testEmptyList() {
        var jsonStr = """
            {"name":null,"materials":[]}""";
        var d = Json.objectify(jsonStr, Food.class);
        assertEquals(jsonStr, Json.stringify(d));
    }


    @Test
    void testNullList() {
        var jsonStr = """
            {"name":null,"materials":null}""";
        var d = Json.objectify(jsonStr, Food.class);
        assertEquals(jsonStr, Json.stringify(d));
    }


    @Test
    void testNullEnum() {
        var jsonStr = """
            {"fullName":{"givenName":"a","familyName":"b"},"age":20,"gender":null}""";
        var d = Json.objectify(jsonStr, Person.class);
        assertEquals(jsonStr, Json.stringify(d));
    }


    @Test
    void testNullObject() {
        var jsonStr = """
            {"fullName":null,"age":30,"gender":"FEMALE"}""";
        var d = Json.objectify(jsonStr, Person.class);
        assertEquals(jsonStr, Json.stringify(d));
    }

}
