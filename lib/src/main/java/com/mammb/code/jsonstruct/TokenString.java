package com.mammb.code.jsonstruct;

public class TokenString extends Token implements CharSource {

    private final CharSource source;
    private char[] chars;

    TokenString(CharSource source) {
        super(Type.STRING);
        this.source = source;
    }

    static TokenString of(CharSource source) {
        return new TokenString(source);
    }

    @Override
    public char[] chars() {
        char[] ret = chars;
        if (ret == null) {
            chars = ret = source.chars();
            return ret;
        }
        return ret;
    }

    @Override
    public String toString() {
        return new String(chars());
    }

}
