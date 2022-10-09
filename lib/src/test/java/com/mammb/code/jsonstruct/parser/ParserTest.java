package com.mammb.code.jsonstruct.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
