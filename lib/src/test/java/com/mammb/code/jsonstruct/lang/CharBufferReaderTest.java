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
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link CharBufferReader}.
 * @author Naotsugu Kobayashi
 */
class CharBufferReaderTest {

    @Test
    void testRead() {

        var sr = CharBufferReader.of(new StringReader("abc  \tdef"));
        assertEquals('a', sr.read());
        assertEquals('b', sr.read());
        assertEquals('c', sr.read());
        assertEquals(' ', sr.read());
        assertEquals('d', sr.readNextChar());
        sr.skip(1);
        assertEquals('f', sr.readNextChar());
        sr.stepBack();
        assertEquals('f', sr.read());
        assertEquals(9, sr.getPosition());
        assertEquals(-1, sr.read());
    }


    @Test
    void testLength() {
        var sr = CharBufferReader.of(new StringReader("abc def"));
        assertEquals(3, sr.length(c -> c > ' '));
        sr.read();
        assertEquals(2, sr.length(c -> c > ' '));
    }


    @Test
    void testReadChars() {
        var sr = CharBufferReader.of(new StringReader("abc def"));
        char[] chars = new char[5];
        sr.read(chars, 1, 4);
        assertEquals('a', chars[1]);
        assertEquals('b', chars[2]);
        assertEquals('c', chars[3]);
        assertEquals(' ', chars[4]);
    }

}
