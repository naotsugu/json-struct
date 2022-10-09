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

import java.io.Reader;

/**
 * Json.
 * @author Naotsugu Kobayashi
 */
public interface Json {

    /**
     * Construct the given class instance from json.
     * @param clazz the class
     * @param reader Reader
     * @param <T> the type of class
     * @return the class instance
     */
    <T> T as(Class<T> clazz, Reader reader);

    /**
     * Construct the given class instance from json.
     * @param clazz the class
     * @param cs the char sequence of json
     * @param <T> the type of class
     * @return the class instance
     */
    <T> T as(Class<T> clazz, CharSequence cs);

    static Json of() {
        return new Json_();
    }

}
