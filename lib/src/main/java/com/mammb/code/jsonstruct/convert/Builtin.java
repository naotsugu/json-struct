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
import com.mammb.code.jsonstruct.parser.NumberSource;
import com.mammb.code.jsonstruct.processor.JsonStructException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Builtin.
 * @author Naotsugu Kobayashi
 */
public class Builtin {

    static Locale locale = Locale.getDefault();

    /**
     * Create a builtin mappings.
     * @return the builtin mappings
     */
    public static Map<Class<?>, Function<JsonValue, ?>> mapping() {


        Map<Class<?>, Function<JsonValue, ?>> map = new HashMap<>();

        map.put(Byte.class,       v -> Byte.parseByte(str(v)));
        map.put(Byte.TYPE,        v -> Byte.parseByte(str(v)));
        map.put(BigDecimal.class, v -> asNs(v).getBigDecimal());
        map.put(BigInteger.class, v -> asNs(v).getBigDecimal().toBigInteger());

        map.put(String.class,     v -> str(v));
        map.put(Integer.class,    v -> asNs(v).getInt());
        map.put(Integer.TYPE,     v -> asNs(v).getInt());
        map.put(Long.class,       v -> asNs(v).getLong());
        map.put(Long.TYPE,        v -> asNs(v).getLong());
        map.put(Boolean.class,    v -> v.equals(JsonValue.TRUE));
        map.put(Boolean.TYPE,     v -> v.equals(JsonValue.TRUE));
        map.put(Character.class,  v -> asCs(v).chars()[0]);
        map.put(Character.TYPE,   v -> asCs(v).chars()[0]);

        map.put(LocalDate.class,      v -> LocalDate.parse(str(v), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale)));
        map.put(LocalTime.class,      v -> LocalTime.parse(str(v), DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale)));
        map.put(LocalDateTime.class,  v -> LocalDateTime.parse(str(v), DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale)));
        map.put(OffsetTime.class,     v -> OffsetTime.parse(str(v), DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale)));
        map.put(OffsetDateTime.class, v -> OffsetDateTime.parse(str(v), DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale)));

        map.put(Period.class,    v -> Period.parse(str(v)));

        map.put(Path.class,      v -> Paths.get(str(v)));
        map.put(URI.class,       v -> URI.create(str(v)));
        map.put(URL.class,       v -> tried(() -> new URL(str(v))));
        map.put(UUID.class,      v -> UUID.fromString(str(v)));
        return map;
    }


    private static String str(JsonValue val) {
        if (val instanceof CharSource cs) {
            return new String(cs.chars());
        } else {
            throw new JsonStructException("Illegal value.[{}]", val);
        }
    }


    private static CharSource asCs(JsonValue val) {
        if (val instanceof CharSource cs) {
            return cs;
        } else {
            throw new JsonStructException("Illegal value.[{}]", val);
        }
    }


    private static NumberSource asNs(JsonValue val) {
        if (val instanceof NumberSource ns) {
            return ns;
        } else {
            throw new JsonStructException("Illegal value.[{}]", val);
        }
    }


    public static <T> T tried(ThrowsSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private interface ThrowsSupplier<T> {
        T get() throws Exception;
    }

}
