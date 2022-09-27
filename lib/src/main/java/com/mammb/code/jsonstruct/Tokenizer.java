package com.mammb.code.jsonstruct;

import java.io.Reader;
import static com.mammb.code.jsonstruct.TokenType.*;

public class Tokenizer {

    private final Buffer buffer;

    Tokenizer(Reader reader) {
        this.buffer = Buffer.of(reader);
    }

    Token<?> next() {
        int ch = buffer.read();
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
            int ch = buffer.read();
            if (ch == '"') {
                break;
            } else if (ch == '\\') {
                sb.append(unescape());
            } else if (ch >= 0x20) {
                sb.append(ch);
            } else {
                throw unexpectedChar(ch);
            }
        }
        return Token.cs(sb);
    }

    private Token<Number> readNumber(int ch)  {
        // TODO
        return Token.number(0);
    }


    private Token<Void> readTrue() {
        if (buffer.read() != 'r') throw unexpectedChar('r');
        if (buffer.read() != 'u') throw unexpectedChar('u');
        if (buffer.read() != 'e') throw unexpectedChar('e');
        return Token.type(TRUE);
    }

    private Token<Void> readFalse() {
        if (buffer.read() != 'a') throw unexpectedChar('a');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        if (buffer.read() != 's') throw unexpectedChar('s');
        if (buffer.read() != 'e') throw unexpectedChar('e');
        return Token.type(FALSE);
    }

    private Token<Void> readNull() {
        if (buffer.read() != 'u') throw unexpectedChar('u');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        return Token.type(NULL);
    }

    private RuntimeException unexpectedChar(int ch) {
        return new RuntimeException("Unexpected char");
    }

    private int unescape() {
        int ch = buffer.read();
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
                    int ch3 = buffer.read();
                    int digit = (ch3 >= 0 && ch3 < Hex.TBL.length) ? Hex.TBL[ch3] : -1;
                    if (digit < 0) throw unexpectedChar(ch3);
                    unicode = (unicode << 4) | digit;
                }
                yield unicode;
            }
            default -> throw unexpectedChar(ch);
        };
    }

}
