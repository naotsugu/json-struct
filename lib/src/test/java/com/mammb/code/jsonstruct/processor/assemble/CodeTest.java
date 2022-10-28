package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.processor.assembly.Code;
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
            }""", code.content());
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
            }""", code.content());
    }


}
