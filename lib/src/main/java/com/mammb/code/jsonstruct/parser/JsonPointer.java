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

/**
 * JsonPointer.
 * @author Naotsugu Kobayashi
 */
public class JsonPointer {

    private final List<String> tokens;
    private final CharSequence val;

    private JsonPointer(CharSequence val) {
        this.val = Objects.requireNonNull(val);
        this.tokens = split(val);
        if (!"".equals(tokens.get(0))) {
            throw new RuntimeException();
        }
    }

    public static JsonPointer of(CharSequence val) {
        return new JsonPointer(val);
    }

    private static List<String> split(CharSequence cs) {
        int off = 0;
        int next = 0;
        var str = cs.toString();
        var list = new ArrayList<String>();
        while ((next = str.indexOf('/', off)) != -1) {
            list.add(unescape(str.substring(off, next)));
            off = next + 1;
        }
        list.add(unescape(str.substring(off)));
        return list;
    }


    private static String unescape(String str) {
        return (str.indexOf('~') != -1)
            ? str.replace("~1", "/").replace("~0", "~")
            : str;
    }


    public JsonValue getValue(JsonStructure structure) {
        if (tokens.size() == 1) {
            return structure;
        }
        JsonValue value = structure;
        for (int i = 1; i < tokens.size(); i++) {
            if (value instanceof JsonObject object) {
                value = object.get(tokens.get(i).toString());

            } else if (value instanceof JsonArray array) {
                int index = asIndex(tokens.get(i));
                value = array.get(index);
            } else {
                throw new RuntimeException();
            }
            if (value == null) {
                throw new RuntimeException();
            }
        }
        return value;
    }

    public CharSequence token(int index) {
        return tokens.get(index);
    }

    public int tokenSize() {
        return tokens.size();
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


    static private int asIndex(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException();
        }
        if (token.equals("-")) {
            return -1;
        }
        if (token.equals("0")) {
            return 0;
        }
        if (token.charAt(0) == '+' || token.charAt(0) == '-') {
            throw new RuntimeException();
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException ex) {
            throw new RuntimeException();
        }
    }

}
