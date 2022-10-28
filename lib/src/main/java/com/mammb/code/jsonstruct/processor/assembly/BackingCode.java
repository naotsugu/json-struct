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
package com.mammb.code.jsonstruct.processor.assembly;

/**
 * BackingCode.
 * @author Naotsugu Kobayashi
 */
public class BackingCode {

    /** the code. */
    private final Code code;

    /** the backing code. */
    private final Code backingMethods;


    /**
     * Constructor.
     * @param code the code
     * @param backingMethods the backingMethods
     */
    private BackingCode(Code code, Code backingMethods) {
        this.code = code;
        this.backingMethods = backingMethods;
    }


    /**
     * Create a new empty BackingCode.
     * @return BackingCode
     */
    public static BackingCode of() {
        return new BackingCode(Code.of(), Code.of());
    }


    /**
     * Create a new BackingCode with given arguments.
     * @param code the code
     * @param backingMethods the backing code
     * @return BackingCode
     */
    public static BackingCode of(Code code, Code backingMethods) {
        return new BackingCode(code, backingMethods);
    }


    /**
     * Add the given BackingCode.
     * @param other to be added BackingCode
     * @return this BackingCode
     */
    public BackingCode add(BackingCode other) {
        code.add(other.code);
        backingMethods.add(other.backingMethods);
        return this;
    }


    /**
     * Gets a code.
     * @return code
     */
    public Code code() {
        return code;
    }


    /**
     * Gets the backingMethods.
     * @return the backingMethods
     */
    public Code backingMethods() {
        return backingMethods;
    }

}
