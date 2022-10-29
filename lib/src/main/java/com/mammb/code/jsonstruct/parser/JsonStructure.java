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

import java.util.function.Function;

/**
 * JsonStructure.
 *
 * @author Naotsugu Kobayashi
 */
public interface JsonStructure extends JsonValue {

    /**
     * Gets the element identified by the specified pointer.
     * @param pointer the specified json pointer
     * @return the element identified by the specified pointer
     */
    default JsonValue at(String pointer) {
        return JsonPointer.of(pointer).asValue(this).orElse(null);
    }


    /**
     * Gets the element identified by the specified pointer with the specified conversion function.
     * @param pointer the specified json pointer
     * @param conv the conversion function
     * @param <T> the type to convert
     * @return the converted element identified by the specified pointer
     */
    default <T> T as(String pointer, Function<JsonValue, T> conv) {
        return conv.apply(at(pointer));
    }


    /**
     * Returns the number of elements in this structure.
     * @return the number of elements in this structure
     */
    int size();

}
