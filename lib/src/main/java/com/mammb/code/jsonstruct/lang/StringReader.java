package com.mammb.code.jsonstruct.lang;

import java.io.Reader;
import java.util.Objects;

public class StringReader extends Reader {

    private String str;
    private int length;
    private int next;

    public StringReader(String s) {
        this.str = s;
        this.length = s.length();
        this.next = 0;
    }

    public int read() {
        if (next >= length)
            return -1;
        return str.charAt(next++);
    }

    @Override
    public int read(char[] chars, int off, int len) {
        Objects.checkFromIndexSize(off, len, chars.length);
        if (len == 0) {
            return 0;
        }
        if (next >= length)
            return -1;
        int n = Math.min(length - next, len);
        str.getChars(next, next + n, chars, off);
        next += n;
        return n;
    }

    @Override
    public void close() {
        str = null;
        length = 0;
        next = 0;
    }

}
