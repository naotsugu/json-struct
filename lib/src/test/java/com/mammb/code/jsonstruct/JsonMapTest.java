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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JsonMapTest.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonMapTest {

    @Test
    void testSimpleMap() {

        var jsonStr = """
            {"id":10,"map":{"k1":10,"k2":20}}""";

        var d = Json.from(jsonStr, Data1.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data1(Long id, Map<String, Integer> map) {}


    @Test
    void testObjectMap() {

        var jsonStr = """
            {"id":10,"map":{"k1":{"givenName":"g1","familyName":"f1"},"k2":{"givenName":"g2","familyName":"f2"}}}""";
        var d = Json.from(jsonStr, Data2.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data2(Long id, Map<String, FullName> map) {}


    @Test
    void testObjectValueMap() {

        var jsonStr = """
            {"id":10,"map":{"k1":{"givenName":"g1","familyName":"f1"},"k2":{"givenName":"g2","familyName":"f2"}}}""";
        var d = Json.from(jsonStr, Data3.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data3(Long id, Map<String, FullName> map) {}


    @Test
    void testObjectKeyMap() {

        var jsonStr = """
            {"id":9,"map":[{"givenName":"g1","familyName":"f1"},1,{"givenName":"g2","familyName":"f2"},2]}""";
        var d = Json.from(jsonStr, Data4.class);
        assertEquals(jsonStr, Json.stringifyOf(d));
    }

    @JsonStruct
    public record Data4(Long id, Map<FullName, Integer> map) {}

}
