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
 * Test for {@link Path}.
 * @author Naotsugu Kobayashi
 */
class PathTest {

    @Test
    void testPath() {

        var path = Path.of("aa", "bb");
        assertFalse(path.isEmpty());
        assertEquals("aaBb", path.camelJoin());
        assertEquals("/aa/bb", path.pointerJoin());
        assertEquals("Optional.ofNullable(aa).map(e -> e.bb()).orElse(null)", path.elvisJoin());

        path.add("cc");
        assertEquals("aaBbCc", path.camelJoin());
        assertEquals("/aa/bb/cc", path.pointerJoin());
        assertEquals("Optional.ofNullable(aa).map(e -> e.bb()).map(e -> e.cc()).orElse(null)", path.elvisJoin());

    }

}
