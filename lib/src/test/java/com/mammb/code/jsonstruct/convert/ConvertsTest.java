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
package com.mammb.code.jsonstruct.convert;

import com.mammb.code.jsonstruct.parser.JsonString;
import com.mammb.code.jsonstruct.parser.JsonValue;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Converts}.
 * @author Naotsugu Kobayashi
 */
class ConvertsTest {

    @Test
    void testDefaults() {
        assertEquals((byte) 0, Converts.of().defaults(byte.class));
        assertEquals((short) 0, Converts.of().defaults(short.class));
        assertEquals((int)0, Converts.of().defaults(int.class));
        assertEquals(0L, Converts.of().defaults(long.class));
        assertEquals(0.0f, Converts.of().defaults(float.class));
        assertEquals(0.0d, Converts.of().defaults(double.class));
        assertEquals('\u0000', Converts.of().defaults(char.class));
        assertEquals(false, Converts.of().defaults(boolean.class));
        assertEquals(Optional.empty(), Converts.of().defaults(Optional.class));
        assertEquals(OptionalDouble.empty(), Converts.of().defaults(OptionalDouble.class));
        assertEquals(OptionalLong.empty(), Converts.of().defaults(OptionalLong.class));
        assertEquals(OptionalInt.empty(), Converts.of().defaults(OptionalInt.class));
        assertNull(Converts.of().defaults(String.class));
    }


    @Test
    void testObjectify() {
        LocalDate expected = LocalDate.of(2022,11,7);
        assertEquals(expected,
            Converts.of().to(LocalDate.class).apply(JsonString.of("2022-11-07")));
    }

}
