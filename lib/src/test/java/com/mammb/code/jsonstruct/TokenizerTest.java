package com.mammb.code.jsonstruct;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;
import static com.mammb.code.jsonstruct.Token.Type.*;

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

        var tokenizer = Tokenizer.of(r);

        assertEquals(CURLYOPEN, tokenizer.next().type());

        var token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("name", token.toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("John Smith", token.toString());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("price", token.toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(NUMBER, token.type());
        assertEquals(2300, ((TokenNumber)token).asInt());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("color", token.toString());
        assertEquals(COLON, tokenizer.next().type());

        assertEquals(SQUAREOPEN, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("red", token.toString());
        assertEquals(COMMA, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("green", token.toString());
        assertEquals(SQUARECLOSE, tokenizer.next().type());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("deleted", token.toString());
        assertEquals(COLON, tokenizer.next().type());
        token = tokenizer.next();
        assertEquals(FALSE, token.type());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("shipTo", token.toString());
        assertEquals(COLON, tokenizer.next().type());

        assertEquals(CURLYOPEN, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("name", token.toString());
        assertEquals(COLON, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("Jane \"Smith\"", token.toString());
        assertEquals(COMMA, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("zip", token.toString());
        assertEquals(COLON, tokenizer.next().type());

        token = tokenizer.next();
        assertEquals(STRING, token.type());
        assertEquals("12345", token.toString());

        assertEquals(CURLYCLOSE, tokenizer.next().type());

        assertEquals(CURLYCLOSE, tokenizer.next().type());

        assertEquals(EOF, tokenizer.next().type());
    }
}