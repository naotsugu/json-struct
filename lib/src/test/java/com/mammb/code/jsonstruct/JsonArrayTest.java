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

import com.mammb.code.jsonstruct.testdata.FullName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JsonArrayTest.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonArrayTest {

    @Test
    void testSimpleArray() {

        var jsonStr = """
            {"id":10,"array":["a","b"]}""";

        var d = Json.from(jsonStr, Data1.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data1(Long id, String[] array) {}


    @Test
    void testObjectArray() {

        var jsonStr = """
            {"id":10,"array":[{"givenName":"g1","familyName":"f1"},{"givenName":"g2","familyName":"f2"}]}""";

        var d = Json.from(jsonStr, Data2.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data2(Long id, FullName[] array) {}


    @Test
    void testNestArray() {

        var jsonStr = """
            {"id":10,"array":[["a","b"],["c","d"]]}""";

        var d = Json.from(jsonStr, Data3.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data3(Long id, String[][] array) {}

}
