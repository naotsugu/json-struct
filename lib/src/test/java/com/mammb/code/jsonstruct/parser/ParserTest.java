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
package com.mammb.code.jsonstruct.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Parser}.
 *
 * @author Naotsugu Kobayashi
 */
class ParserTest {

    @Test
    void testParse() {
        var p = Parser.of("""
        {
          "key1" : "val1",
          "key2" : 100
        }
        """);

        var obj = (JsonObject) p.parse();
        assertTrue(obj.get("key1") instanceof JsonString);
        assertEquals("val1", obj.get("key1").toString());

        assertTrue(obj.get("key2") instanceof JsonNumber);
        assertEquals(100, ((NumberSource) obj.get("key2")).getInt());

    }

}
