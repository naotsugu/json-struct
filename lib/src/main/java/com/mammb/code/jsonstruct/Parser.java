package com.mammb.code.jsonstruct;

import java.io.Reader;
import java.io.StringReader;

import static com.mammb.code.jsonstruct.Token.Type.STRING;
import static com.mammb.code.jsonstruct.Token.Type.TRUE;

public class Parser {

    private final Tokenizer tokenizer;
    private Token curr;
    private Token prev;

    private Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public static Parser of(Reader reader) {
        return new Parser(Tokenizer.of(reader));
    }

    public static Parser of(CharSequence cs) {
        return new Parser(Tokenizer.of(new StringReader(cs.toString())));
    }

    JsonStructure parse() {
        Token token = tokenizer.next();
        return switch (token.type) {
            case CURLY_OPEN -> parseObject(JsonObject.of());
            case SQUARE_OPEN -> parseArray(JsonArray.of());
            case EOF -> null;
            default -> throw new RuntimeException();
        };
    }

    JsonObject parseObject(JsonObject obj) {
        String key = "";
        for (;;) {
            prev = curr;
            curr = tokenizer.next();
            switch (curr.type) {
                case CURLY_OPEN -> {
                    if (!key.isEmpty()) {
                        obj.put(key, parseObject(JsonObject.of()));
                        key = "";
                    }
                }
                case SQUARE_OPEN -> {
                    if (!key.isEmpty()) {
                        obj.put(key, parseArray(JsonArray.of()));
                        key = "";
                    }
                }
                case STRING -> {
                    if (!key.isEmpty()) {
                        obj.put(key, JsonString.of((CharSource) curr));
                        key = "";
                    }
                }
                case NUMBER -> {
                    if (!key.isEmpty()) {
                        obj.put(key, JsonNumber.of((NumberSource) curr));
                        key = "";
                    }
                }
                case TRUE, FALSE -> {
                    if (!key.isEmpty()) {
                        obj.put(key, (curr.type == TRUE) ? JsonValue.TRUE : JsonValue.FALSE);
                        key = "";
                    }
                }
                case NULL -> {
                    if (!key.isEmpty()) {
                        obj.put(key, JsonValue.NULL);
                        key = "";
                    }
                }
                case COLON -> {
                    if (prev.type == STRING) key = prev.toString();
                    else throw new RuntimeException();
                }
                case COMMA -> {
                    // TODO
                }
                case CURLY_CLOSE -> {
                    return obj;
                }
                default -> throw new RuntimeException();
            }

        }
    }


    JsonArray parseArray(JsonArray array) {
        for (;;) {
            prev = curr;
            curr = tokenizer.next();
            switch (curr.type) {
                case CURLY_OPEN ->
                    array.add(parseObject(JsonObject.of()));
                case SQUARE_OPEN ->
                    array.add(parseArray(JsonArray.of()));
                case STRING ->
                    array.add(JsonString.of((CharSource) curr));
                case NUMBER ->
                    array.add(JsonNumber.of((NumberSource) curr));
                case TRUE, FALSE ->
                    array.add((curr.type == TRUE) ? JsonValue.TRUE : JsonValue.FALSE);
                case NULL ->
                    array.add(JsonValue.NULL);
                case COMMA -> {
                    // TODO
                }
                case SQUARE_CLOSE -> {
                    return array;
                }
                default -> throw new RuntimeException();
            }
        }
    }


}
