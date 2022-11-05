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

import java.io.Closeable;
import java.util.function.Predicate;

/**
 * CharReader.
 * @author Naotsugu Kobayashi
 */
public interface CharReader extends Closeable {

    /**
     * Reads a single character.
     * @return The character read, or -1 if the end of the stream has been reached
     */
    int read();


    /**
     * Reads a next character with skipping white space.
     * @return The character read, or -1 if the end of the stream has been reached
     */
    int readNextChar();


    /**
     * Get the length of time until the condition given as a predicate is satisfied.
     * The cursor does not move forward.
     * @param until a predicate
     * @return the length
     */
    int length(Predicate<Character> until);


    /**
     * Reads characters into a portion of an array.
     * @param chars destination buffer
     * @param off offset at which to start storing characters
     * @param len maximum number of characters to read
     * @return The number of characters read, or -1 if the end of the stream has been reached
     */
    int read(char[] chars, int off, int len);


    /**
     * Skips characters.
     * @param n the number of characters to skip
     */
    void skip(int n);


    /**
     * Step back a single read.
     * This is one shot operation.
     */
    void stepBack();

}
