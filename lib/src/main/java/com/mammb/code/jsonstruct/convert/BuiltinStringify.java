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

/**
 * Builtin serializes.
 *
 * @author Naotsugu Kobayashi
 */
public class BuiltinStringify {

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
     * Convert with builtin.
     * @param object the target of convert
     * @param sb the StringifyBuilder
     */
    public static void apply(Object object, StringifyBuilder sb) {

        switch (object.getClass().getCanonicalName()) {

            case "java.lang.Byte",
                 "java.lang.Boolean",
                 "java.lang.Double",
                 "java.lang.Float",
                 "java.lang.Integer",
                 "java.lang.Long",
                 "java.lang.Short",
                 "java.math.BigDecimal",
                 "java.math.BigInteger"    -> sb.appendNum(object);
            case "java.lang.Number"        -> sb.appendNum(new BigDecimal(String.valueOf(object)).toString());
            case "byte"                    -> sb.appendNum(Byte.toString((byte) object));
            case "boolean"                 -> sb.appendNum(Boolean.toString((boolean) object));
            case "double"                  -> sb.appendNum(Double.toString((double) object));
            case "float"                   -> sb.appendNum(Float.toString((float) object));
            case "int"                     -> sb.appendNum(Integer.toString((int) object));
            case "long"                    -> sb.appendNum(Long.toString((long) object));
            case "short"                   -> sb.appendNum(Short.toString((short) object));
            case "java.util.OptionalDouble"-> sb.appendNum(((OptionalDouble) object).isPresent() ? String.valueOf(((OptionalDouble) object).getAsDouble()) : "null");
            case "java.util.OptionalInt"   -> sb.appendNum(((OptionalInt) object).isPresent() ? String.valueOf(((OptionalInt) object).getAsInt()) : "null");
            case "java.util.OptionalLong"  -> sb.appendNum(((OptionalLong) object).isPresent() ? String.valueOf(((OptionalLong) object).getAsLong()) : "null");

            case "char"                    -> sb.appendStr(String.valueOf((char) object));
            case "java.util.Date"          -> sb.appendStr(DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).withLocale(locale).format(((Date) object).toInstant()));
            case "java.util.Calendar"      -> sb.appendStr(str((Calendar) object));
            case "java.util.TimeZone"      -> sb.appendStr(((TimeZone) object).getID());
            case "java.time.Instant"       -> sb.appendStr(DateTimeFormatter.ISO_INSTANT.withLocale(locale).format((Instant) object));
            case "java.time.LocalDateTime" -> sb.appendStr(DateTimeFormatter.ISO_LOCAL_DATE_TIME.withLocale(locale).format((LocalDateTime) object));
            case "java.time.LocalDate"     -> sb.appendStr(DateTimeFormatter.ISO_LOCAL_DATE.withZone(UTC).withLocale(locale).format((LocalDate) object));
            case "java.time.LocalTime"     -> sb.appendStr(DateTimeFormatter.ISO_LOCAL_TIME.withLocale(locale).format((LocalTime) object));
            case "java.time.OffsetDateTime"-> sb.appendStr(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(locale).format((OffsetDateTime) object));
            case "java.time.OffsetTime"    -> sb.appendStr(DateTimeFormatter.ISO_OFFSET_TIME.withLocale(locale).format((OffsetTime) object));
            case "java.time.ZonedDateTime" -> sb.appendStr(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(locale).format((ZonedDateTime) object));
            case "java.time.ZoneId"        -> sb.appendStr(((ZoneId) object).getId());
            case "java.time.ZoneOffset"    -> sb.appendStr(((ZoneOffset) object).getId());
            default -> sb.appendStr(object);
        }
    }


    /**
     * Stringify calendar.
     * @param value calendar value
     * @return string
     */
    private static CharSequence str(Calendar value) {
        DateTimeFormatter formatter = value.isSet(Calendar.HOUR) || value.isSet(Calendar.HOUR_OF_DAY)
            ? DateTimeFormatter.ISO_DATE_TIME
            : DateTimeFormatter.ISO_DATE;
        return formatter.withZone(value.getTimeZone().toZoneId()).withLocale(locale)
            .format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTimeInMillis()),
                value.getTimeZone().toZoneId()));
    }

}
