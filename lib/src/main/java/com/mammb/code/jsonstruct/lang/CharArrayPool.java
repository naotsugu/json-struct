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

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The CharArray Pool.
 * @author Naotsugu Kobayashi
 */
public class CharArrayPool {

    /** The queue reference. */
    private volatile WeakReference<ConcurrentLinkedQueue<CharArray>> queueRef;


    /**
     * Create a new CharArrayPool.
     * @return a new CharArrayPool
     */
    public static CharArrayPool of() {
        return new CharArrayPool();
    }


    /**
     * Gets a new CharArray from the pool.
     * @return a new CharArray
     */
    public final CharArray take() {
        CharArray ret = getQueue().poll();
        return (ret == null) ? CharArray.of(1024) : ret;
    }


    /**
     * Recycle a given charArray.
     * back to the pool.
     * @param ca a CharArray
     */
    public final void recycle(CharArray ca) {
        getQueue().offer(ca.reset());
    }


    /**
     * Get a queue.
     * @return a queue
     */
    private ConcurrentLinkedQueue<CharArray> getQueue() {

        WeakReference<ConcurrentLinkedQueue<CharArray>> ref = queueRef;
        if (ref != null) {
            ConcurrentLinkedQueue<CharArray> queue = ref.get();
            if (queue != null) {
                return queue;
            }
        }

        // overwrite the queue
        ConcurrentLinkedQueue<CharArray> queue = new ConcurrentLinkedQueue<>();
        queueRef = new WeakReference<>(queue);
        return queue;
    }

}
