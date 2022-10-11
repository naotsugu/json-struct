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
