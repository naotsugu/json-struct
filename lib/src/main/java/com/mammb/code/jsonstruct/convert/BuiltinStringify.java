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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 * Builtin serializes.
 * @author Naotsugu Kobayashi
 */
public class BuiltinStringify {

    public static Map<Class<?>, Function<?, CharSequence>> map() {
        Map<Class<?>, Function<?, CharSequence>> map = new HashMap<>();
        map.put(Byte.class,       String::valueOf);
        map.put(Byte.TYPE,        v -> String.valueOf((byte) v));
        map.put(BigDecimal.class, String::valueOf);
        map.put(BigInteger.class, String::valueOf);
        map.put(Boolean.class,    String::valueOf);
        map.put(Boolean.TYPE,     v -> String.valueOf((boolean) v));
        map.put(Calendar.class, v -> "");
        map.put(Character.class,  String::valueOf);
        map.put(Character.TYPE,   v -> String.valueOf((char) v));
        map.put(Date.class, v -> "");
        map.put(Double.class,     String::valueOf);
        map.put(Double.TYPE,      v -> String.valueOf((double) v));
        map.put(Duration.class, v -> "");
        map.put(Float.class,      String::valueOf);
        map.put(Float.TYPE,       v -> String.valueOf((float) v));
        map.put(Integer.class,    String::valueOf);
        map.put(Integer.TYPE,     v -> String.valueOf((int) v));
        map.put(Instant.class, v -> "");
        map.put(LocalDateTime.class, v -> "");
        map.put(LocalDate.class, v -> "");
        map.put(LocalTime.class, v -> "");
        map.put(Long.class,       String::valueOf);
        map.put(Long.TYPE,        v -> String.valueOf((long) v));
        map.put(Number.class,     v -> "");
        map.put(OffsetDateTime.class, v -> "");
        map.put(OffsetTime.class, v -> "");
        map.put(OptionalDouble.class, v -> "");
        map.put(OptionalInt.class, v -> "");
        map.put(OptionalLong.class, v -> "");
        map.put(Path.class, v -> "");
        map.put(Period.class, v -> "");
        map.put(Short.class,      String::valueOf);
        map.put(Short.TYPE,       v -> String.valueOf((short) v));
        map.put(String.class, Object::toString);
        map.put(TimeZone.class, v -> "");
        map.put(URI.class, v -> "");
        map.put(URL.class, v -> "");
        map.put(UUID.class, v -> "");
        map.put(ZonedDateTime.class, v -> "");
        map.put(ZoneId.class, v -> "");
        map.put(ZoneOffset.class, v -> "");

        return map;
    }

}
