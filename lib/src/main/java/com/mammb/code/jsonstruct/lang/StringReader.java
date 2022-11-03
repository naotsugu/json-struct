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
package com.mammb.code.jsonstruct.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * A character stream whose source is a string.
 *
 * @author Naotsugu Kobayashi
 */
public class StringReader extends Reader {

    /** the source string. */
    private String str;

    /** the source length. */
    private int length;

    /** index of next reading. */
    private int next;

    /** marked index. */
    private int mark;


    /**
     * Creates a new string reader.
     * @param s String providing the character stream.
     */
    public StringReader(String s) {
        this.str = s;
        this.length = s.length();
        this.next = 0;
        this.mark = 0;
    }


    /**
     * Reads a single character.
     * @return The character read, or -1 if the end of the stream has been reached
     */
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
    public boolean markSupported() {
        return true;
    }


    @Override
    public void mark(int readAheadLimit) {
        if (readAheadLimit < 0)
            throw new IllegalArgumentException("Read-ahead limit < 0");
        mark = next;
    }


    @Override
    public void reset() {
        next = mark;
    }


    @Override
    public long skip(long n) {
        if (next >= length)
            return 0;
        long r = Math.min(length - next, n);
        r = Math.max(-next, r);
        next += r;
        return r;
    }


    @Override
    public void close() {
        str = null;
        length = 0;
        next = 0;
        mark = 0;
    }

}
