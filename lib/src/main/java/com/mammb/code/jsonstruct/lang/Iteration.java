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

/**
 * Iteration.
 * @author Naotsugu Kobayashi
 */
public interface Iteration {

    /**
     * Get a current index.
     * @return current index
     */
    int index();


    /**
     * Gets whether the following elements are present.
     * @return {@code true} if whether the following elements are present
     */
    boolean hasNext();


    /**
     * Gets whether the current is the first element.
     * @return {@code true} if whether the current is the first element
     */
    default boolean isFirst() {
        return index() == 0;
    }


    /**
     * Gets whether the current is the last element.
     * @return {@code true} if whether the current is the last element
     */
    default boolean isLast() {
        return !hasNext();
    }


    /**
     * Gets whether the current index is odd or not.
     * @return {@code true} if whether the current index is odd
     */
    default boolean isOdd() {
        return !isEven();
    }


    /**
     * Gets whether the current index is even or not.
     * @return {@code true} if whether the current index is even
     */
    default boolean isEven() {
        return index() % 2 == 0;
    }


    /** The empty iteration. */
    Iteration EMPTY = new Empty();


    /**
     * The empty iteration class.
     */
    class Empty implements Iteration {
        @Override
        public int index() { return -1; }
        @Override
        public boolean hasNext() { return false; }
    }

}
