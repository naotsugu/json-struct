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

import java.util.*;
import java.util.function.Function;

/**
 * Converts.
 * @author Naotsugu Kobayashi
 */
public class Converts {

    /** The Builtin objectify map. */
    private final Map<Class<?>, Function<JsonValue, ?>> objectifyMap;

    /** The Builtin stringify map. */
    private final Map<Class<?>, Function<?, CharSequence>> stringifyMap;

    /** The Optional objectify map. */
    private final Map<Class<?>, Function<JsonValue, ?>> objectifyOptMap;

    /** The Optional stringify map. */
    private final Map<Class<?>, Function<?, CharSequence>> stringifyOptMap;


    /**
     * Constructor.
     */
    private Converts() {
        this.objectifyMap = BuiltinObjectify.map();
        this.stringifyMap = BuiltinStringify.map();
        this.objectifyOptMap = new HashMap<>();
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
     * Gets an objectify converter for the given class.
     * @param clazz the Target Classes
     * @param <T> the type of class
     * @return the converter
     */
    @SuppressWarnings("unchecked")
    public <T> Function<JsonValue, T> to(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return v -> null;
        }
        if (!objectifyOptMap.isEmpty() && objectifyOptMap.containsKey(clazz)) {
            return (Function<JsonValue, T>) objectifyOptMap.get(clazz);
        }
        if (objectifyMap.containsKey(clazz)) {
            return (Function<JsonValue, T>) objectifyMap.get(clazz);
        }
        return v -> null;
    }


    /**
     * Gets the stringify for the given object.
     * @param object the target object
     * @param <T> the type of class
     * @return the converter
     */
    @SuppressWarnings("unchecked")
    public <T> CharSequence stringify(T object) {
        if (Objects.isNull(object)) {
            return "null";
        }
        if (object instanceof Enum<?> en) {
            return "\"" + en.name() + "\"";
        }
        if (!stringifyOptMap.isEmpty() && stringifyOptMap.containsKey(object.getClass())) {
            return ((Function<T, CharSequence>) stringifyOptMap.get(object.getClass())).apply(object);
        }
        if (stringifyMap.containsKey(object.getClass())) {
            return ((Function<T, CharSequence>) stringifyMap.get(object.getClass())).apply(object);
        }
        return "\"" + object + "\"";
    }


    /**
     * Add optional mapping
     * @param clazz the Class
     * @param conv the convert
     */
    public void add(Class<?> clazz, Function<String, ?> conv) {
        objectifyOptMap.put(clazz, adapt(conv));
    }


    /**
     * Add optional stringify mapping
     * @param clazz the Class
     * @param conv the convert
     */
    public void addStringify(Class<?> clazz, Function<?, CharSequence> conv) {
        stringifyOptMap.put(clazz, conv);
    }


    /**
     * Gets the predefined classes fqcn.
     * @return the predefined classes fqcn
     */
    public Set<String> typeClasses() {
        Set<String> set = new HashSet<>();
        objectifyMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
        objectifyOptMap.keySet().forEach(k -> set.add(k.getCanonicalName()));
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


    /**
     * Transform function
     * @param fun the string convert function
     * @return the JsonValue convert function
     */
    private static Function<JsonValue, ?> adapt(Function<String, ?> fun) {
        return (JsonValue v) -> fun.apply(new String(((CharSource) v).chars()));
    }

}
