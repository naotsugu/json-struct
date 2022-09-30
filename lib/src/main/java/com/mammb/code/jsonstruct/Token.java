package com.mammb.code.jsonstruct;

public class Token {

    public final Type type;

    Token(Type type) {
        this.type = type;
    }

    public static Token of(Type type) {
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
            default -> throw new IllegalArgumentException();
        };
    }

    public Type type() {
        return type;
    }

    private static final Token TRUE  = new Token(Type.TRUE);
    private static final Token FALSE = new Token(Type.FALSE);
    private static final Token NULL  = new Token(Type.NULL);
    private static final Token EOF   = new Token(Type.EOF);
    private static final Token COLON = new Token(Type.COLON);
    private static final Token COMMA = new Token(Type.COMMA);
    private static final Token CURLYOPEN   = new Token(Type.CURLYOPEN);
    private static final Token CURLYCLOSE  = new Token(Type.CURLYCLOSE);
    private static final Token SQUAREOPEN  = new Token(Type.SQUAREOPEN);
    private static final Token SQUARECLOSE = new Token(Type.SQUARECLOSE);

    public enum Type {
        CURLYOPEN, SQUAREOPEN, COLON, COMMA,
        STRING, NUMBER, TRUE, FALSE, NULL,
        CURLYCLOSE, SQUARECLOSE,
        EOF;
    }



}
