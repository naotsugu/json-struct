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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * JsonObject.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonObject extends JsonStructure {

    /**
     * Associates the specified json value with the specified key in this object.
     * @param name key with which the specified json value is to be associated
     * @param value json value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    JsonValue put(String name, JsonValue value);


    /**
     * Gets the json value to which the specified name is mapped.
     * @param name the name whose associated value is to be returned
     * @return the json value
     */
    JsonValue get(String name);


    /**
     * Gets a {@link Set} view of the mappings contained in this map.
     * @return a set view of the mappings contained in this map
     */
    Set<Map.Entry<String, JsonValue>> entrySet();


    /**
     * Create a new JsonObject instance.
     * @return a new JsonObject instance
     */
    static JsonObject of() {
        return new JsonObjectImpl();
    }


    /**
     * JsonObject implementation.
     */
    class JsonObjectImpl extends LinkedHashMap<String, JsonValue> implements JsonObject {

        @Override
        public JsonValue get(String name) {
            return super.get(name);
        }
    }

}
