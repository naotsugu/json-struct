package com.mammb.code.jsonstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;

import static com.mammb.code.jsonstruct.TokenType.*;

public class Tokenizer {

    private final Reader reader;
    private int prev = -2;

    Tokenizer(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
    }

    Token<?> next() {
        int ch = read();
        return switch (ch) {
            case '"' -> readString();
            case '{' -> Token.type(CURLYOPEN);
            case '[' -> Token.type(SQUAREOPEN);
            case ':' -> Token.type(COLON);
            case ',' -> Token.type(COMMA);
            case 't' -> readTrue();
            case 'f' -> readFalse();
            case 'n' -> readNull();
            case ']' -> Token.type(SQUARECLOSE);
            case '}' -> Token.type(CURLYCLOSE);
            case '0','1','2','3','4','5','6','7','8','9','-' -> readNumber(ch);
            case ' ', '\t', '\r', '\n' -> next();
            case -1 -> Token.type(EOF);
            default -> throw unexpectedChar(ch);
        };
    }


    private Token<CharSequence> readString() {
        StringBuilder sb = new StringBuilder();
        for (;;) {
            int ch = read();
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                sb.append((char) unescape());
            } else if (ch >= 0x20) {
                sb.append((char) ch);
            } else {
                throw unexpectedChar(ch);
            }
        }
        return Token.cs(sb); // TODO lazy
    }

    private Token<Number> readNumber(int ch)  {

        boolean minus = false;
        boolean fracOrExp = false;
        StringBuilder sb = new StringBuilder();

        // sign
        if (ch == '-') {
            minus = true;
            sb.append((char) ch);
            ch = read();
            if (ch < '0' || ch >'9') throw unexpectedChar(ch);
        }

        // int
        if (ch == '0') {
            sb.append((char) ch);
            ch = read();
        } else {
            do {
                sb.append((char) ch);
                ch = read();
            } while (ch >= '0' && ch <= '9');
        }

        // frac
        if (ch == '.') {
            fracOrExp = true;
            int count = 0;
            do {
                sb.append((char) ch);
                ch = read();
                count++;
            } while (ch >= '0' && ch <= '9');
            if (count == 1) throw unexpectedChar(ch);
        }

        // exp
        if (ch == 'e' || ch == 'E') {
            fracOrExp = true;
            sb.append((char) ch);
            ch = read();
            if (ch == '+' || ch == '-') {
                sb.append((char) ch);
                ch = read();
            }
            int count;
            for (count = 0; ch >= '0' && ch <= '9'; count++) {
                sb.append((char) ch);
                ch = read();
            }
            if (count == 0) throw unexpectedChar(ch);
        }

        prev = ch;

        if (!fracOrExp && (sb.length() <= 9 || (minus && sb.length() <= 10))) {
            int num = 0;
            for (int i = minus ? 1 : 0; i < sb.length(); i++) {
                num = num * 10 + (sb.charAt(i) - '0');
            }
            return Token.number(minus ? -num : num);
        }
        if (!fracOrExp && (sb.length() <= 18 || (minus && sb.length() <= 19))) {
            long num = 0;
            for (int i = minus ? 1 : 0; i < sb.length(); i++) {
                num = num * 10 + (sb.charAt(i) - '0');
            }
            return Token.number(minus ? -num : num);
        }
        var bd = new BigDecimal(sb.toString()); // TODO lazy
        return Token.number(bd);
    }


    private Token<Void> readTrue() {
        if (read() != 'r') throw unexpectedChar('r');
        if (read() != 'u') throw unexpectedChar('u');
        if (read() != 'e') throw unexpectedChar('e');
        return Token.type(TRUE);
    }

    private Token<Void> readFalse() {
        if (read() != 'a') throw unexpectedChar('a');
        if (read() != 'l') throw unexpectedChar('l');
        if (read() != 's') throw unexpectedChar('s');
        if (read() != 'e') throw unexpectedChar('e');
        return Token.type(FALSE);
    }

    private Token<Void> readNull() {
        if (read() != 'u') throw unexpectedChar('u');
        if (read() != 'l') throw unexpectedChar('l');
        if (read() != 'l') throw unexpectedChar('l');
        return Token.type(NULL);
    }

    private RuntimeException unexpectedChar(int ch) {
        return new RuntimeException("Unexpected char");
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
                    int ch3 = read();
                    int digit = (ch3 >= 0 && ch3 < Hex.TBL.length) ? Hex.TBL[ch3] : -1;
                    if (digit < 0) throw unexpectedChar(ch3);
                    unicode = (unicode << 4) | digit;
                }
                yield unicode;
            }
            default -> throw unexpectedChar(ch);
        };
    }


    int read() {
        try {
            if (prev != -2) {
                int ret = prev;
                prev = -2;
                return ret;
            }
            return reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
