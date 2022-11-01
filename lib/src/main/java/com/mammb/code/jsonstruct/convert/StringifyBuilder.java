package com.mammb.code.jsonstruct.convert;

import java.io.IOException;

public class StringifyBuilder {

    private final Appendable appendable;

    public StringifyBuilder(Appendable appendable) {
        this.appendable = appendable;
    }



    StringifyBuilder append(Object object) {
        appendOn(String.valueOf(object));
        return this;
    }

    StringifyBuilder append(CharSequence cs) {
        appendOn(cs);
        return this;
    }


    StringifyBuilder appendStr(Object object) {
        if (object == null) {
            appendOn("null");
        } else {
            appendOn("\"").appendEscOn(String.valueOf(object)).appendOn("\"");
        }
        return this;
    }


    StringifyBuilder appendStr(CharSequence cs) {
        if (cs == null) {
            appendOn("null");
        } else {
            appendOn("\"").appendEscOn(cs).appendOn("\"");
        }
        return this;
    }


    private StringifyBuilder appendOn(CharSequence cs) {
        try {
            appendable.append(cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    private StringifyBuilder appendOn(char ch) {
        try {
            appendable.append(ch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private StringifyBuilder appendEscOn(CharSequence cs) {
        int len = cs.length();
        for (int i = 0; i < len; i++) {
            int begin = i, end = i;
            char c = cs.charAt(i);
            while (c >= ' ' && c != '"' && c != '\\') {
                // RFC 4627  unescaped = %x20-21 / %x23-5B / %x5D-10FFFF
                i++; end = i;
                if (i >= len) break;
                c = cs.charAt(i);
            }

            if (begin < end) {
                appendOn(cs.subSequence(begin, end));
                if (i == len)  break;
            }

            switch (c) {
                case '"', '\\' -> appendOn('\\').appendOn(c);
                case '\b' -> appendOn('\\').appendOn('b');
                case '\f' -> appendOn('\\').appendOn('f');
                case '\n' -> appendOn('\\').appendOn('n');
                case '\r' -> appendOn('\\').appendOn('r');
                case '\t' -> appendOn('\\').appendOn('t');
                default -> {
                    String hex = "000" + Integer.toHexString(c);
                    appendOn("\\u").appendOn(hex.substring(hex.length() - 4));
                }
            }
        }
        return this;
    }


}
