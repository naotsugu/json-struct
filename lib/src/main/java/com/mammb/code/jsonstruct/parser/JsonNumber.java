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

import java.math.BigDecimal;

/**
 * JsonNumber.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonNumber extends JsonValue, NumberSource {

    static JsonNumber of(NumberSource ns) {
        return new JsonNumberImpl(ns);
    }

    class JsonNumberImpl implements JsonNumber {
        private final NumberSource source;
        public JsonNumberImpl(NumberSource ns) {
            this.source = ns;
        }

        @Override
        public int getInt() {
            return source.getInt();
        }

        @Override
        public long getLong() {
            return source.getLong();
        }

        @Override
        public BigDecimal getBigDecimal() {
            return source.getBigDecimal();
        }

        @Override
        public String toString() {
            return source.toString();
        }

        @Override
        public char[] chars() {
            return source.chars();
        }
    }

}
