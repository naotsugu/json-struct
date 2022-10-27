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
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
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


    /**
     * Gets the package of an element
     * @param element the element being examined
     * @return the package of an element
     */
    public PackageElement getPackage(Element element) {
        return elementUtils.getPackageOf(element);
    }


    /**
     * Gets whether the given {@link Element} is a Class.
     * An enum is not considered a class.
     * @param element the {@link Element}
     * @return {@code true} if whether the given {@link Element} is a Class
     */
    public boolean isClass(Element element) {
        return element.getKind() == ElementKind.CLASS ||
            element.getKind() == ElementKind.RECORD;
    }


    /**
     * Gets whether the given {@link Element} is a Constructor.
     * Targets a public constructor.
     * @param element the {@link Element}
     * @return {@code true} if whether the given {@link Element} is a Constructor
     */
    public boolean isConstructor(Element element) {
        return element.getKind() == ElementKind.CONSTRUCTOR &&
            element.getModifiers().contains(Modifier.PUBLIC);
    }


    /**
     * Gets whether the given {@link Element} is a Static Factory Method.
     * @param element the {@link Element}
     * @return {@code true} if whether the given {@link Element} is a Static Factory Method
     */
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


    /**
     * Gets whether the given {@link Element} is a Constructor like(Constructor or Static factory method).
     * @param element the {@link Element}
     * @return {@code true} if whether the given {@link Element} is a Constructor like
     */
    public boolean isConstructorLike(Element element) {
        return isConstructor(element) || isStaticFactory(element);
    }


    /**
     * Gets whether the given {@link TypeMirror} is a Enum type.
     * @param typeMirror the {@link TypeMirror}
     * @return {@code true} if whether the given {@link TypeMirror} is a Enum type
     */
    public boolean isEnum(TypeMirror typeMirror) {
        return typeUtils.asElement(typeMirror).getKind() == ElementKind.ENUM;
    }


    /**
     * Gets whether the given {@link TypeMirror} is a List type.
     * @param typeMirror the {@link TypeMirror}
     * @return {@code true} if whether the given {@link TypeMirror} is a List type
     */
    public boolean isListLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.List").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    /**
     * Gets whether the given {@link TypeMirror} is a Set type.
     * @param typeMirror the {@link TypeMirror}
     * @return {@code true} if whether the given {@link TypeMirror} is a Set type
     */
    public boolean isSetLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.Set").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    /**
     * Gets whether the given {@link TypeMirror} is a Map type.
     * @param typeMirror the {@link TypeMirror}
     * @return {@code true} if whether the given {@link TypeMirror} is a Map type
     */
    public boolean isMapLike(TypeMirror typeMirror) {
        TypeMirror list = elementUtils.getTypeElement("java.util.Map").asType();
        TypeMirror erasure = typeUtils.erasure(typeMirror);
        return typeUtils.isAssignable(erasure, list);
    }


    /**
     * Gets whether the given {@link TypeMirror} is an Array type.
     * @param typeMirror the {@link TypeMirror}
     * @return {@code true} if whether the given {@link TypeMirror} is an Array type
     */
    public boolean isArrayLike(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.ARRAY;
    }


    /**
     * Select the Constructor like(Constructor or Static factory method) present in the given {@link Element}.
     * @param element the {@link Element}
     * @param priorMarker the annotation markers to be preferentially selected.
     * @return the Constructor like {@link ExecutableElement}
     */
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


    /**
     * Gets the element corresponding to a type.
     * the element corresponding to the given type
     * @param type the type to map to an element
     * @return the element corresponding to the given type
     */
    public Element asTypeElement(TypeMirror type) {
        return typeUtils.asElement(type);
    }


    /**
     * Gets the type argument of the given {@link Element} as an {@link Element}.
     * e.g. {@code List<String>} -> element of String type
     * @param element the {@link Element}
     * @return the type argument of the given {@link Element}
     */
    public Element entryElement(Element element) {
        DeclaredType declaredType = (DeclaredType) element.asType();
        var typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 1) {
            throw new JsonStructException("Type arguments size must be 1. [{}]", element);
        }
        return typeUtils.asElement(typeArguments.get(0));
    }

    public TypeMirror entryType(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) typeMirror;
            return arrayType.getComponentType();
        } else if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            var typeArguments = declaredType.getTypeArguments();
            if (typeArguments.size() == 1) {
                return typeArguments.get(0);
            }
        }
        throw new JsonStructException("Type arguments is not found. [{}]", typeMirror);
    }

    public TypeMirror[] mapEntryTypes(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            var typeArguments = declaredType.getTypeArguments();
            if (typeArguments.size() == 2) {
                return new TypeMirror[] { typeArguments.get(0), typeArguments.get(1) };
            }
        }
        throw new JsonStructException("Type arguments is not found. [{}]", typeMirror);
    }

    /**
     * Gets the type argument of the given {@link Element} as an {@link Element}.
     * e.g. {@code Map<String, Integer>} -> element of String and Integer type
     * @param element the {@link Element}
     * @return the type argument of the given {@link Element}
     */
    public Element[] mapEntryElement(Element element) {
        DeclaredType declaredType = (DeclaredType) element.asType();
        var typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 2) {
            throw new JsonStructException("Type arguments size must be 2. [{}]", element);
        }
        return new Element[] {
            typeUtils.asElement(typeArguments.get(0)),
            typeUtils.asElement(typeArguments.get(1))
        };
    }


    /**
     * Select the accessor method present in the given {@link Element}.
     * @param element the {@link Element}.
     * @return the accessor methods
     */
    public List<ExecutableElement> selectAccessors(Element element) {

        if (element.getKind() == ElementKind.RECORD) {
            return element.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.RECORD_COMPONENT)
                .map(RecordComponentElement.class::cast)
                .map(RecordComponentElement::getAccessor)
                .toList();
        }

        if (element.getKind() == ElementKind.CLASS) {
            return element.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(LangModels::isBeanAccessor)
                .toList();
        }

        return List.of();
    }


    /**
     * Gets whether the given {@link ExecutableElement} is a bean accessor.
     * @param execElement the {@link ExecutableElement}
     * @return {@code true} if whether the given {@link ExecutableElement} is a bean accessor
     */
    private static boolean isBeanAccessor(ExecutableElement execElement) {
        if (!execElement.getModifiers().contains(Modifier.PUBLIC) ||
            execElement.getReturnType().getKind() == TypeKind.VOID ||
            execElement.getParameters().size() > 0) {
            return false;
        }
        String name = execElement.getSimpleName().toString();
        if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
            return true;
        }
        if (name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2)) &&
            execElement.getReturnType().getKind() == TypeKind.BOOLEAN) {
            return true;
        }
        return false;
    }


    /**
     * Get the accessor property name for the given {@link ExecutableElement}.
     * @param accessor the {@link ExecutableElement}.
     * @return the accessor property name
     */
    public String getPropertyName(ExecutableElement accessor) {
        if (!isBeanAccessor(accessor)) {
            return accessor.getSimpleName().toString();
        }
        String name = accessor.getSimpleName().toString();
        if (name.startsWith("get")) {
            return decapitalize(name.substring(3));
        } else if (name.startsWith("is")) {
            return decapitalize(name.substring(2));
        } else {
            return name;
        }
    }


    /**
     * Utility method to take a string and convert it to normal Java variable name capitalization.
     * Thus, "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays as "URL".
     * @param name The string to be decapitalized.
     * @return The decapitalized version of the string.
     */
    private static String decapitalize(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
            Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
