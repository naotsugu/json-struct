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

class JsonPointerTest {

    @Test
    void test() {
        var pointer = JsonPointer.of("/foo/bar");
        assertEquals(3, pointer.tokenSize());
        assertEquals("", pointer.token(0));
        assertEquals("foo", pointer.token(1));
        assertEquals("bar", pointer.token(2));

        pointer = JsonPointer.of("/~01/~10");
        assertEquals(3, pointer.tokenSize());
        assertEquals("", pointer.token(0));
        assertEquals("~1", pointer.token(1));
        assertEquals("/0", pointer.token(2));

        pointer = JsonPointer.of("/aa/bb/");
        assertEquals(4, pointer.tokenSize());
        assertEquals("", pointer.token(0));
        assertEquals("aa", pointer.token(1));
        assertEquals("bb", pointer.token(2));
        assertEquals("", pointer.token(3));
    }

}
