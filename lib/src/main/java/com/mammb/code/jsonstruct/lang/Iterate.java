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

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Iterate.
 * @author Naotsugu Kobayashi
 */
public class Iterate<E> implements Iterable<Iterate.Entry<E>> {

    /** Iterate subject. */
    private final Supplier<Iterator<E>> iteratorSupplier;


    /**
     * Constructor.
     * @param iteratorSupplier the iterator supplier
     */
    private Iterate(Supplier<Iterator<E>> iteratorSupplier) {
        this.iteratorSupplier = iteratorSupplier;
    }


    /**
     * Create a iterate.
     * @param iterable to iterate subject
     * @param <E> the type of iterable
     * @return Iterate
     */
    public static <E> Iterate<E> of(Iterable<E> iterable) {
        return new Iterate<>(iterable::iterator);
    }


    /**
     * Create a iterate.
     * @param array to iterate subject
     * @param <E> the type of iterable
     * @return Iterate
     */
    public static <E> Iterate<E> of(E[] array) {
        return new Iterate<>(() -> List.of(array).iterator());
    }


    /**
     * Create a iterate.
     * Stream is something that can only be used once.
     * @param stream to iterate stream
     * @param <E> the type of iterable
     * @return Iterate
     */
    public static <E> Iterate<E> of(Stream<E> stream) {
        return new Iterate<>(stream::iterator);
    }


    @Override
    public Iterator<Entry<E>> iterator() {

        return new Iterator<>() {

            private final Iterator<E> iterator = iteratorSupplier.get();

            private int index = 0;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Entry<E> next() {
                E value = iterator.next();
                return new Entry<>(value, index++, iterator.hasNext());
            }

        };
    }


    /**
     * Iterate entry.
     * @param value entry value
     * @param index index
     * @param hasNext whether the following elements are present
     * @param <E> the type of entry
     */
    public record Entry<E>(E value, int index, boolean hasNext) implements Iteration { }

}
