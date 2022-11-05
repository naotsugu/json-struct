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

import java.util.function.Predicate;

/**
 * StringReader.
 * @author Naotsugu Kobayashi
 */
public class StringReader implements CharReader {

    /** the source string. */
    private String string;

    /** the source length. */
    private int length;

    /** index of next reading. */
    private int next;


    /**
     * Constructor.
     * @param string the source string
     */
    public StringReader(String string) {
        this.string = string;
        this.length = string.length();
        this.next = 0;
    }


    /**
     * Create a new StringReader for given string.
     * @param string the source string
     * @return a new StringReader
     */
    public static StringReader of(String string) {
        return new StringReader(string);
    }


    @Override
    public int read() {
        return (next >= length) ? -1 : string.charAt(next++);
    }


    @Override
    public int readNextChar() {
        int ch;
        do {
            ch = (next >= length) ? -1 : string.charAt(next++);
        } while (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
        return ch;
    }


    @Override
    public int length(Predicate<Character> until) {
        for (int i = next; i < length; i++) {
            if (until.test(string.charAt(i))) {
                continue;
            }
            return i - next;
        }
        return -1;
    }


    @Override
    public int read(char[] chars, int off, int len) {

        if (chars.length < off + len) throw new IndexOutOfBoundsException(off + len);
        if (len == 0) return 0;
        if (next >= length) return -1;

        int n = Math.min(length - next, len);
        string.getChars(next, next + n, chars, off);
        next += n;

        return n;
    }


    @Override
    public void skip(int n) {
        next += Math.min(length - next, n);
    }


    @Override
    public void stepBack() {
        next--;
    }


    @Override
    public void close() {
        string = null;
    }

}
