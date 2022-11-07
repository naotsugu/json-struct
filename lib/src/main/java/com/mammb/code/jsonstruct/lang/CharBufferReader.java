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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Predicate;

/**
 * CharBufferReader.
 * @author Naotsugu Kobayashi
 */
public class CharBufferReader implements CharReader {

    /** The reader within. */
    private Reader in;

    /** The position of reader. */
    private int position;

    /** The read buffer. */
    private char[] buf;

    /** The number of limit for read buffer. */
    private int limit;

    /** The index of next read. */
    private int next;

    /** The previous character. */
    private int latestRead;

    /** need stepBack?. */
    private boolean stepBack;


    /**
     * Constructor.
     * @param in The reader within
     */
    private CharBufferReader(Reader in) {
        this.in = (in.markSupported()) ? in : new BufferedReader(in);
        this.position = 0;
        this.buf = new char[64]; // small cache
        this.limit = 0;
        this.next = 0;
        this.latestRead = -1;
    }


    /**
     * Create a new CharBufferReader.
     * @param in The reader within
     * @return a new CharBufferReader
     */
    public static CharBufferReader of(Reader in) {
        return new CharBufferReader(in);
    }


    @Override
    public int read() {
        position++;
        if (stepBack) {
            stepBack = false;
            return latestRead;
        }
        if (next >= limit) fillBuffer();
        if (next == limit) return -1;
        latestRead = buf[next++];
        return latestRead;
    }


    @Override
    public int readNextChar() {
        int ch;
        do {
            ch = read();
        } while (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n');
        return ch;
    }


    @Override
    public int length(Predicate<Character> until) {
        try {
            int length = 0;
            if (stepBack && !until.test((char) latestRead)) {
                return length;
            }
            for (int i = next  + (stepBack ? 1 : 0); i < limit; i++, length++) {
                int ch = buf[i];
                if (!until.test((char) ch)) {
                    return length;
                }
            }
            in.mark(Integer.MAX_VALUE);
            for (;; length++) {
                int ch = in.read();
                if (ch == -1 || !until.test((char) ch)) {
                    in.reset();
                    return length;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int read(char[] chars, int off, int len) {
        try {
            position += len;
            if (stepBack) {
                chars[off] = (char) latestRead;
                stepBack = false;
                return in.read(chars, off + 1, len - 1) + 1;
            } else {
                return in.read(chars, off, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void skip(int n) {
        for (int i = 0; i < n; i++) read();
    }


    @Override
    public void stepBack() {
        position--;
        stepBack = true;
    }


    @Override
    public void close() {
        try {
            in.close();
            position = 0;
            buf = null;
            limit = 0;
            next = -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Fill up the buffer from the reader.
     */
    private void fillBuffer() {
        try {
            limit = next = 0;
            int ret;
            while ((ret = in.read(buf, limit, buf.length - limit)) != -1) {
                limit += ret;
                if (limit == buf.length) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getPosition() {
        return position;
    }

}
