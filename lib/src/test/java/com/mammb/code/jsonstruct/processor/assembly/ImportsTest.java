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
 * Test for {@link Imports}.
 *
 * @author Naotsugu Kobayashi
 */
class ImportsTest {

    @Test
    void test() {
        var imports = Imports.of();
        assertEquals("String", imports.apply("java.lang.String"));

        imports.add("import java.util.function.Function;");
        imports.add("java.util.function.Consumer");
        imports.marge(Imports.of("java.util.Arrays"));

        var str = imports.toString();
        assertEquals("""
           import java.util.Arrays;
           import java.util.function.Consumer;
           import java.util.function.Function;""", str);
    }

    @Test
    void testGenericsImport() {
        var imports = Imports.of();
        var ret = imports.apply("java.util.Map<java.util.List<java.lang.String>,java.util.List<java.lang.Integer>>");

        assertEquals("Map<List<String>,List<Integer>>", ret);

        var str = imports.toString();
        assertEquals("""
           import java.util.List;
           import java.util.Map;""", str);
    }

}
