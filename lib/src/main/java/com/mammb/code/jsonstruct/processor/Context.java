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

import com.mammb.code.jsonstruct.model.JsonStructEntity;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * Context of annotation processing.
 * @author Naotsugu Kobayashi
 */
public class Context implements ProcessingEnvironment {

    /** Debug option key. */
    public static final String DEBUG_OPTION_KEY = "debug";

    /** Annotation processing environment. */
    private final ProcessingEnvironment pe;

    /** Mode of debug. */
    private final boolean debug;

    /** Processed class holder. */
    private final Collection<Object> processed;


    /**
     * Private constructor.
     * @param pe the annotation processing environment
     */
    public Context(ProcessingEnvironment pe) {
        this.pe = pe;
        this.processed = new ArrayList<>();
        this.debug = Boolean.parseBoolean(pe.getOptions()
            .getOrDefault(DEBUG_OPTION_KEY, "false"));
    }


    @Override
    public Map<String, String> getOptions() {
        return pe.getOptions();
    }


    @Override
    public Messager getMessager() {
        return pe.getMessager();
    }


    @Override
    public Filer getFiler() {
        return pe.getFiler();
    }


    @Override
    public Elements getElementUtils() {
        return pe.getElementUtils();
    }


    @Override
    public Types getTypeUtils() {
        return pe.getTypeUtils();
    }


    @Override
    public SourceVersion getSourceVersion() {
        return pe.getSourceVersion();
    }


    @Override
    public Locale getLocale() {
        return pe.getLocale();
    }


    /**
     * Add the given entity as processed.
     * @param object the {@link JsonStructEntity}
     */
    public void addProcessed(Object object) {
        processed.add(object);
    }


    /**
     * Gets the processed entities.
     * @return the processed entities
     */
    public List<Object> getProcessed() {
        return List.copyOf(processed);
    }


    /**
     * Gets the processed entities.
     * @return the processed entities
     */
    public <T> List<T> getProcessed(Class<T> type) {
        return processed.stream()
            .filter(type::isInstance)
            .map(type::cast).toList();
    }


    /**
     * Write the debug log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logDebug(String message, Object... args) {
        if (!debug) return;
        pe.getMessager().printMessage(Diagnostic.Kind.OTHER, formatted(message, args));
    }


    /**
     * Write the info log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logInfo(String message, Object... args) {
        pe.getMessager().printMessage(Diagnostic.Kind.NOTE, formatted(message, args));
    }


    /**
     * Write the error log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logError(String message, Object... args) {
        pe.getMessager().printMessage(Diagnostic.Kind.ERROR, formatted(message, args));
    }


    /**
     * Format the given format string with args.
     * @param format the format string
     * @param args the arguments referenced by the format specifiers in this string.
     * @return the formatted string
     */
    private String formatted(String format, Object... args) {
        return Arrays.stream(args)
            .map(arg -> Objects.nonNull(arg) ? arg.toString() : "")
            .reduce(format, (str, arg) -> str.replaceFirst("\\{}", arg));
    }

    public boolean isBasic(TypeMirror type) {
        // TODO
        return Objects.equals("java.lang.String", type.toString()) ||
            Objects.equals("int", type.toString());
    }

}
