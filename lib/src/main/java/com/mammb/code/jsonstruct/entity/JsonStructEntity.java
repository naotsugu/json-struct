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
package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.Context;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

/**
 * JsonStructEntity.
 * @author Naotsugu Kobayashi
 */
public class JsonStructEntity {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "com.mammb.code.jsonstruct.JsonStruct";

    /** Context of processing. */
    private final Context context;

    /** The target type element. */
    private final TypeElement element;

    private final ObjectEntity entity;

    /**
     * Constructor.
     */
    private JsonStructEntity(Context context,
            TypeElement element, ExecutableElement execElement) {
        this.context = context;
        this.element = element;
        this.entity = new ObjectEntity(context, execElement);
    }

    public static Optional<JsonStructEntity> of(Context context, Element element) {

        if (!isInTarget(element)) {
            return Optional.empty();
        }

        if (element.getKind().isClass()) {
            return Optional.of(new JsonStructEntity(
                context,
                (TypeElement) element,
                Utils.getConstructor(element).orElseThrow()));
        }

        if (element.getKind() == ElementKind.CONSTRUCTOR &&
            element.getModifiers().contains(Modifier.PUBLIC)) {
            return Optional.of(new JsonStructEntity(
                context,
                (TypeElement) element.getEnclosingElement(),
                (ExecutableElement) element));
        }

        if (element.getKind() == ElementKind.METHOD &&
            element.getModifiers().contains(Modifier.PUBLIC)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;
            if (typeElement.getQualifiedName().toString().equals(executableElement.getReturnType().toString())) {
                return Optional.of(new JsonStructEntity(
                    context,
                    typeElement,
                    executableElement));
            }
        }
        return Optional.empty();
    }

    public String code() {
        return """
            @Override
            public %s from(Reader reader) {
                var json = Parser.of(reader).parse();
                return %s;
            }
            """.formatted(getSimpleName(), entity.code());
    }

    public String getSimpleName() {
        return element.getSimpleName().toString();
    }


    /**
     * Get qualified name of the entity class.
     * e.g. {@code foo.bar.Person}
     * @return qualified name of the static metamodel class
     */
    public String getQualifiedName() {
        return element.getQualifiedName().toString();
    }


    public String getPackageName() {
        return context.getElementUtils().getPackageOf(element).getQualifiedName().toString();
    }


    private static boolean isInTarget(Element element) {
        return element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Object::toString)
            .anyMatch(ANNOTATION_TYPE::equals);
    }

}
