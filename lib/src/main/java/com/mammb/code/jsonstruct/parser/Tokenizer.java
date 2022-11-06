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

import com.mammb.code.jsonstruct.lang.CharArray;
import com.mammb.code.jsonstruct.lang.CharReader;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tokenizer.
 *
 * @author Naotsugu Kobayashi
 */
class Tokenizer {

    private static final ConcurrentHashMap<String, Token> tokenCache = new ConcurrentHashMap<>();

    /** Reader. */
    private final CharReader reader;

    /** CharArray. */
    private final CharArray ca;


    /**
     * Constructor.
     * @param reader the reader
     * @param ca CharArray
     */
    private Tokenizer(CharReader reader, CharArray ca) {
        this.reader = reader;
        this.ca = ca;
    }


    /**
     * Create a new Tokenizer.
     * @param reader the reader
     * @return a new Tokenizer
     */
    static Tokenizer of(CharReader reader) {
        return new Tokenizer(reader, CharArray.of(32));
    }


    /**
     * Create a new Tokenizer.
     * @param reader the reader
     * @param ca the CharArray
     * @return a new Tokenizer
     */
    static Tokenizer of(CharReader reader, CharArray ca) {
        return new Tokenizer(reader, ca);
    }


    /**
     * Read a next token.
     * @return a next token
     */
    Token next() {
        int ch = reader.readNextChar();
        return switch (ch) {
            case '"' -> readString();
            case '{' -> Token.CURLY_OPEN;
            case '[' -> Token.SQUARE_OPEN;
            case ':' -> Token.COLON;
            case ',' -> Token.COMMA;
            case 't' -> readTrue();
            case 'f' -> readFalse();
            case 'n' -> readNull();
            case ']' -> Token.SQUARE_CLOSE;
            case '}' -> Token.CURLY_CLOSE;
            case '0','1','2','3','4','5','6','7','8','9','-' -> readNumber(ch);
            case -1 -> Token.EOF;
            default -> throw syntaxError(ch);
        };
    }


    /**
     * Read string.
     * @return token
     */
    private Token readString() {
        for (;;) {
            int len = reader.length(c -> c >= ' ' && c != '"' && c != '\\');
            ca.add(reader, len);
            int ch = reader.read();
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                ca.add((char) unescape(reader.read()));
            } else {
                throw syntaxError(ch);
            }
        }
        return Token.string(ca.popString());
    }


    /**
     * Read number.
     * @return token
     */
    private Token readNumber(int ch)  {

        boolean frac = false;
        boolean exp = false;

        if (ch == '-') {
            ca.add((char) ch);
            ch = reader.read();
            if (ch < '0' || ch > '9') throw syntaxError(ch);
        }

        if (ch == '0') {
            ca.add((char) ch);
            ch = reader.read();
        } else {
            do {
                ca.add((char) ch);
                ch = reader.read();
            } while (ch >= '0' && ch <= '9');
        }

        if (ch == '.') {
            frac = true;
            int count = 0;
            do {
                ca.add((char) ch);
                ch = reader.read();
                count++;
            } while (ch >= '0' && ch <= '9');
            if (count == 1) throw syntaxError(ch);
        }

        if (ch == 'e' || ch == 'E') {
            exp = true;
            ca.add((char) ch);
            ch = reader.read();
            if (ch == '+' || ch == '-') {
                ca.add((char) ch);
                ch = reader.read();
            }
            int count;
            for (count = 0; ch >= '0' && ch <= '9'; count++) {
                ca.add((char) ch);
                ch = reader.read();
            }
            if (count == 0) throw syntaxError(ch);
        }

        reader.stepBack();
        return Token.number(ca.popChars(), frac, exp);

    }


    /**
     * Read true.
     * @return token
     */
    private Token readTrue() {
        if (reader.read() != 'r') throw syntaxError('r');
        if (reader.read() != 'u') throw syntaxError('u');
        if (reader.read() != 'e') throw syntaxError('e');
        return Token.TRUE;
    }


    /**
     * Read false.
     * @return token
     */
    private Token readFalse() {
        if (reader.read() != 'a') throw syntaxError('a');
        if (reader.read() != 'l') throw syntaxError('l');
        if (reader.read() != 's') throw syntaxError('s');
        if (reader.read() != 'e') throw syntaxError('e');
        return Token.FALSE;
    }


    /**
     * Read null.
     * @return token
     */
    private Token readNull() {
        if (reader.read() != 'u') throw syntaxError('u');
        if (reader.read() != 'l') throw syntaxError('l');
        if (reader.read() != 'l') throw syntaxError('l');
        return Token.NULL;
    }


    /**
     * Unescape.
     * @return unescaped read character
     */
    private int unescape(int ch) {
        return switch (ch) {
            case 'b' -> '\b';
            case 't' -> '\t';
            case 'n' -> '\n';
            case 'f' -> '\f';
            case 'r' -> '\r';
            case '"', '\\', '/' -> ch;
            case 'u' -> {
                int unicode = 0;
                for (int i = 0; i < 4; i++) {
                    int c = reader.read();
                    unicode = (unicode << 4) | HexFormat.fromHexDigit(c);
                }
                yield unicode;
            }
            default -> throw syntaxError(ch);
        };
    }


    /**
     * Create a JsonParseException.
     * @param ch character
     * @return a JsonParseException
     */
    private JsonParseException syntaxError(int ch) {
        return new JsonParseException("Unexpected char. [{}] index:{}", ch, reader.getPosition());
    }

}
