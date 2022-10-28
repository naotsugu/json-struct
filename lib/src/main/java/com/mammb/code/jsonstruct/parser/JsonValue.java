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
package com.mammb.code.jsonstruct.parser;

import java.util.function.Function;

/**
 * JsonValue.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonValue {

    /** json null. */
    JsonValue NULL = new JsonNull();

    /** json true. */
    JsonValue TRUE = new JsonTrue();

    /** json false. */
    JsonValue FALSE = new JsonFalse();

    /** json null instance. */
    record JsonNull() implements JsonValue { }

    /** json true instance. */
    record JsonTrue() implements JsonValue { }

    /** json false instance. */
    record JsonFalse() implements JsonValue { }


    @Override
    String toString();


    /**
     * Gets the result of a conversion with the specified conversion function.
     * @param conv the conversion function
     * @param <T> the type to convert
     * @return the result of a conversion
     */
    default <T> T as(Function<JsonValue, T> conv) {
        return conv.apply(this);
    }

}
