package com.mammb.code.jsonstruct;

import java.math.BigDecimal;

public class TokenNumber extends Token implements CharSource {

    private final CharSource source;
    private final boolean frac;
    private final boolean exp;
    private char[] chars;


    TokenNumber(CharSource source, boolean frac, boolean exp) {
        super(Type.NUMBER);
        this.source = source;
        this.frac = frac;
        this.exp = exp;
    }

    static TokenNumber of(CharSource source) {
        return new TokenNumber(source, false, false);
    }

    static TokenNumber of(CharSource source, boolean frac, boolean exp) {
        return new TokenNumber(source, frac, exp);
    }

    int asInt() {
        char[] ca = chars();
        boolean minus = ca.length > 0 && ca[0] == '-';

        if (!(frac || exp) && (ca.length <= 9 || (minus && ca.length <= 10))) {
            int num = 0;
            for (int i = minus ? 1 : 0; i < ca.length; i++) {
                num = num * 10 + (ca[i] - '0');
            }
            return minus ? -num : num;
        } else {
            return new BigDecimal(ca).intValue();
        }
    }

    long asLong() {
        char[] ca = chars();
        boolean minus = ca.length > 0 && ca[0] == '-';

        if (!(frac || exp) && (ca.length <= 18 || (minus && ca.length <= 19))) {
            long num = 0;
            for(int i = minus ? 1 : 0; i < ca.length; i++) {
                num = num * 10 + (ca[i] - '0');
            }
            return minus ? -num : num;
        } else {
            return new BigDecimal(ca).longValue();
        }
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
