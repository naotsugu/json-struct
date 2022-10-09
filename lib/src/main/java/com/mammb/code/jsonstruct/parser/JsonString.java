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

    static JsonString of(CharSource cs) {
        return new JsonStringImpl(cs);
    }

    class JsonStringImpl implements JsonString {
        private final CharSource source;
        public JsonStringImpl(CharSource cs) {
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

}
