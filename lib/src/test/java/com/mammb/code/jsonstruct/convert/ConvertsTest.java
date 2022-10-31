package com.mammb.code.jsonstruct.convert;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

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
}
