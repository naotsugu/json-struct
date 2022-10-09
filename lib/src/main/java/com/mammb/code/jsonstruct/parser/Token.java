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
 * Token.
 *
 * @author Naotsugu Kobayashi
 */
class Token {

    public final Type type;

    Token(Type type) {
        this.type = type;
    }

    public static Token string(CharSource source) {
        return new Str(source);
    }

    public static Token number(CharSource source, boolean frac, boolean exp) {
        return new Num(source, frac, exp);
    }

    public static Token of(Type type) {
        return switch (type) {
            case CURLY_OPEN   -> Token.CURLY_OPEN;
            case SQUARE_OPEN  -> Token.SQUARE_OPEN;
            case CURLY_CLOSE  -> Token.CURLY_CLOSE;
            case SQUARE_CLOSE -> Token.SQUARE_CLOSE;
            case COLON -> Token.COLON;
            case COMMA -> Token.COMMA;
            case TRUE  -> Token.TRUE;
            case FALSE -> Token.FALSE;
            case NULL  -> Token.NULL;
            case EOF   -> Token.EOF;
            default -> throw new IllegalArgumentException();
        };
    }

    private static final Token TRUE  = new Token(Type.TRUE);
    private static final Token FALSE = new Token(Type.FALSE);
    private static final Token NULL  = new Token(Type.NULL);
    private static final Token EOF   = new Token(Type.EOF);
    private static final Token COLON = new Token(Type.COLON);
    private static final Token COMMA = new Token(Type.COMMA);
    private static final Token CURLY_OPEN = new Token(Type.CURLY_OPEN);
    private static final Token CURLY_CLOSE = new Token(Type.CURLY_CLOSE);
    private static final Token SQUARE_OPEN = new Token(Type.SQUARE_OPEN);
    private static final Token SQUARE_CLOSE = new Token(Type.SQUARE_CLOSE);

    public enum Type {
        CURLY_OPEN, SQUARE_OPEN, COLON, COMMA, STRING, NUMBER, TRUE, FALSE, NULL, CURLY_CLOSE, SQUARE_CLOSE, EOF;
    }


    static class Str extends Token implements CharSource {

        private final CharSource source;
        private char[] chars;

        private Str(CharSource source) {
            super(Type.STRING);
            this.source = source;
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
