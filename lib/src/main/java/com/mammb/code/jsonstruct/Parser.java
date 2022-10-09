package com.mammb.code.jsonstruct;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Consumer;

import static com.mammb.code.jsonstruct.Token.Type.*;

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
            case CURLY_OPEN  -> parseObject(JsonObject.of());
            case SQUARE_OPEN -> parseArray(JsonArray.of());
            case EOF -> null;
            default  -> throw new RuntimeException();
        };
    }


    JsonObject parseObject(JsonObject obj) {
        String name = "";
        for (;;) {
            prev = curr;
            curr = tokenizer.next();
            if (prev == null || prev.type != COLON) {
                name = "";
            }
            switch (curr.type) {
                case CURLY_OPEN ->
                    keying(name, k -> obj.put(k, parseObject(JsonObject.of())));
                case SQUARE_OPEN ->
                    keying(name, k -> obj.put(k, parseArray(JsonArray.of())));
                case NUMBER ->
                    keying(name, k -> obj.put(k, JsonNumber.of((NumberSource) curr)));
                case TRUE ->
                    keying(name, k -> obj.put(k, JsonValue.TRUE));
                case FALSE ->
                    keying(name, k -> obj.put(k, JsonValue.FALSE));
                case NULL ->
                    keying(name, k -> obj.put(k, JsonValue.NULL));
                case STRING -> {
                    if (!name.isEmpty())
                        obj.put(name, JsonString.of((CharSource) curr));
                }
                case COLON -> {
                    if (prev != null && prev.type == STRING) name = prev.toString();
                    else throw new RuntimeException();
                }
                case COMMA -> {
                    if (!name.isEmpty() || prev == null ||
                        prev.type == CURLY_OPEN || prev.type == SQUARE_OPEN ||
                        prev.type == COLON || prev.type == COMMA)
                        throw new RuntimeException();
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
                case TRUE ->
                    array.add(JsonValue.TRUE);
                case FALSE ->
                    array.add(JsonValue.FALSE);
                case NULL ->
                    array.add(JsonValue.NULL);
                case COMMA -> {
                    if (prev == null ||
                        prev.type == CURLY_OPEN || prev.type == SQUARE_OPEN ||
                        prev.type == COMMA)
                        throw new RuntimeException();
                }
                case SQUARE_CLOSE -> {
                    return array;
                }
                default -> throw new RuntimeException();
            }
        }
    }


    private void keying(String key, Consumer<String> consumer) {
        if (key.isEmpty()) {
            throw new RuntimeException();
        }
        consumer.accept(key);
    }

}
