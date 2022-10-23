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

    private final Reader reader;
    private final CharArray ca;
    private Integer prev;

    private int line;
    private int colm;


    Tokenizer(Reader reader, CharArray ca) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.ca = ca;
        this.prev = null;
        this.line = 1;
        this.colm = 0;
    }

    static Tokenizer of(Reader reader) {
        return new Tokenizer(reader, CharArray.of());
    }

    Token next() {
        int ch = read();
        return switch (ch) {
            case '"' -> readString();
            case '{' -> Token.of(CURLY_OPEN);
            case '[' -> Token.of(SQUARE_OPEN);
            case ':' -> Token.of(COLON);
            case ',' -> Token.of(COMMA);
            case 't' -> readTrue();
            case 'f' -> readFalse();
            case 'n' -> readNull();
            case ']' -> Token.of(SQUARE_CLOSE);
            case '}' -> Token.of(CURLY_CLOSE);
            case '0','1','2','3','4','5','6','7','8','9','-' -> readNumber(ch);
            case ' ', '\t', '\r', '\n' -> next();
            case -1 -> Token.of(EOF);
            default -> throw unexpectedChar(ch);
        };
    }


    private Token readString() {
        int start = ca.length();
        for (;;) {
            int ch = read();
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                ca.add((char) unescape());
            } else if (ch >= 0x20) {
                ca.add((char) ch);
            } else {
                throw unexpectedChar(ch);
            }
        }
        return Token.string(ca.subArray(start));
    }


    private Token readNumber(int ch)  {

        int start = ca.length();

        boolean frac = false;
        boolean exp = false;

        if (ch == '-') {
            ca.add((char) ch);
            ch = read();
            if (ch < '0' || ch > '9') throw unexpectedChar(ch);
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
            if (count == 1) throw unexpectedChar(ch);
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
            if (count == 0) throw unexpectedChar(ch);
        }

        prev = ch;

        return Token.number(ca.subArray(start), frac, exp);

    }


    private Token readTrue() {
        if (read() != 'r') throw unexpectedChar('r');
        if (read() != 'u') throw unexpectedChar('u');
        if (read() != 'e') throw unexpectedChar('e');
        return Token.of(TRUE);
    }


    private Token readFalse() {
        if (read() != 'a') throw unexpectedChar('a');
        if (read() != 'l') throw unexpectedChar('l');
        if (read() != 's') throw unexpectedChar('s');
        if (read() != 'e') throw unexpectedChar('e');
        return Token.of(FALSE);
    }


    private Token readNull() {
        if (read() != 'u') throw unexpectedChar('u');
        if (read() != 'l') throw unexpectedChar('l');
        if (read() != 'l') throw unexpectedChar('l');
        return Token.of(NULL);
    }


    private int unescape() {
        int ch = read();
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
            default -> throw unexpectedChar(ch);
        };
    }


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
            throw new RuntimeException(e);
        }
    }


    private RuntimeException unexpectedChar(int ch) {
        return new JsonParseException("Unexpected char.[{}]]", ch);
    }


    int getLine() {
        return line;
    }


    int getColm() {
        return colm;
    }

}
