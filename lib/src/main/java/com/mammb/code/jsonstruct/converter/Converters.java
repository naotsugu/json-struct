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
import com.mammb.code.jsonstruct.parser.JsonValue;
import com.mammb.code.jsonstruct.parser.NumberSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Converters.
 * @author Naotsugu Kobayashi
 */
public class Converters {

    private static final Map<Class<?>, Converter<JsonValue, ?>> map = builtin();
    private Map<Class<?>, Converter<?, ?>> opt;


    private Converters() {
        this.opt = Collections.emptyMap();
    }

    public static Converters of() {
        return new Converters();
    }

    public void add(Class<?> clazz, Converter<JsonValue, ?> converter) {
        if (opt == Collections.EMPTY_MAP) {
            opt = new HashMap<>();
        }
        opt.put(clazz, converter);
    }


    @SuppressWarnings("unchecked")
    public <T> Converter<JsonValue, T> to(Class<?> clazz) {
        return opt.containsKey(clazz)
            ? (Converter<JsonValue, T>) opt.get(clazz)
            : (Converter<JsonValue, T>) map.get(clazz);
    }


    private static Map<Class<?>, Converter<JsonValue, ?>> builtin() {
        Map<Class<?>, Converter<JsonValue, ?>> map = new HashMap<>();
        map.put(String.class,  v -> new String(asCs(v).chars()));
        map.put(Integer.class, v -> asNs(v).getInt());
        map.put(Integer.TYPE,  v -> asNs(v).getInt());
        map.put(Long.class,    v -> asNs(v).getLong());
        map.put(Long.TYPE,     v -> asNs(v).getLong());
        return map;
    }

    private static CharSource asCs(JsonValue val) {
        if (val instanceof CharSource cs) {
            return cs;
        } else {
            throw new RuntimeException();
        }
    }

    private static NumberSource asNs(JsonValue val) {
        if (val instanceof NumberSource ns) {
            return ns;
        } else {
            throw new RuntimeException();
        }
    }

}
