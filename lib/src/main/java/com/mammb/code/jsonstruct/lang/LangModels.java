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
package com.mammb.code.jsonstruct.lang;

import com.mammb.code.jsonstruct.processor.JsonStructException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for java lang model.
 * @author Naotsugu Kobayashi
 */
public class LangModels {

    /** ElementUtils. */
    private final Elements elementUtils;

    /** TypeUtils. */
    private final Types typeUtils;


    /**
     * Constructor.
     */
    private LangModels(Elements elementUtils, Types typeUtils) {
        this.elementUtils = Objects.requireNonNull(elementUtils);
        this.typeUtils = Objects.requireNonNull(typeUtils);
    }


    /**
     * Create a new LangModels.
     * @param elements ElementUtils
     * @param types TypeUtils
     * @return the LangModels
     */
    public static LangModels of(Elements elements, Types types) {
        return new LangModels(elements, types);
    }


    public PackageElement getPackage(Element element) {
        return elementUtils.getPackageOf(element);
    }


    public boolean isClass(Element element) {
        return element.getKind() == ElementKind.CLASS ||
            element.getKind() == ElementKind.RECORD;
    }

    public boolean isConstructor(Element element) {
        return element.getKind() == ElementKind.CONSTRUCTOR &&
            element.getModifiers().contains(Modifier.PUBLIC);
    }


    public boolean isStaticFactory(Element element) {

        if (element.getKind() == ElementKind.METHOD &&
            element.getModifiers().contains(Modifier.PUBLIC) &&
            element.getModifiers().contains(Modifier.STATIC)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;

            return typeElement.getQualifiedName().toString()
                .equals(executableElement.getReturnType().toString());
        }

        return false;

    }


    public boolean isConstructorLike(Element element) {
        return isConstructor(element) || isStaticFactory(element);
    }


    public boolean isListLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.List").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    public boolean isSetLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.Set").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    public boolean isMapLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.Map").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    public boolean isArrayLike(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY;
    }


    public Optional<ExecutableElement> selectConstructorLike(
            Element element, Class<? extends Annotation> priorMarker) {

        List<ExecutableElement> candidate = element.getEnclosedElements().stream()
            .filter(this::isConstructorLike)
            .map(ExecutableElement.class::cast)
            .toList();

        if (Objects.nonNull(priorMarker)) {
            var ret = candidate.stream()
                .filter(e -> Objects.nonNull(e.getAnnotation(priorMarker)))
                .findFirst();
            if (ret.isPresent()) return ret;
        }

        var ret = candidate.stream()
            .filter(this::isConstructor)
            .filter(e -> e.getParameters().size() > 0)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
        if (ret.isPresent()) return ret;

        ret = candidate.stream()
            .filter(this::isStaticFactory)
            .filter(e -> e.getParameters().size() > 0)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
        if (ret.isPresent()) return ret;

        return candidate.stream().findFirst();

    }


    public Element asTypeElement(TypeMirror type) {
        return typeUtils.asElement(type);
    }


    public Element entryElement(Element element) {
        DeclaredType declaredType = (DeclaredType) element.asType();
        var typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 1) {
            throw new JsonStructException();
        }
        return typeUtils.asElement(typeArguments.get(0));
    }

    public Element[] mapEntryElement(Element element) {
        DeclaredType declaredType = (DeclaredType) element.asType();
        var typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 2) {
            throw new JsonStructException();
        }
        return new Element[] {
            typeUtils.asElement(typeArguments.get(0)),
            typeUtils.asElement(typeArguments.get(1))
        };
    }

}
