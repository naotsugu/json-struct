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
package com.mammb.code.jsonstruct;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Json.
 * @author Naotsugu Kobayashi
 */
public interface Json<T> {

    /**
     * Construct the given class instance from json.
     * @param reader Reader
     * @return the class instance
     */
    T from(Reader reader);


    /**
     * Construct the given class instance from json.
     * @param cs the char sequence of json
     * @return the class instance
     */
    default T from(CharSequence cs) {
        return from(new StringReader(cs.toString()));
    }


    /**
     * Writes the object content tree to a {@link Appendable}.
     * @param object the object content tree to be serialized.
     * @param writer destination of json data where serialized from java content tree
     * @throws IOException if io error occurred
     */
    void stringify(T object, Appendable writer) throws IOException;


    /**
     * Serializes the object content tree to a {@link CharSequence}.
     * @param object the object content tree to be serialized.
     * @return the {@link CharSequence} serialized from java content tree.
     */
    default String stringify(T object) {
        try {
            StringBuilder sb = new StringBuilder(256);
            stringify(object, sb);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Create a Json instance for a specified type.
     * @param clazz the specified type
     * @param <T> type of class
     * @return a Json instance
     */
    static <T> Json<T> of(Class<T> clazz) {
        return Json_.of(clazz);
    }

}
