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
import com.mammb.code.jsonstruct.testdata.Pet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonPrettyWriterTest.
 *
 * @author Naotsugu Kobayashi
 */
class JsonPrettyWriterTest {

    @Test
    void testPrettyString() {
        var ret = JsonPrettyWriter.toPrettyString("""
            {"str":"a\\",1","obj":{"n1":"v1","n2":"v2"},"list1":["11","12"],"list2":["21","22"]}""");
        assertEquals("""
            {
              "str": "a\\",1",
              "obj": {
                "n1": "v1",
                "n2": "v2"
              },
              "list1": [
                "11",
                "12"
              ],
              "list2": [
                "21",
                "22"
              ]
            }""", ret);
    }


    @Test
    void testPrettyWriter() throws IOException {

        var pet = Pet.of("a", Map.of("1", new FullName("a", "b")));
        var writer = new StringWriter();
        Json.stringify(pet, JsonPrettyWriter.of(writer));

        assertEquals("""
            {
              "name": "a",
              "owners": {
                "1": {
                  "givenName": "a",
                  "familyName": "b"
                }
              }
            }""", writer.toString());
    }

}
