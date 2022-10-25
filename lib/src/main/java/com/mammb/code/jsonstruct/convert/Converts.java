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
package com.mammb.code.jsonstruct.convert;

import com.mammb.code.jsonstruct.parser.CharSource;
import com.mammb.code.jsonstruct.parser.JsonValue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Converts.
 * @author Naotsugu Kobayashi
 */
public class Converts {

    /** The Builtin mapping. */
    private final Map<Class<?>, Function<JsonValue, ?>> map;

    /** The Optional mapping. */
    private final Map<Class<?>, Function<JsonValue, ?>> opt;


    /**
     * Constructor.
     */
    private Converts() {
        this.map = Builtin.mapping();
        this.opt = new HashMap<>();
    }


    /**
     * Create a Converts.
     * @return a Converts
     */
    public static Converts of() {
        return new Converts();
    }


    /**
     * Gets the converter for the given class.
     * @param clazz the Target Classes
     * @param <T> the type of class
     * @return the converter
     */
    @SuppressWarnings("unchecked")
    public <T> Function<JsonValue, T> to(Class<?> clazz) {
        return opt.containsKey(clazz)
            ? (Function<JsonValue, T>) opt.get(clazz)
            : (Function<JsonValue, T>) map.get(clazz);
    }


    /**
     * Add optional mapping
     * @param clazz the Class
     * @param conv the convert
     */
    public void add(Class<?> clazz, Function<String, ?> conv) {
        opt.put(clazz, adapt(conv));
    }


    /**
     * Add all optional mapping
     * @param conv the convert
     */
    public void addAll(Map<Class<?>, Function<String, ?>> conv) {
        conv.forEach(this::add);
    }


    /**
     * Gets the predefined classes fqcn.
     * @return the predefined classes fqcn
     */
    public Set<String> classes() {
        Set<String> set = new HashSet<>();
        map.keySet().forEach(k -> set.add(k.getCanonicalName()));
        opt.keySet().forEach(k -> set.add(k.getCanonicalName()));
        return set;
    }


    private static Function<JsonValue, ?> adapt(Function<String, ?> fun) {
        return (JsonValue v) -> fun.apply(new String(((CharSource) v).chars()));
    }

}
