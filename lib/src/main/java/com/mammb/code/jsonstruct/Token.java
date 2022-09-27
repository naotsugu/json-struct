package com.mammb.code.jsonstruct;

public record Token<T>(TokenType type, T value) {

    public static Token<CharSequence> cs(CharSequence cs) {
        return new Token<>(TokenType.STRING, cs);
    }

    public static Token<char[]> chars(char[] chars) {
        return new Token<>(TokenType.STRING, chars);
    }

    public static Token<Number> number(Number number) {
        return new Token<>(TokenType.NUMBER, number);
    }

    public static Token<Void> type(TokenType type) {
        return new Token<>(type, null);
    }


}
