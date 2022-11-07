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

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link StringifyBuilder}.
 * @author Naotsugu Kobayashi
 */
class StringifyBuilderTest {

    @Test
    void testBuilder() {
        Appendable appendable = new StringBuilder();
        var sb = StringifyBuilder.of(appendable, Converts.of());
        sb.append('{');
        sb.appendStr("name").append(':').appendObj(LocalDate.now()).append(',');
        sb.appendStr("age").append(':').appendNum(30).append(',');
        sb.appendStr("tel").append(':').appendNull().append(',');
        sb.appendStr("note").append(':').appendStr("\r\n");
        sb.append('}');

        assertEquals("""
            {"name":"2022-11-07","age":30,"tel":null,"note":"\\r\\n"}""", appendable.toString());
    }

}
