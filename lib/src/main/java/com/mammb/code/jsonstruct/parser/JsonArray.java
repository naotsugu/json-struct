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
import java.util.Iterator;
import java.util.List;

/**
 * JsonArray.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonArray extends JsonStructure, Iterable<JsonValue> {

    /**
     * Gets the array value at the specified position in this array.
     * @param index index of the value to be returned
     * @return the value at the specified position in this array
     */
    JsonValue get(int index);


    /**
     * Appends the specified JsonValue to the end of this array.
     * @param value JsonValue to be appended to this array
     */
    void add(JsonValue value);


    /**
     * Create a new JsonArray instance.
     * @return a new JsonArray instance
     */
    static JsonArray of() {
        return new JsonArrayImpl();
    }


    /**
     * JsonArray implementation.
     */
    class JsonArrayImpl implements JsonArray {

        private final List<JsonValue> values = new ArrayList<>();

        @Override
        public JsonValue get(int index) {
            return values.get(index);
        }

        @Override
        public void add(JsonValue value) {
            values.add(value);
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public Iterator<JsonValue> iterator() {
            return values.iterator();
        }
    }

}
