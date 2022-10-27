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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * Builtin serializes.
 * @author Naotsugu Kobayashi
 */
public class BuiltinStringify {

    private static final Locale locale = Locale.getDefault();
    private static final ZoneId UTC = ZoneId.of("UTC");

    public static Map<Class<?>, Function<?, CharSequence>> map() {
        Map<Class<?>, Function<?, CharSequence>> map = new HashMap<>();
        map.put(Byte.class,          String::valueOf);
        map.put(Byte.TYPE,           v -> String.valueOf((byte) v));
        map.put(BigDecimal.class,    String::valueOf);
        map.put(BigInteger.class,    String::valueOf);
        map.put(Boolean.class,       String::valueOf);
        map.put(Boolean.TYPE,        v -> String.valueOf((boolean) v));
        map.put(Calendar.class,      v -> esc(str((Calendar) v)));
        map.put(Character.class,     v -> esc(String.valueOf(v)));
        map.put(Character.TYPE,      v -> esc(String.valueOf((char) v)));
        map.put(Date.class,          v -> esc(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale).format(((Date) v).toInstant())));
        map.put(Double.class,        String::valueOf);
        map.put(Double.TYPE,         v -> String.valueOf((double) v));
        map.put(Duration.class,      v -> esc(String.valueOf(v)));
        map.put(Float.class,         String::valueOf);
        map.put(Float.TYPE,          v -> String.valueOf((float) v));
        map.put(Integer.class,       String::valueOf);
        map.put(Integer.TYPE,        v -> String.valueOf((int) v));
        map.put(Instant.class,       v -> esc(DateTimeFormatter.ISO_INSTANT.withLocale(locale).format((Instant) v)));
        map.put(LocalDateTime.class, v -> esc(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale).format((LocalDateTime) v)));
        map.put(LocalDate.class,     v -> esc(DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC).withLocale(locale).format((LocalDate) v)));
        map.put(LocalTime.class,     v -> esc(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale).format((LocalTime) v)));
        map.put(Long.class,          String::valueOf);
        map.put(Long.TYPE,           v -> String.valueOf((long) v));
        map.put(Number.class,        v -> new BigDecimal(String.valueOf(v)).toString());
        map.put(OffsetDateTime.class,v -> esc(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format((OffsetDateTime) v)));
        map.put(OffsetTime.class,    v -> esc(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale).format((OffsetTime) v)));
        map.put(OptionalDouble.class,v -> ((OptionalDouble) v).isPresent() ? String.valueOf(((OptionalDouble) v).getAsDouble()) : "null");
        map.put(OptionalInt.class,   v -> ((OptionalInt) v).isPresent() ? String.valueOf(((OptionalInt) v).getAsInt()) : "null");
        map.put(OptionalLong.class,  v -> ((OptionalLong) v).isPresent() ? String.valueOf(((OptionalLong) v).getAsLong()) : "null");
        map.put(Path.class,          v -> esc(String.valueOf(v)));
        map.put(Period.class,        v -> esc(String.valueOf(v)));
        map.put(Short.class,         String::valueOf);
        map.put(Short.TYPE,          v -> String.valueOf((short) v));
        map.put(String.class,        v -> esc((String) v));
        map.put(TimeZone.class,      v -> esc(((TimeZone) v).getID()));
        map.put(URI.class,           v -> esc(String.valueOf(v)));
        map.put(URL.class,           v -> esc(String.valueOf(v)));
        map.put(UUID.class,          v -> esc(String.valueOf(v)));
        map.put(ZonedDateTime.class, v -> esc(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale).format((ZonedDateTime) v)));
        map.put(ZoneId.class,        v -> esc(((ZoneId) v).getId()));
        map.put(ZoneOffset.class,    v -> esc(((ZoneOffset) v).getId()));

        return map;
    }

    private static CharSequence esc(CharSequence cs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            int begin = i, end = i;
            char c = cs.charAt(i);
            while (c >= ' ' && c != '"' && c != '\\') {
                // RFC 4627  unescaped = %x20-21 / %x23-5B / %x5D-10FFFF
                i++; end = i;
                if (i >= len) break;
                c = cs.charAt(i);
            }

            if (begin < end) {
                sb.append(cs.subSequence(begin, end));
                if (i == len)  break;
            }

            switch (c) {
                case '"', '\\' -> sb.append('\\').append(c);
                case '\b' ->  sb.append('\\').append('b');
                case '\f' ->  sb.append('\\').append('f');
                case '\n' ->  sb.append('\\').append('n');
                case '\r' ->  sb.append('\\').append('r');
                case '\t' ->  sb.append('\\').append('t');
                default -> {
                    String hex = "000" + Integer.toHexString(c);
                    String code = "\\u" + hex.substring(hex.length() - 4);
                    sb.append(code);
                }
            }
        }
        sb.append("\"");
        return sb;
    }

    private static CharSequence str(Calendar value) {
        DateTimeFormatter formatter = value.isSet(Calendar.HOUR) || value.isSet(Calendar.HOUR_OF_DAY)
            ? DateTimeFormatter.ISO_DATE_TIME
            : DateTimeFormatter.ISO_DATE;
        return formatter.withZone(value.getTimeZone().toZoneId()).withLocale(locale)
            .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTimeInMillis()),
                value.getTimeZone().toZoneId()));
    }

}
