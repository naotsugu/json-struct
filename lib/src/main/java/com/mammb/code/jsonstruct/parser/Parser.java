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
import com.mammb.code.jsonstruct.lang.StringReader;
import java.io.Reader;
import java.util.function.Consumer;
import static com.mammb.code.jsonstruct.parser.Token.Type.*;

/**
 * Parser.
 *
 * @author Naotsugu Kobayashi
 */
public class Parser {

    /** Tokenizer. */
    private final Tokenizer tokenizer;

    /** current token. */
    private Token curr;

    /** previous token. */
    private Token prev;


    /**
     * Constructor.
     * @param tokenizer the Tokenizer
     */
    private Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }


    /**
     * Create a new Parser.
     * @param reader the Reader
     * @return a new Parser
     */
    public static Parser of(Reader reader) {
        return new Parser(Tokenizer.of(reader));
    }


    /**
     * Create a new Parser.
     * @param reader the Reader
     * @param ca the CharArray
     * @return a new Parser
     */
    public static Parser of(Reader reader, CharArray ca) {
        return new Parser(Tokenizer.of(reader, ca));
    }


    /**
     * Create a new Parser.
     * @param cs the CharSequence
     * @return a new Parser
     */
    public static Parser of(CharSequence cs) {
        return new Parser(Tokenizer.of(new StringReader(cs.toString())));
    }


    /**
     * Parses JSON and generates JsonStructure.
     * @return JsonStructure
     */
    public JsonStructure parse() {
        Token token = tokenizer.next();
        return switch (token.type) {
            case CURLY_OPEN  -> parseObject(JsonObject.of());
            case SQUARE_OPEN -> parseArray(JsonArray.of());
            case EOF -> null;
            default  -> throw new JsonParseException();
        };
    }


    /**
     * Parse object.
     * @param obj the JsonObject parsed in
     * @return JsonObject
     */
    JsonObject parseObject(JsonObject obj) {
        Token name = null;
        for (;;) {
            prev = curr;
            curr = tokenizer.next();
            if (prev == null || prev.type != COLON) {
                name = null;
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
                    if (name != null)
                        keying(name, k -> obj.put(k, JsonString.of((CharSource) curr)));
                }
                case COLON -> {
                    if (prev != null && prev.type == STRING) name = prev;
                    else throw new JsonParseException();
                }
                case COMMA -> {
                    if (name != null || prev == null ||
                        prev.type == CURLY_OPEN || prev.type == SQUARE_OPEN ||
                        prev.type == COLON || prev.type == COMMA)
                        throw new JsonParseException();
                }
                case CURLY_CLOSE -> {
                    return obj;
                }
                default -> throw new JsonParseException();
            }
        }
    }


    /**
     * Parse array
     * @param array the array parsed in
     * @return JsonArray
     */
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
                default -> throw new JsonParseException();
            }
        }
    }


    /**
     * Apply key
     * @param name the name token
     * @param consumer Key consuming actions
     */
    private void keying(Token name, Consumer<String> consumer) {
        if (name == null) {
            throw new JsonParseException();
        }
        consumer.accept(name.toString());
    }

}
