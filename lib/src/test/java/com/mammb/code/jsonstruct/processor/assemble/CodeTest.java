package com.mammb.code.jsonstruct.processor.assemble;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeTest {

    @Test
    void testBind() {
        var code = Code.of("List<#{type}> list;");
        code.interpolateType("#{type}", "String");
        assertEquals("List<String> list;", code.content());
    }

    @Test
    void testMultiLineBind() {
        var code = Code.of("""
            public static void main(String[] args) {
                #{sout}
            }
            """);
        code.interpolate("#{sout}", Code.of("""
            System.out.println("hello");
            System.exit(0);
            """));
        assertEquals("""
            public static void main(String[] args) {
                System.out.println("hello");
                System.exit(0);
            }
            """, code.content());
    }

    @Test
    void testClear() {
        var code = Code.of("""
            public static void main(String[] args) {
                #{sout}
            }
            """);
        code.clear("#{sout}");
        assertEquals("""
            public static void main(String[] args) {
            }
            """, code.content());
    }

    @Test
    void testJoin() {

        var code1 = Code.of("foo");
        var code2 = Code.of("bar");
        var code3 = Code.of("baz");

        var joined = Code.join(",", code1, code2, code3);

        assertEquals("""
                foo,
                bar,
                baz
                """, joined.content());

    }

}
