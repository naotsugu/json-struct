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

import com.mammb.code.jsonstruct.lang.CharBufferReader;
import com.mammb.code.jsonstruct.lang.CharReader;
import com.mammb.code.jsonstruct.lang.StringReader;

/**
 * Json.
 * @author Naotsugu Kobayashi
 */
public interface Json<T> {

    /**
     * Construct the given class instance from json.
     * @param reader CharReader
     * @return the class instance
     */
    T from(CharReader reader);


    /**
     * Construct the given class instance from json.
     * @param reader Reader
     * @return the class instance
     */
    default T fromJson(Reader reader) {
        return from(CharBufferReader.of(reader));
    }


    /**
     * Construct the given class instance from json.
     * @param cs the char sequence of json
     * @return the class instance
     */
    default T fromJson(CharSequence cs) {
        return from(new StringReader(cs.toString()));
    }


    /**
     * Construct the given class instance from json.
     * @param cs the char sequence of json
     * @param clazz the type of model
     * @param <T> type of class
     * @return the class instance
     */
    static <T> T objectify(CharSequence cs, Class<T> clazz) {
        return of(clazz).fromJson(cs);
    }


    /**
     * Construct the given class instance from json.
     * @param reader Reader
     * @param clazz the type of model
     * @param <T> type of class
     * @return the class instance
     */
    static <T> T objectify(Reader reader, Class<T> clazz) {
        return of(clazz).fromJson(reader);
    }


    /**
     * Writes the object content tree to a {@link Appendable}.
     * @param object the object content tree to be serialized.
     * @param writer destination of json data where serialized from java content tree
     * @throws IOException if io error occurred
     */
    void toJson(T object, Appendable writer) throws IOException;


    /**
     * Serializes the object content tree to a Json string.
     * @param object the object content tree to be serialized.
     * @return the String serialized from java content tree.
     */
    default String toJson(T object) {
        try {
            StringBuilder sb = new StringBuilder(256);
            toJson(object, sb);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Serializes the object content tree to a Json string.
     * @param object the object content tree to be serialized.
     * @param writer destination of json data where serialized from java content tree
     * @param <T> type of class
     * @throws IOException if io error occurred
     */
    static <T> void stringify(T object, Appendable writer) throws IOException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        of(clazz).toJson(object, writer);
    }


    /**
     * Serializes the object content tree to a Json string.
     * @param object the object content tree to be serialized.
     * @param <T> type of class
     * @return the String serialized from java content tree.
     */
    static <T> String stringify(T object) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();
        return of(clazz).toJson(object);
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
