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
package com.mammb.code.jsonstruct.processor.assemble;

/**
 * BackingCode.
 * @author Naotsugu Kobayashi
 */
public class BackingCode {

    private final Code code;

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


    public static BackingCode of() {
        return new BackingCode(Code.of(), Code.of());
    }


    public static BackingCode of(String literal) {
        return new BackingCode(Code.of(literal), Code.of());
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
     * Append the given literal at the end of last line.
     * @param literal to be appended literal
     * @return this BackingCode
     */
    public BackingCode append(String literal) {
        code.append(literal);
        return this;
    }


    /**
     * Interpolate the type value to this code by given key.
     * @param key the key name
     * @param type the type value to be interpolated
     * @return this BackingCode
     */
    public BackingCode interpolateType(String key, String type) {
        code.interpolateType(key, type);
        return this;
    }


    /**
     * Interpolate the code to this code by given key.
     * @param key the key name
     * @param content the content to be interpolated
     * @return this BackingCode
     */
    public BackingCode interpolate(String key, String content) {
        code.interpolate(key, content);
        return this;
    }


    /**
     * Interpolate the code to this code by given key.
     * @param key the key name
     * @param other the code to be interpolated
     * @return this BackingCode
     */
    public BackingCode interpolate(String key, Code other) {
        code.interpolate(key, other);
        return this;
    }


    /**
     * Add backing method.
     * @param method the backing method
     * @return this BackingCode
     */
    public BackingCode addBackingMethod(Code method) {
        backingMethods.add(method);
        return this;
    }


    public Code code() {
        return code;
    }


    public Code backingMethods() {
        return backingMethods;
    }

}
