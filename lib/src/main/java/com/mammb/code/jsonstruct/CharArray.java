package com.mammb.code.jsonstruct;

import java.io.Serializable;
import java.util.Arrays;

public class CharArray implements Serializable {

    private static final char[] EMPTY = {};

    private char[] elements;
    private int length;

    private CharArray(char[] elements, int length) {
        this.elements = elements;
        this.length = length;
    }

    public static CharArray of() {
        return new CharArray(EMPTY, 0);
    }

    public static CharArray of(char value) {
        return new CharArray(new char[] { value }, 1);
    }

    public static CharArray of(char[] values) {
        return new CharArray(Arrays.copyOf(values, values.length), values.length);
    }

    public void add(char[] values) {
        if (length + values.length > elements.length) {
            elements = grow(values.length);
        }
        System.arraycopy(values, 0, elements, length, values.length);
        length += values.length;
    }

    public void add(char value) {
        if (length == elements.length) {
            elements = grow(length + 1);
        }
        elements[length++] = value;
    }

    public int get(char index) {
        return elements[index];
    }

    public char[] get() {
        return Arrays.copyOf(elements, length);
    }

    public void clear() {
        elements = EMPTY;
        length = 0;
    }

    public int length() {
        return length;
    }

    public int capacity() {
        return elements.length;
    }

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

    private static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    public static int newCapacity(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            int minLength = oldLength + minGrowth;
            if (minLength < 0) {
                throw new OutOfMemoryError(
                        "Required array length %d %d is too large".formatted(oldLength, minGrowth));
            }
            return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
        }
    }

}
