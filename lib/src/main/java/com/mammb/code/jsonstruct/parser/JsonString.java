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

/**
 * JsonString.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonString extends JsonValue, CharSource {

    /**
     * Create a new JsonString instance with the specified {@link CharSource}.
     * @param cs the specified {@link CharSource}
     * @return a new JsonString instance
     */
    static JsonString of(CharSource cs) {
        return new JsonStringImpl(cs);
    }


    /**
     * Create a new JsonString instance with the specified String.
     * @param str the specified String
     * @return a new JsonString instance
     */
    static JsonString of(String str) {
        return new JsonStringRaw(str);
    }


    /**
     * JsonObject implementation.
     */
    class JsonStringImpl implements JsonString {

        private final CharSource source;

        private JsonStringImpl(CharSource cs) {
            source = cs;
        }

        @Override
        public char[] chars() {
            return source.chars();
        }

        @Override
        public String toString() {
            return source.toString();
        }

    }


    /**
     * JsonObject implementation.
     */
    class JsonStringRaw implements JsonString {

        private final String source;

        private JsonStringRaw(String str) {
            source = str;
        }

        @Override
        public char[] chars() {
            return source.toCharArray();
        }

        @Override
        public String toString() {
            return source.toString();
        }

    }

}
