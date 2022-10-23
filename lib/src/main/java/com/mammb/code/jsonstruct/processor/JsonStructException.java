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
package com.mammb.code.jsonstruct.processor;

import java.util.Arrays;
import java.util.Objects;

/**
 * JsonStructException.
 * @author Naotsugu Kobayashi
 */
public class JsonStructException extends RuntimeException {

    /**
     * Constructs a new JsonStructException with {@code null} as its detail message.
     */
    public JsonStructException() {
        super();
    }


    /**
     * Constructs a new JsonStructException with the specified detail message.
     * @param message the detail message.
     */
    public JsonStructException(String message) {
        super(message);
    }


    /**
     * Constructs a new JsonStructException with the specified detail message.
     * @param format the detail message format.
     * @param args  the detail message format arguments.
     */
    public JsonStructException(String format, Object... args) {
        super(formatted(format, args));
    }


    /**
     * Constructs a new JsonStructException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public JsonStructException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Constructs a new JsonStructException with the specified cause.
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public JsonStructException(Throwable cause) {
        super(cause);
    }


    /**
     * Format the given format string with args.
     * @param format the format string
     * @param args the arguments referenced by the format specifiers in this string.
     * @return the formatted string
     */
    private static String formatted(String format, Object... args) {
        return Arrays.stream(args)
            .map(arg -> Objects.nonNull(arg) ? arg.toString().replace("$", "\\$") : "")
            .reduce(format, (str, arg) -> str.replaceFirst("\\{}", arg));
    }

}
