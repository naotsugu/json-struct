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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HexFormat;

import static com.mammb.code.jsonstruct.parser.Token.Type.*;

/**
 * Tokenizer.
 *
 * @author Naotsugu Kobayashi
 */
class Tokenizer {

    /** Reader. */
    private final Reader reader;

    /** CharArray. */
    private final CharArray ca;

    /** Previously read character. */
    private Integer prev;

    /** number of current line. */
    private int line;

    /** number of current column. */
    private int colm;


    /**
     * Constructor.
     * @param reader the reader
     * @param ca CharArray
     */
    private Tokenizer(Reader reader, CharArray ca) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.ca = ca;
        this.prev = null;
        this.line = 1;
        this.colm = 0;
    }


    /**
     * Create a new Tokenizer.
     * @param reader the reader
     * @return a new Tokenizer
     */
    static Tokenizer of(Reader reader) {
        return new Tokenizer(reader, CharArray.of());
    }


    /**
     * Create a new Tokenizer.
     * @param reader the reader
     * @param ca the CharArray
     * @return a new Tokenizer
     */
    static Tokenizer of(Reader reader, CharArray ca) {
        return new Tokenizer(reader, ca);
    }


    /**
     * Get the number of current line.
     * @return the number of current line
     */
    int getLine() {
        return line;
    }


    /**
     * Get the number of current column.
     * @return the number of current column
     */
    int getColm() {
        return colm;
    }


    /**
     * Read a next token.
     * @return a next token
     */
    Token next() {
        int ch = read();
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
            case ' ', '\t', '\r', '\n' -> next();
            case -1 -> Token.EOF;
            default -> throw syntaxError(ch);
        };
    }


    /**
     * Read string.
     * @return token
     */
    private Token readString() {
        int start = ca.length();
        for (;;) {
            int ch = read();
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                ca.add((char) unescape(read()));
            } else if (ch >= 0x20) {
                ca.add((char) ch);
            } else {
                throw syntaxError(ch);
            }
        }
        return Token.string(ca.subArray(start));
    }

    /**
     * Read number.
     * @return token
     */
    private Token readNumber(int ch)  {

        int start = ca.length();

        boolean frac = false;
        boolean exp = false;

        if (ch == '-') {
            ca.add((char) ch);
            ch = read();
            if (ch < '0' || ch > '9') throw syntaxError(ch);
        }

        if (ch == '0') {
            ca.add((char) ch);
            ch = read();
        } else {
            do {
                ca.add((char) ch);
                ch = read();
            } while (ch >= '0' && ch <= '9');
        }

        if (ch == '.') {
            frac = true;
            int count = 0;
            do {
                ca.add((char) ch);
                ch = read();
                count++;
            } while (ch >= '0' && ch <= '9');
            if (count == 1) throw syntaxError(ch);
        }

        if (ch == 'e' || ch == 'E') {
            exp = true;
            ca.add((char) ch);
            ch = read();
            if (ch == '+' || ch == '-') {
                ca.add((char) ch);
                ch = read();
            }
            int count;
            for (count = 0; ch >= '0' && ch <= '9'; count++) {
                ca.add((char) ch);
                ch = read();
            }
            if (count == 0) throw syntaxError(ch);
        }

        prev = ch;

        return Token.number(ca.subArray(start), frac, exp);

    }


    /**
     * Read true.
     * @return token
     */
    private Token readTrue() {
        if (read() != 'r') throw syntaxError('r');
        if (read() != 'u') throw syntaxError('u');
        if (read() != 'e') throw syntaxError('e');
        return Token.TRUE;
    }


    /**
     * Read false.
     * @return token
     */
    private Token readFalse() {
        if (read() != 'a') throw syntaxError('a');
        if (read() != 'l') throw syntaxError('l');
        if (read() != 's') throw syntaxError('s');
        if (read() != 'e') throw syntaxError('e');
        return Token.FALSE;
    }


    /**
     * Read null.
     * @return token
     */
    private Token readNull() {
        if (read() != 'u') throw syntaxError('u');
        if (read() != 'l') throw syntaxError('l');
        if (read() != 'l') throw syntaxError('l');
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
                    int c = read();
                    unicode = (unicode << 4) | HexFormat.fromHexDigit(c);
                }
                yield unicode;
            }
            default -> throw syntaxError(ch);
        };
    }


    /**
     * Read a next character
     * @return a next character
     */
    private int read() {
        try {
            if (prev != null) {
                int ret = prev;
                prev = null;
                return ret;
            }
            int ret = reader.read();
            if (ret == '\n') {
                line++;
                colm = 0;
            } else {
                colm++;
            }
            return ret;
        } catch (IOException e) {
            throw new JsonParseException(e);
        }
    }


    /**
     * Create a JsonParseException.
     * @param ch character
     * @return a JsonParseException
     */
    private JsonParseException syntaxError(int ch) {
        return new JsonParseException("Unexpected char. [{}] line:{}, column:{}", ch, line, colm);
    }

}
