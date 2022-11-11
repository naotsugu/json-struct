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
package com.mammb.code.jsonstruct;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JsonStructConvertTest.
 * @see com.mammb.code.jsonstruct.testdata.CustomConverts
 * @author Naotsugu Kobayashi
 */
public class JsonStructConvertTest {

    @Test
    void testCustomConvert() {
        var json = Json.of(Data1.class);
        var jsonStr = """
            {"dateTime":"2022/10/20 12:34:56"}""";
        var d = json.from(jsonStr);
        assertEquals("2022-10-20T12:34:56", d.dateTime.toString());
        assertEquals(jsonStr.trim(), json.stringify(d));
    }

    @JsonStruct
    public record Data1(LocalDateTime dateTime) {}

}
