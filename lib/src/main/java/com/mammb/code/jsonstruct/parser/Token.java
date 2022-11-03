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
import java.util.Objects;

/**
 * The Json token.
 * @author Naotsugu Kobayashi
 */
class Token {

    /** the type of token. */
    public final Type type;


    /**
     * Constructor.
     * @param type the type of token
     */
    Token(Type type) {
        this.type = type;
    }


    /**
     * Create a string token.
     * @param source the source of token
     * @return a string token
     */
    public static Token string(CharSource source) {
        return new Str(source);
    }


    /**
     * Create a string token.
     * @param str the source of token
     * @return a string token
     */
    public static Token string(String str) {
        return new StrRaw(str);
    }


    /**
     * Create a number token.
     * @param source the source of token
     * @param frac fraction?
     * @param exp exponential?
     * @return a number token
     */
    public static Token number(CharSource source, boolean frac, boolean exp) {
        return new Num(source, frac, exp);
    }


    /** Token true. */
    static final Token TRUE  = new Token(Type.TRUE);
    /** Token false. */
    static final Token FALSE = new Token(Type.FALSE);
    /** Token null. */
    static final Token NULL  = new Token(Type.NULL);
    /** Token eof. */
    static final Token EOF   = new Token(Type.EOF);
    /** Token colon. */
    static final Token COLON = new Token(Type.COLON);
    /** Token comma. */
    static final Token COMMA = new Token(Type.COMMA);
    /** Token curly open. */
    static final Token CURLY_OPEN = new Token(Type.CURLY_OPEN);
    /** Token curly close. */
    static final Token CURLY_CLOSE = new Token(Type.CURLY_CLOSE);
    /** Token square open. */
    static final Token SQUARE_OPEN = new Token(Type.SQUARE_OPEN);
    /** Token square close. */
    static final Token SQUARE_CLOSE = new Token(Type.SQUARE_CLOSE);


    /**
     * Token type.
     */
    public enum Type {
        /** curly open {@code `{`}. */
        CURLY_OPEN,
        /** square open {@code `[`}. */
        SQUARE_OPEN,
        /** colon {@code `:`}. */
        COLON,
        /** comma {@code `,`}. */
        COMMA,
        /** string. */
        STRING,
        /** number. */
        NUMBER,
        /** true. */
        TRUE,
        /** false. */
        FALSE,
        /** null. */
        NULL,
        /** curly close {@code `}`}. */
        CURLY_CLOSE,
        /** square close {@code `]`}. */
        SQUARE_CLOSE, EOF;
    }


    /**
     * String token.
     */
    static class Str extends Token implements CharSource {

        private final CharSource source;
        private String str;

        private Str(CharSource source) {
            super(Type.STRING);
            this.source = Objects.requireNonNull(source);
        }

        @Override
        public char[] chars() {
            return toString().toCharArray();
        }

        @Override
        public String toString() {
            String ret = str;
            if (ret == null) {
                str = ret = source.toString();
                return ret;
            }
            return ret;
        }

    }


    /**
     * String token.
     */
    static class StrRaw extends Token implements CharSource {

        private final String str;

        private StrRaw(String str) {
            super(Type.STRING);
            this.str = Objects.requireNonNull(str);
        }

        @Override
        public char[] chars() {
            return str.toCharArray();
        }

        @Override
        public String toString() {
            return str;
        }

    }


    /**
     * Number token.
     */
    static class Num extends Token implements NumberSource {

        private final CharSource source;
        private final boolean frac;
        private final boolean exp;
        private char[] chars;
        private BigDecimal bd;

        private Num(CharSource source, boolean frac, boolean exp) {
            super(Type.NUMBER);
            this.source = source;
            this.frac = frac;
            this.exp = exp;
        }

        @Override
        public int getInt() {
            char[] ca = chars();
            boolean minus = ca.length > 0 && ca[0] == '-';

            if (!(frac || exp) && (ca.length <= 9 || (minus && ca.length == 10))) {
                int num = 0;
                for (int i = minus ? 1 : 0; i < ca.length; i++) {
                    num = num * 10 + (ca[i] - '0');
                }
                return minus ? -num : num;
            } else {
                return getBigDecimal().intValue();
            }
        }

        @Override
        public long getLong() {
            char[] ca = chars();
            boolean minus = ca.length > 0 && ca[0] == '-';

            if (!(frac || exp) && (ca.length <= 18 || (minus && ca.length == 19))) {
                long num = 0;
                for(int i = minus ? 1 : 0; i < ca.length; i++) {
                    num = num * 10 + (ca[i] - '0');
                }
                return minus ? -num : num;
            } else {
                return getBigDecimal().longValue();
            }
        }

        @Override
        public BigDecimal getBigDecimal() {
            return (bd == null) ? bd = new BigDecimal(chars()) : bd;
        }

        @Override
        public char[] chars() {
            char[] ret = chars;
            if (ret == null) {
                chars = ret = source.chars();
                return ret;
            }
            return ret;
        }

        @Override
        public String toString() {
            return new String(chars());
        }

    }

}
