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
    private final Map<Class<?>, Function<JsonValue, ?>> typeMap;

    private final Map<Class<?>, Function<?, CharSequence>> stringifyMap;

    /** The Optional mapping. */
    private final Map<Class<?>, Function<JsonValue, ?>> typeOptMap;

    private final Map<Class<?>, Function<?, CharSequence>> stringifyOptMap;


    /**
     * Constructor.
     */
    private Converts() {
        this.typeMap = BuiltinType.map();
        this.stringifyMap = BuiltinStringify.map();
        this.typeOptMap = new HashMap<>();
        this.stringifyOptMap =  new HashMap<>();
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
        return typeOptMap.containsKey(clazz)
            ? (Function<JsonValue, T>) typeOptMap.get(clazz)
            : (Function<JsonValue, T>) typeMap.get(clazz);
    }

    /**
     * Gets the stringify for the given object.
     * @param object the target object
     * @param <T> the type of class
     * @return the converter
     */
    @SuppressWarnings("unchecked")
    public <T> CharSequence stringify(T object) {
        return stringifyOptMap.containsKey(object.getClass())
            ? ((Function<T, CharSequence>) stringifyOptMap.get(object.getClass())).apply(object)
            : ((Function<T, CharSequence>) stringifyMap.get(object.getClass())).apply(object);
    }


    /**
     * Add optional mapping
     * @param clazz the Class
     * @param conv the convert
     */
    public void add(Class<?> clazz, Function<String, ?> conv) {
        typeOptMap.put(clazz, adapt(conv));
    }


    /**
     * Gets the predefined classes fqcn.
     * @return the predefined classes fqcn
     */
    public Set<String> typeClasses() {
        Set<String> set = new HashSet<>();
        typeMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
        typeOptMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
        return set;
    }


    /**
     * Gets the predefined stringify classes fqcn.
     * @return the predefined stringify classes fqcn
     */
    public Set<String> stringifyClasses() {
        Set<String> set = new HashSet<>();
        stringifyMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
        stringifyOptMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
        return set;
    }

    private static Function<JsonValue, ?> adapt(Function<String, ?> fun) {
        return (JsonValue v) -> fun.apply(new String(((CharSource) v).chars()));
    }

}
