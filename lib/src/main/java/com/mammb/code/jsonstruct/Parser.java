package com.mammb.code.jsonstruct;

import java.io.Reader;
import java.io.StringReader;

public class Parser {

    private final Tokenizer tokenizer;

    private Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public static Parser of(Reader reader) {
        return new Parser(Tokenizer.of(reader));
    }

    public static Parser of(CharSequence cs) {
        return new Parser(Tokenizer.of(new StringReader(cs.toString())));
    }


}
