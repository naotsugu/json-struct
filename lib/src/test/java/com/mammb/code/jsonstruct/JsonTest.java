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

import com.mammb.code.jsonstruct.testdata.*;
import org.junit.jupiter.api.Test;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case of {@link Json}.
 *
 * @author Naotsugu Kobayashi
 */
class JsonTest {

    @Test
    void testBook() {

        var json = Json.of(Book.class);
        var jsonStr = """
            {"name":"bookName"}
            """;

        var book = json.fromJson(jsonStr);
        assertEquals("bookName", book.getName());

        assertEquals(jsonStr.trim(), json.toJson(book));
    }


    @Test
    void testFood() {

        var json = Json.of(Food.class);
        var jsonStr = """
            {"name":"foodName","materials":["material0","material1"]}
            """;

        var food = json.fromJson(jsonStr);
        assertEquals("foodName", food.name());
        assertEquals("material0", food.materials().get(0));
        assertEquals("material1", food.materials().get(1));

        assertEquals(jsonStr.trim(), json.toJson(food));
    }


    @Test
    void testPerson() {

        var json = Json.of(Person.class);
        var jsonStr = """
            {"fullName":{"givenName":"Bob","familyName":"Dylan"},"age":81,"gender":"MALE"}
            """;

        var person = json.fromJson(jsonStr);
        assertEquals("Bob", person.fullName().givenName());
        assertEquals("Dylan", person.fullName().familyName());
        assertEquals(81, person.age());
        assertEquals(Gender.MALE, person.gender());

        assertEquals(jsonStr.trim(), json.toJson(person));

    }


    @Test
    void testPet() {
        var json = Json.of(Pet.class);
        var jsonStr = """
            {"name":"Bob","owners":{"owner1":{"givenName":"a","familyName":"b"},"owner2":{"givenName":"c","familyName":"d"}}}
            """;
        var pet = json.fromJson(jsonStr);
        assertEquals("Bob", pet.getName());
        Iterator<Map.Entry<String, FullName>> iterator =  pet.getOwners().entrySet().iterator();
        var n1 = iterator.next();
        assertEquals("owner1", n1.getKey());
        assertEquals("a", n1.getValue().givenName());
        assertEquals("b", n1.getValue().familyName());
        var n2 = iterator.next();
        assertEquals("owner2", n2.getKey());
        assertEquals("c", n2.getValue().givenName());
        assertEquals("d", n2.getValue().familyName());

        assertEquals(jsonStr.trim(), json.toJson(pet));
    }

}
