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
package com.mammb.code.jsonstruct.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonArray.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonArray extends JsonStructure {

    void add(JsonValue value);

    static JsonArray of() {
        return new JsonArrayImpl();
    }


    class JsonArrayImpl implements JsonArray {

        private final List<JsonValue> values = new ArrayList<>();

        @Override
        public void add(JsonValue value) {
            values.add(value);
        }
    }

}
