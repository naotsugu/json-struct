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
import com.mammb.code.jsonstruct.JsonStructException;
import java.math.*;
import java.net.*;
import java.nio.file.*;
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

    /** the name of builtin classes. */
    public static final Set<String> typeNames = typeNames();


    /**
     * Gets the builtin class names.
     * @return the builtin class names
     */
    private static Set<String> typeNames() {
        Set<String> set = new HashSet<>();
        // java.lang.*
        set.add(String.class.getCanonicalName());
        set.add(CharSequence.class.getCanonicalName());
        set.add(Byte.class.getCanonicalName());
        set.add(Byte.TYPE.getCanonicalName());
        set.add(Boolean.class.getCanonicalName());
        set.add(Boolean.TYPE.getCanonicalName());
        set.add(Character.class.getCanonicalName());
        set.add(Character.TYPE.getCanonicalName());
        set.add(Double.class.getCanonicalName());
        set.add(Double.TYPE.getCanonicalName());
        set.add(Float.class.getCanonicalName());
        set.add(Float.TYPE.getCanonicalName());
        set.add(Integer.class.getCanonicalName());
        set.add(Integer.TYPE.getCanonicalName());
        set.add(Long.class.getCanonicalName());
        set.add(Long.TYPE.getCanonicalName());
        set.add(Short.class.getCanonicalName());
        set.add(Short.TYPE.getCanonicalName());
        set.add(Number.class.getCanonicalName());
        // java.math.*
        set.add(BigDecimal.class.getCanonicalName());
        set.add(BigInteger.class.getCanonicalName());
        // java.nio.file.*
        set.add(Path.class.getCanonicalName());
        // java.net.*
        set.add(URI.class.getCanonicalName());
        set.add(URL.class.getCanonicalName());
        // java.util.*
        set.add(UUID.class.getCanonicalName());
        set.add(Date.class.getCanonicalName());
        set.add(Calendar.class.getCanonicalName());
        set.add(OptionalDouble.class.getCanonicalName());
        set.add(OptionalInt.class.getCanonicalName());
        set.add(OptionalLong.class.getCanonicalName());
        set.add(TimeZone.class.getCanonicalName());
        // java.time.*
        set.add(Duration.class.getCanonicalName());
        set.add(Period.class.getCanonicalName());
        set.add(Instant.class.getCanonicalName());
        set.add(LocalDateTime.class.getCanonicalName());
        set.add(LocalDate.class.getCanonicalName());
        set.add(LocalTime.class.getCanonicalName());
        set.add(OffsetDateTime.class.getCanonicalName());
        set.add(OffsetTime.class.getCanonicalName());
        set.add(ZonedDateTime.class.getCanonicalName());
        set.add(ZoneId.class.getCanonicalName());
        set.add(ZoneOffset.class.getCanonicalName());

        return Collections.unmodifiableSet(set);
    }


    /**
     * Gets the builtin converter.
     * @param clazz the target class
     * @return the converter
     */
    public static Function<JsonValue, ?> to(Class<?> clazz) {
        return switch (clazz.getCanonicalName()) {
            case "java.lang.String"             -> v -> v.toString();
            case "byte", "java.lang.Byte"       -> v -> Byte.parseByte(v.toString());
            case "boolean", "java.lang.Boolean" -> v -> v.equals(JsonValue.TRUE);
            case "double", "java.lang.Double"   -> v -> Double.parseDouble(v.toString());
            case "float", "java.lang.Float"     -> v -> Float.parseFloat(v.toString());
            case "int", "java.lang.Integer"     -> v -> asNs(v).getInt();
            case "long", "java.lang.Long"       -> v -> asNs(v).getLong();
            case "short", "java.lang.Short"     -> v -> Short.parseShort(v.toString());
            case "java.math.BigDecimal",
                 "java.lang.Number"             -> v -> asNs(v).getBigDecimal();
            case "java.math.BigInteger"         -> v -> asNs(v).getBigDecimal().toBigInteger();
            case "java.util.OptionalDouble"     -> v -> v.equals(JsonValue.NULL) ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(v.toString()));
            case "java.util.OptionalInt"        -> v -> v.equals(JsonValue.NULL) ? OptionalInt.empty() : OptionalInt.of(asNs(v).getInt());
            case "java.util.OptionalLong"       -> v -> v.equals(JsonValue.NULL) ? OptionalLong.empty() : OptionalLong.of(asNs(v).getLong());

            case "char", "java.lang.Character"  -> v -> asCs(v).chars()[0];
            case "java.util.Date"               -> v -> asDate(v.toString());
            case "java.util.Calendar"           -> v -> asCalendar(v.toString());
            case "java.util.TimeZone"           -> v -> asTimeZone(v.toString());
            case "java.time.Instant"            -> v -> Instant.from(DateTimeFormatter.ISO_INSTANT.withZone(UTC).withLocale(locale).parse(v.toString()));
            case "java.time.LocalDateTime"      -> v -> LocalDateTime.parse(v.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale));
            case "java.time.LocalDate"          -> v -> LocalDate.parse(v.toString(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
            case "java.time.LocalTime"          -> v -> LocalTime.parse(v.toString(), DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale));
            case "java.time.OffsetDateTime"     -> v -> OffsetDateTime.parse(v.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale));
            case "java.time.OffsetTime"         -> v -> OffsetTime.parse(v.toString(), DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale));
            case "java.time.ZonedDateTime"      -> v -> ZonedDateTime.parse(v.toString(), DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale));
            case "java.time.ZoneId"             -> v -> ZoneId.of(v.toString());
            case "java.time.ZoneOffset"         -> v -> ZoneOffset.of(v.toString());
            case "java.time.Duration"           -> v -> Duration.parse(v.toString());
            case "java.nio.file.Path"           -> v -> Paths.get(v.toString());
            case "java.time.Period"             -> v -> Period.parse(v.toString());
            case "java.net.URI"                 -> v -> URI.create(v.toString());
            case "java.net.URL"                 -> v -> trying(() -> new URL(v.toString()));
            case "java.util.UUID"               -> v -> UUID.fromString(v.toString());
            default                             -> v -> null;
        };
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
