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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Representation of a JSON Pointer as specified in RFC 6901.
 * A JSON Pointer, when applied to a target {@link JsonValue},
 * defines a reference location in the target.
 *
 * @author Naotsugu Kobayashi
 */
public class JsonPointer {

    /** the token of pointer. */
    private final List<String> tokens;

    /** the value of pointer. */
    private final CharSequence val;


    /**
     * Constructor.
     * @param val the value of pointer
     */
    private JsonPointer(CharSequence val) {
        this.val = Objects.requireNonNull(val);
        this.tokens = split(val);
        if (!"".equals(tokens.get(0))) {
            throw new RuntimeException();
        }
    }


    /**
     * Create a new Pointer for the given pointer value.
     * @param val pointer value
     * @return JsonPointer
     */
    public static JsonPointer of(CharSequence val) {
        return new JsonPointer(val);
    }


    /**
     * Split the pointer value.
     * @param cs the pointer value
     * @return the split pointer value
     */
    private static List<String> split(CharSequence cs) {
        int offset = 0;
        int next = 0;
        var str = cs.toString();
        var list = new ArrayList<String>();
        while ((next = str.indexOf('/', offset)) != -1) {
            list.add(unescape(str.substring(offset, next)));
            offset = next + 1;
        }
        list.add(unescape(str.substring(offset)));
        return list;
    }


    /**
     * Gets whether if there is a value at the referenced location in the specified target.
     * @param structure the target referenced by this {@code JsonPointer}
     * @return {@code true} if this pointer points to a value in a specified structure.
     */
    public boolean containsValue(JsonValue structure) {
        return asValue(structure).isPresent();
    }


    /**
     * Gets the value at the referenced location in the specified target.
     * @param structure the target referenced by this {@code JsonPointer}
     * @return the referenced value in the target
     * @throws JsonParseException if not exists
     */
    public JsonValue getValue(JsonValue structure) {
        return asValue(structure).orElseThrow(JsonParseException::new);
    }


    /**
     * Gets the value at the referenced location in the specified target.
     * @param structure the target referenced by this {@code JsonPointer}
     * @return the referenced value in the target
     */
    public Optional<JsonValue> asValue(JsonValue structure) {
        if (isSelf()) {
            return Optional.of(structure);
        }
        JsonValue value = structure;
        for (int i = 1; i < tokens.size(); i++) {
            if (value instanceof JsonObject object) {
                value = object.get(tokens.get(i));

            } else if (value instanceof JsonArray array) {
                int index = asIndex(tokens.get(i));
                value = array.get(index);
            } else {
                throw new RuntimeException();
            }
            if (value == null) {
                return Optional.empty();
            }
        }
        return Optional.of(value);
    }


    /**
     * Gets the pointer token.
     * @param index the index
     * @return the pointer token
     */
    public CharSequence token(int index) {
        return tokens.get(index);
    }


    /**
     * Gets the size of token.
     * @return the size of token
     */
    public int tokenSize() {
        return tokens.size();
    }


    /**
     * Parse token as index
     * @param token the token
     * @return parsed index
     */
    private int asIndex(String token) {
        if (token == null || token.isBlank()) {
            throw new JsonParseException("parse index error.[{}]{}", token, tokens);
        }
        if (token.equals("-")) {
            return -1;
        }
        if (token.equals("0")) {
            return 0;
        }
        if (token.charAt(0) == '+' || token.charAt(0) == '-') {
            throw new JsonParseException();
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException ex) {
            throw new JsonParseException();
        }
    }


    /**
     * Unescape pointer string.
     * @param str pointer string
     * @return unescaped pointer string
     */
    private static String unescape(String str) {
        return (str.indexOf('~') != -1)
            ? str.replace("~1", "/").replace("~0", "~")
            : str;
    }


    /**
     * Gets whether if this pointer has root path.
     * @return {@code true} if this pointer has root path.
     */
    private boolean isSelf() {
        return tokens.size() <= 1 ||
            (tokens.size() == 2 && tokens.get(0).isBlank() && tokens.get(1).isBlank());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonPointer that = (JsonPointer) o;
        return val.equals(that.val);
    }


    @Override
    public int hashCode() {
        return val.hashCode();
    }

}
