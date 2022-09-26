package com.mammb.code.jsonstruct;

import java.io.Reader;

public class Tokenizer {

    private final Buffer buffer;

    Tokenizer(Reader reader) {
        this.buffer = Buffer.of(reader);
    }

    JsonToken next() {
        int ch = buffer.read();

        JsonToken token = switch (ch) {
            case '"' -> readString();
            case '{' -> JsonToken.CURLYOPEN;
            case '[' -> JsonToken.SQUAREOPEN;
            case ':' -> JsonToken.COLON;
            case ',' -> JsonToken.COMMA;
            case 't' -> readTrue();
            case 'f' -> readFalse();
            case 'n' -> readNull();
            case ']' -> JsonToken.SQUARECLOSE;
            case '}' -> JsonToken.CURLYCLOSE;
            case '0','1','2','3','4','5','6','7','8','9','-' -> readNumber(ch);
            case 0x20, 0x09, 0x0a, 0x0d -> next(); // whitespace
            case -1 -> JsonToken.EOF;
            default -> throw unexpectedChar(ch);
        };
        return token;
    }


    private JsonToken readString() {
        // TODO
        return JsonToken.STRING;
    }

    private JsonToken readNumber(int ch)  {
        // TODO
        return JsonToken.NUMBER;
    }


    private JsonToken readTrue() {
        if (buffer.read() != 'r') throw unexpectedChar('r');
        if (buffer.read() != 'u') throw unexpectedChar('u');
        if (buffer.read() != 'e') throw unexpectedChar('e');
        return JsonToken.TRUE;
    }

    private JsonToken readFalse() {
        if (buffer.read() != 'a') throw unexpectedChar('a');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        if (buffer.read() != 's') throw unexpectedChar('s');
        if (buffer.read() != 'e') throw unexpectedChar('e');
        return JsonToken.FALSE;
    }

    private JsonToken readNull() {
        if (buffer.read() != 'u') throw unexpectedChar('u');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        if (buffer.read() != 'l') throw unexpectedChar('l');
        return JsonToken.NULL;
    }

    private RuntimeException unexpectedChar(int ch) {
        return new RuntimeException("Unexpected char");
    }

}
