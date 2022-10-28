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
package com.mammb.code.jsonstruct.parser;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Growable char array.
 * @author Naotsugu Kobayashi
 */
public class CharArray implements Serializable {

    /** empty. */
    private static final char[] EMPTY = {};

    /** The char array elements. */
    private char[] elements;

    /** The length of char array. */
    private int length;


    /**
     * Constructor.
     * @param elements the char array elements
     * @param length the length of char array
     */
    private CharArray(char[] elements, int length) {
        this.elements = elements;
        this.length = length;
    }


    /**
     * Create an empty char array.
     * @return CharArray
     */
    public static CharArray of() {
        return new CharArray(EMPTY, 0);
    }


    /**
     * Create a char array from given value.
     * @param value char
     * @return CharArray
     */
    public static CharArray of(char value) {
        return new CharArray(new char[] { value }, 1);
    }


    /**
     * Create a char array from given values.
     * @param values the initial chars
     * @return CharArray
     */
    public static CharArray of(char[] values) {
        return new CharArray(Arrays.copyOf(values, values.length), values.length);
    }


    /**
     * Add char values.
     * @param values the char values
     */
    public void add(char[] values) {
        if (length + values.length > elements.length) {
            elements = grow(values.length);
        }
        System.arraycopy(values, 0, elements, length, values.length);
        length += values.length;
    }


    /**
     * Add char value.
     * @param value the char value
     */
    public void add(char value) {
        if (length == elements.length) {
            elements = grow(length + 1);
        }
        elements[length++] = value;
    }


    /**
     * Gets the element at the specified position in this array.
     * @param index index of the element to return
     * @return the element at the specified position in this array
     */
    public int get(char index) {
        return elements[index];
    }


    /**
     * Gets an array containing all the elements in this array in proper sequence.
     * @return an array containing all of the elements in this array in proper sequence
     */
    public char[] array() {
        return Arrays.copyOf(elements, length);
    }


    /**
     * Gets CharSource that is a subArray of this array.
     * @param start the beginning index, inclusive.
     * @param end the ending index, exclusive.
     * @return the subArray
     */
    public CharSource subArray(int start, int end) {
        return new SubArray(this, start, end);
    }


    /**
     * Gets CharSource that is a subArray of this array.
     * @param start the beginning index, inclusive
     * @return the subArray
     */
    public CharSource subArray(int start) {
        return new SubArray(this, start, length);
    }


    /**
     * Clear this array elements.
     */
    public void clear() {
        elements = EMPTY;
        length = 0;
    }


    /**
     * Gets the length of this array.
     * @return the length of this array
     */
    public int length() {
        return length;
    }


    /**
     * Gets the capacity of this buffer.
     * @return the capacity of this buffer
     */
    public int capacity() {
        return elements.length;
    }


    /**
     * Grow this buffer.
     * @param minCapacity min capacity
     * @return a grown buffer
     */
    private char[] grow(int minCapacity) {
        int oldCapacity = elements.length;
        if (length == 0 || elements == EMPTY) {
            return elements = new char[Math.max(10, minCapacity)];
        } else {
            return elements = Arrays.copyOf(elements, newCapacity(oldCapacity,
                    minCapacity - oldCapacity,
                    Math.min(512, oldCapacity >> 1)));
        }
    }


    /**
     * Gets the next new capacity.
     * @param oldLength old length
     * @param minGrowth min growth length
     * @param prefGrowth preferred growth length
     * @return the next new capacity
     */
    private static int newCapacity(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if (0 < prefLength && prefLength <= Integer.MAX_VALUE - 8) {
            return prefLength;
        } else {
            int minLength = oldLength + minGrowth;
            if (minLength < 0) {
                throw new OutOfMemoryError(
                        "Required array length %d %d is too large".formatted(oldLength, minGrowth));
            }
            return Math.max(minLength, Integer.MAX_VALUE - 8);
        }
    }


    /**
     * The SubArray record.
     * @param source the CharArray
     * @param start the beginning index, inclusive.
     * @param end the ending index, exclusive.
     */
    private record SubArray(CharArray source, int start, int end) implements CharSource, Serializable {

        @Override
        public char[] chars() {
            return Arrays.copyOfRange(source.elements, start, end);
        }

        @Override
        public String toString() {
            return new String(chars());
        }

    }

}
