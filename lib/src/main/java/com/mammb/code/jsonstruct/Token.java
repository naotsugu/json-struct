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
        return switch (type) {
            case CURLYOPEN -> Token.CURLYOPEN;
            case SQUAREOPEN -> Token.SQUAREOPEN;
            case COLON -> Token.COLON;
            case COMMA -> Token.COMMA;
            case TRUE -> Token.TRUE;
            case FALSE -> Token.FALSE;
            case NULL -> Token.NULL;
            case CURLYCLOSE -> Token.CURLYCLOSE;
            case SQUARECLOSE -> Token.SQUARECLOSE;
            case EOF -> Token.EOF;
            default -> throw new RuntimeException();
        };
    }

    private final static Token<Void> TRUE  = new Token<>(TokenType.TRUE, null);
    private final static Token<Void> FALSE = new Token<>(TokenType.FALSE, null);
    private final static Token<Void> NULL  = new Token<>(TokenType.NULL, null);
    private final static Token<Void> EOF   = new Token<>(TokenType.EOF, null);
    private final static Token<Void> COLON = new Token<>(TokenType.COLON, null);
    private final static Token<Void> COMMA = new Token<>(TokenType.COMMA, null);
    private final static Token<Void> CURLYOPEN   = new Token<>(TokenType.CURLYOPEN, null);
    private final static Token<Void> CURLYCLOSE  = new Token<>(TokenType.CURLYCLOSE, null);
    private final static Token<Void> SQUAREOPEN  = new Token<>(TokenType.SQUAREOPEN, null);
    private final static Token<Void> SQUARECLOSE = new Token<>(TokenType.SQUARECLOSE, null);

}
