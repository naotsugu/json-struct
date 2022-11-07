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
 * Test for {@link BackingCodeTest}.
 *
 * @author Naotsugu Kobayashi
 */
class BackingCodeTest {

    @Test
    void of() {
        var bc = BackingCode.of(Code.of("c1"), Code.of("b1"));
        assertEquals("c1", bc.code().content());
        assertEquals("b1", bc.backingCodes().content());
        bc.add(BackingCode.of(Code.of("c2"), Code.of("b2")));
        assertEquals("c1\nc2", bc.code().content());
        assertEquals("b1\nb2", bc.backingCodes().content());
    }
}
