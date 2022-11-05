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

import java.io.IOException;
import java.util.function.Consumer;

/**
 * StringifyBuilder.
 *
 * @author Naotsugu Kobayashi
 */
public class StringifyBuilder {

    /** Appendable. */
    private final Appendable appendable;

    /** Converts. */
    private final Converts convert;


    /**
     * Constructor.
     * @param appendable Appendable
     * @param convert Converts
     */
    private StringifyBuilder(Appendable appendable, Converts convert) {
        this.appendable = appendable;
        this.convert = convert;
    }


    /**
     * Create a new StringifyBuilder.
     * @param appendable Appendable
     * @param convert Converts
     * @return a new StringifyBuilder
     */
    public static StringifyBuilder of(Appendable appendable, Converts convert) {
        return new StringifyBuilder(appendable, convert);
    }


    /**
     * Append object to this builder.
     * @param object the object to append
     * @return this builder
     */
    public StringifyBuilder appendObj(Object object) {
        if (object instanceof CharSequence) {
            // bypass
            appendOn('"');
            appendEscOn((CharSequence) object);
            appendOn('"');
        } else {
            convert.stringify(object, this);
        }
        return this;
    }


    /**
     * Append string as function.
     * @param fun the function
     * @return this builder
     */
    public StringifyBuilder appendFun(Consumer<StringifyBuilder> fun) {
        fun.accept(this);
        return this;
    }


    /**
     * Append CharSequence to this builder.
     * @param cs the CharSequence
     * @return this builder
     */
    public StringifyBuilder append(CharSequence cs) {
        appendOn(cs);
        return this;
    }


    /**
     * Append a character to this builder.
     * @param ch a character
     * @return this builder
     */
    public StringifyBuilder append(char ch) {
        appendOn(ch);
        return this;
    }


    /**
     * Append null string to this builder.
     * @return this builder
     */
    StringifyBuilder appendNull() {
        appendOn("null");
        return this;
    }


    /**
     * Append the non string value to this builder.
     * @param object the value to be appended
     * @return this builder
     */
    StringifyBuilder appendNum(Object object) {
        appendOn(String.valueOf(object));
        return this;
    }


    /**
     * Append the non string value to this builder.
     * @param cs the value to be appended
     * @return this builder
     */
    StringifyBuilder appendNum(CharSequence cs) {
        appendOn(cs);
        return this;
    }


    /**
     * Append object as string to this builder.
     * @param object the value to be appended
     * @return this builder
     */
    StringifyBuilder appendStr(Object object) {
        if (object == null) {
            return appendNull();
        }
        appendOn('"');
        appendEscOn(String.valueOf(object));
        appendOn('"');
        return this;
    }


    /**
     * Append CharSequence to this builder.
     * @param cs the value to be appended
     * @return this builder
     */
    StringifyBuilder appendStr(CharSequence cs) {
        if (cs == null) {
            return appendNull();
        }
        appendOn('"');
        appendEscOn(cs);
        appendOn('"');
        return this;
    }


    private void appendOn(CharSequence cs) {
        try {
            appendable.append(cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void appendOn(char ch) {
        try {
            appendable.append(ch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void appendEscOn(CharSequence cs) {
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
                appendOn(cs.subSequence(begin, end));
                if (i == len)  break;
            }

            switch (c) {
                case '"', '\\' -> { appendOn('\\'); appendOn(c); }
                case '\b' -> appendOn("\\b");
                case '\f' -> appendOn("\\f");
                case '\n' -> appendOn("\\n");
                case '\r' -> appendOn("\\r");
                case '\t' -> appendOn("\\t");
                default -> {
                    String hex = "000" + Integer.toHexString(c);
                    appendOn("\\u");
                    appendOn(hex.substring(hex.length() - 4));
                }
            }
        }
    }

}
