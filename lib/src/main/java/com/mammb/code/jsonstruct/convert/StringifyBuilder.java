package com.mammb.code.jsonstruct.convert;

import java.io.IOException;
import java.util.function.Consumer;

public class StringifyBuilder {

    private final Appendable appendable;

    private final Converts convert;


    public StringifyBuilder(Appendable appendable, Converts convert) {
        this.appendable = appendable;
        this.convert = convert;
    }

    public static StringifyBuilder of(Appendable appendable, Converts convert) {
        return new StringifyBuilder(appendable, convert);
    }


    public StringifyBuilder appendObj(Object object) {
        if (object instanceof CharSequence) {
            // bypass
            appendOn('"');
            appendEscOn((CharSequence) object);
            appendOn('"');
        } else {
            convert.stringify(object, this);
        }
        return this;
    }

    public StringifyBuilder appendFun(Consumer<StringifyBuilder> fun) {
        fun.accept(this);
        return this;
    }


    public StringifyBuilder append(CharSequence cs) {
        appendOn(cs);
        return this;
    }

    public StringifyBuilder append(char ch) {
        appendOn(ch);
        return this;
    }


    StringifyBuilder appendNull() {
        appendOn("null");
        return this;
    }

    StringifyBuilder appendNum(Object object) {
        appendOn(String.valueOf(object));
        return this;
    }

    StringifyBuilder appendNum(CharSequence cs) {
        appendOn(cs);
        return this;
    }


    StringifyBuilder appendStr(Object object) {
        if (object == null) {
            return appendNull();
        }
        appendOn('"');
        appendEscOn(String.valueOf(object));
        appendOn('"');
        return this;
    }


    StringifyBuilder appendStr(CharSequence cs) {
        if (cs == null) {
            return appendNull();
        }
        appendOn('"');
        appendEscOn(cs);
        appendOn('"');
        return this;
    }


    private void appendOn(CharSequence cs) {
        try {
            appendable.append(cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void appendOn(char ch) {
        try {
            appendable.append(ch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void appendEscOn(CharSequence cs) {
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
                case '"', '\\' -> { appendOn('\\'); appendOn(c); }
                case '\b' -> appendOn("\\b");
                case '\f' -> appendOn("\\f");
                case '\n' -> appendOn("\\n");
                case '\r' -> appendOn("\\r");
                case '\t' -> appendOn("\\t");
                default -> {
                    String hex = "000" + Integer.toHexString(c);
                    appendOn("\\u");
                    appendOn(hex.substring(hex.length() - 4));
                }
            }
        }
    }

}
