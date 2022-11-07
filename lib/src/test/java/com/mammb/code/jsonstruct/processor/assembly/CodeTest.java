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
package com.mammb.code.jsonstruct.processor.assembly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Code}.
 * @author Naotsugu Kobayashi
 */
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
