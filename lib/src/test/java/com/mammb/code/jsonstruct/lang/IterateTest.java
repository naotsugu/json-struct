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
package com.mammb.code.jsonstruct.lang;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Iterate}.
 * @author Naotsugu Kobayashi
 */
class IterateTest {

    @Test
    void testIterate() {
        for (Iterate.Entry<String> e : Iterate.of(List.of("e1", "e2", "e3"))) {
            if (e.isFirst()) {
                assertEquals("e1", e.value());
            }
            if (e.index() == 1) {
                assertEquals("e2", e.value());
            }
            if (e.isLast()) {
                assertEquals("e3", e.value());
            }
        }
    }
}
