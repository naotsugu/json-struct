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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.zone.ZoneRulesException;
import java.util.*;
import java.util.function.Function;

/**
 * Builtin deserializes.
 * @author Naotsugu Kobayashi
 */
public class BuiltinObjectify {

    /** Locale. */
    private static final Locale locale = Locale.getDefault();

    /** UTC Zone. */
    private static final ZoneId UTC = ZoneId.of("UTC");


    /**
     * Create a builtin mappings.
     * @return the builtin mappings
     */
    public static Map<Class<?>, Function<JsonValue, ?>> map() {

        Map<Class<?>, Function<JsonValue, ?>> map = new HashMap<>();

        map.put(Byte.class,          v -> Byte.parseByte(str(v)));
        map.put(Byte.TYPE,           v -> Byte.parseByte(str(v)));
        map.put(BigDecimal.class,    v -> asNs(v).getBigDecimal());
        map.put(BigInteger.class,    v -> asNs(v).getBigDecimal().toBigInteger());
        map.put(Boolean.class,       v -> v.equals(JsonValue.TRUE));
        map.put(Boolean.TYPE,        v -> v.equals(JsonValue.TRUE));
        map.put(Calendar.class,      v -> asCalendar(str(v)));
        map.put(Character.class,     v -> asCs(v).chars()[0]);
        map.put(Character.TYPE,      v -> asCs(v).chars()[0]);
        map.put(Date.class,          v -> asDate(str(v)));
        map.put(Double.class,        v -> Double.parseDouble(str(v)));
        map.put(Double.TYPE,         v -> Double.parseDouble(str(v)));
        map.put(Duration.class,      v -> Duration.parse(str(v)));
        map.put(Float.class,         v -> Float.parseFloat(str(v)));
        map.put(Float.TYPE,          v -> Float.parseFloat(str(v)));
        map.put(Integer.class,       v -> asNs(v).getInt());
        map.put(Integer.TYPE,        v -> asNs(v).getInt());
        map.put(Instant.class,       v -> Instant.from(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(locale).parse(str(v))));
        map.put(LocalDateTime.class, v -> LocalDateTime.parse(str(v), DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale)));
        map.put(LocalDate.class,     v -> LocalDate.parse(str(v), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale)));
        map.put(LocalTime.class,     v -> LocalTime.parse(str(v), DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale)));
        map.put(Long.class,          v -> asNs(v).getLong());
        map.put(Long.TYPE,           v -> asNs(v).getLong());
        map.put(Number.class,        v -> asNs(v).getBigDecimal());
        map.put(OffsetDateTime.class,v -> OffsetDateTime.parse(str(v), DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale)));
        map.put(OffsetTime.class,    v -> OffsetTime.parse(str(v), DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale)));
        map.put(OptionalDouble.class,v -> v.equals(JsonValue.NULL) ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(str(v))));
        map.put(OptionalInt.class,   v -> v.equals(JsonValue.NULL) ? OptionalInt.empty() : OptionalInt.of(asNs(v).getInt()));
        map.put(OptionalLong.class,  v -> v.equals(JsonValue.NULL) ? OptionalLong.empty() : OptionalLong.of(asNs(v).getLong()));
        map.put(Path.class,          v -> Paths.get(str(v)));
        map.put(Period.class,        v -> Period.parse(str(v)));
        map.put(Short.class,         v -> Short.parseShort(str(v)));
        map.put(Short.TYPE,          v -> Short.parseShort(str(v)));
        map.put(String.class,        v -> str(v));
        map.put(TimeZone.class,      v -> asTimeZone(str(v)));
        map.put(URI.class,           v -> URI.create(str(v)));
        map.put(URL.class,           v -> trying(() -> new URL(str(v))));
        map.put(UUID.class,          v -> UUID.fromString(str(v)));
        map.put(ZonedDateTime.class, v -> ZonedDateTime.parse(str(v), DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale)));
        map.put(ZoneId.class,        v -> ZoneId.of(str(v)));
        map.put(ZoneOffset.class,    v -> ZoneOffset.of(str(v)));

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


    private static <T> T trying(ThrowsSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new JsonStructException("zone parse error.", e);
        }
    }


    private interface ThrowsSupplier<T> {
        T get() throws Exception;
    }


    private static Calendar asCalendar(String str) {
        DateTimeFormatter formatter = str.contains("T")
            ? DateTimeFormatter.ISO_DATE_TIME
            : DateTimeFormatter.ISO_DATE;

        final TemporalAccessor parsed = formatter.parse(str);
        LocalTime time = parsed.query(TemporalQueries.localTime());
        ZoneId zone = parsed.query(TemporalQueries.zone());
        if (zone == null) {
            zone = UTC;
        }
        if (time == null) {
            time = LocalTime.parse("00:00:00");
        }
        ZonedDateTime result = LocalDate.from(parsed).atTime(time).atZone(zone);
        return GregorianCalendar.from(result);
    }


    private static Date asDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withLocale(locale);
        ZonedDateTime parsed = (formatter.getZone() == null)
            ? ZonedDateTime.parse(str, formatter.withZone(UTC))
            : ZonedDateTime.parse(str, formatter);
        return Date.from(parsed.toInstant());
    }


    private static TimeZone asTimeZone(String str) {
        try {
            final ZoneId zoneId = ZoneId.of(str);
            final ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(zoneId);
            return new SimpleTimeZone(zonedDateTime.getOffset().getTotalSeconds() * 1000, zoneId.getId());
        } catch (ZoneRulesException e) {
            throw new JsonStructException("zone parse error.", e);
        }
    }

}
