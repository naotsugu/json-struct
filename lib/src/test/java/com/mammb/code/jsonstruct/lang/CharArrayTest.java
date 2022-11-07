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
package com.mammb.code.jsonstruct.lang;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link CharArray}.
 * @author Naotsugu Kobayashi
 */
class CharArrayTest {

    @Test
    void testAdd() {

        var ca = CharArray.of();

        ca.add('1');
        assertEquals(1, ca.length());
        assertEquals('1', ca.get(0));

        ca.add(new char[] { '2', '3' });
        assertEquals(3, ca.length());
        assertEquals('2', ca.get(1));
        assertEquals('3', ca.get(2));

    }


    @Test
    void testAddCharReader() {

        var ca = CharArray.of();

        ca.add(StringReader.of("abc"), 2);
        assertEquals(2, ca.length());
        assertEquals('a', ca.get(0));
        assertEquals('b', ca.get(1));

    }

}
