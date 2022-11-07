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

import com.mammb.code.jsonstruct.processor.assembly.Code;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.util.Optional;

/**
 * JsonStructConvertEntity.
 * @author Naotsugu Kobayashi
 */
public class JsonStructConvertEntity {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "com.mammb.code.jsonstruct.JsonStructConvert";

    /** JsonStructConvert element. */
    private final VariableElement element;

    /** Utility of java lang model. */
    private final LangUtil lang;


    /**
     * Constructor.
     * @param lang utility of java lang model
     * @param element executable element
     */
    public JsonStructConvertEntity(LangUtil lang, VariableElement element) {
        this.lang = lang;
        this.element = element;
    }


    /**
     * Create JsonStructConvertEntity.
     * @param ctx the context of processing
     * @param element the elements
     * @return optional of JsonStructConvertEntity
     */
    public static Optional<JsonStructConvertEntity> of(Context ctx, Element element) {

        if (!isInTarget(ctx, element)) {
            return Optional.empty();
        }
        LangUtil lang = LangUtil.of(ctx.getElementUtils(), ctx.getTypeUtils());
        return Optional.of(new JsonStructConvertEntity(lang, (VariableElement) element));

    }


    /**
     * Build code.
     * @return the code
     */
    public Code build() {
        return Code.of();
    }


    /**
     * Gets whether the element is subject to this Entity.
     * @param ctx the context of processing
     * @param element the element
     * @return {@code true} if whether the element is subject to this Entity
     */
    private static boolean isInTarget(Context ctx, Element element) {
        long count = element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Object::toString)
            .filter(ANNOTATION_TYPE::equals)
            .count();
        return count == 1;
    }

}
