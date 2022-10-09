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

import java.util.HashMap;
import java.util.Map;

/**
 * JsonObject.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonObject extends JsonStructure {

    void put(String name, JsonValue value);
    JsonValue get(String name);

    static JsonObject of() {
        return new JsonObjectImpl();
    }


    class JsonObjectImpl implements JsonObject {

        private final Map<String, JsonValue> values = new HashMap<>();

        @Override
        public void put(String name, JsonValue value) {
            values.put(name, value);
        }

        @Override
        public JsonValue get(String name) {
            return values.get(name);
        }
    }

}
