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
     * @param clazz the target class
     * @param <T> the type of class
     * @return the converter
     */
    @SuppressWarnings("unchecked")
    public <T> Function<JsonValue, T> to(Class<?> clazz) {
        if (clazz == null) {
            return v -> null;
        }
        if (!objectifyOptMap.isEmpty() && objectifyOptMap.containsKey(clazz)) {
            return (Function<JsonValue, T>) objectifyOptMap.get(clazz);
        }
        var fun = (Function<JsonValue, T>) objectifyMap.get(clazz);
        return (fun != null) ? fun : v -> null;
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


    @SuppressWarnings("unchecked")
    public <T> void stringify(T object, StringifyBuilder sb) {
        if (object == null) {
            sb.appendNull();
        } else if (object instanceof Enum<?> en) {
            sb.appendStr(en.name());
        } else if (!stringifyOptMap.isEmpty() && stringifyOptMap.containsKey(object.getClass())) {
            sb.append(((Function<T, CharSequence>) stringifyOptMap.get(object.getClass())).apply(object));
        } else {
            StringifyBuiltin.apply(object, sb);
        }
    }

    /**
     * Gets the default value.
     * @param clazz the target class
     * @param <T> type of class
     * @return the default value
     */
    @SuppressWarnings("unchecked")
    public <T> T defaults(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        if (clazz == Byte.TYPE) {
            byte b = 0;
            return (T) (Object) b;
        }
        if (clazz == Short.TYPE) {
            short s = 0;
            return (T) (Object) s;
        }
        if (clazz == Integer.TYPE) {
            return (T) (Object) 0;
        }
        if (clazz == Long.TYPE) {
            return (T) (Object) 0L;
        }
        if (clazz == Float.TYPE) {
            return (T) (Object) 0.0f;
        }
        if (clazz == Double.TYPE) {
            return (T) (Object) 0.0d;
        }
        if (clazz == Character.TYPE) {
            return (T) (Object) '\u0000';
        }
        if (clazz == Boolean.TYPE) {
            return (T) (Object) false;
        }
        if (clazz == Optional.class) {
            return (T) Optional.empty();
        }
        if (clazz == OptionalDouble.class) {
            return (T) OptionalDouble.empty();
        }
        if (clazz == OptionalLong.class) {
            return (T) OptionalLong.empty();
        }
        if (clazz == OptionalInt.class) {
            return (T) OptionalInt.empty();
        }
        return null;
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
        set.addAll(StringifyBuiltin.typeNames());
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
