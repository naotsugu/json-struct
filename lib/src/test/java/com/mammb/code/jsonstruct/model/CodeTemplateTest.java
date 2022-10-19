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
package com.mammb.code.jsonstruct.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CodeTemplateTest {

    @Test
    void testBind() {
        var code = CodeTemplate.of("List<#{type}> list;");
        code.bind("#{type}", "String");
        assertEquals("List<String> list;", code.content());
    }

    @Test
    void testMultiLineBind() {
        var code = CodeTemplate.of("""
            public static void main(String[] args) {
                #{sout}
            }
            """);
        code.bind("#{sout}", """
            System.out.println("hello");
            System.exit(0);
            """);
        assertEquals("""
            public static void main(String[] args) {
                System.out.println("hello");
                System.exit(0);
            }
            """, code.content());
    }

    @Test
    void testClear() {
        var code = CodeTemplate.of("""
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

        var code1 = CodeTemplate.of("foo");
        var code2 = CodeTemplate.of("bar");
        var code3 = CodeTemplate.of("baz");

        var joined = CodeTemplate.join(", ", code1, code2, code3);

        assertEquals("foo, bar, baz", joined.content());

    }

}
