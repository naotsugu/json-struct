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
package com.mammb.code.jsonstruct.converter;

import com.mammb.code.jsonstruct.parser.CharSource;
import com.mammb.code.jsonstruct.parser.JsonPointer;
import com.mammb.code.jsonstruct.parser.JsonStructure;
import com.mammb.code.jsonstruct.parser.JsonValue;
import com.mammb.code.jsonstruct.parser.NumberSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Converters.
 * @author Naotsugu Kobayashi
 */
public class Converters {

    private final Map<Class<?>, Converter<?, ?>> map;

    public Converters() {
        this.map = builtin();
    }

    @SuppressWarnings("unchecked")
    private <T> T to(Class<T> clazz, JsonValue value) {
        return ((Converter<JsonValue, T>) map.get(clazz)).apply(value);
    }

    public <T> T to(Class<T> clazz, String point, JsonStructure json) {
        return to(clazz, JsonPointer.of(point).getValue(json));

    }

    private static Map<Class<?>, Converter<?, ?>> builtin() {
        Map<Class<?>, Converter<?, ?>> map = new HashMap<>();
        map.put(String.class,  (CharSource s) -> new String(s.chars()));
        map.put(Integer.class, (NumberSource s) -> s.getInt());
        map.put(Integer.TYPE,  (NumberSource s) -> s.getInt());
        map.put(Long.class,    (NumberSource s) -> s.getLong());
        map.put(Long.TYPE,     (NumberSource s) -> s.getLong());
        return map;
    }


}
