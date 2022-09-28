package com.mammb.code.jsonstruct;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static com.mammb.code.jsonstruct.TokenType.*;

class TokenizerTest {

    @Test
    void read() {
        var r = new StringReader("""
                { "name"   : "John Smith",
                  "price"  : 2300,
                  "color"  : [ "red", "green" ],
                  "deleted": false,
                  "shipTo" : { "name" : "Jane \\"Smith\\"",
                               "zip"   : "12345" }
                }
                """);

        var tokenizer = new Tokenizer(r);

        assertEquals(CURLYOPEN, tokenizer.next().type());

        var token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("name", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("John Smith", token.value().toString());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("price", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(NUMBER, token.type());
        assertEquals(2300, token.value());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("color", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());

        assertEquals(SQUAREOPEN, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("red", token.value().toString());
        assertEquals(COMMA, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("green", token.value().toString());
        assertEquals(SQUARECLOSE, tokenizer.next().type());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("deleted", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(FALSE, token.type());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("shipTo", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());

        assertEquals(CURLYOPEN, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("name", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("Jane \"Smith\"", token.value().toString());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("zip", token.value().toString());
        assertEquals(COLON, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("12345", token.value().toString());

        assertEquals(CURLYCLOSE, tokenizer.next().type());

        assertEquals(CURLYCLOSE, tokenizer.next().type());

        assertEquals(EOF, tokenizer.next().type());
    }
}