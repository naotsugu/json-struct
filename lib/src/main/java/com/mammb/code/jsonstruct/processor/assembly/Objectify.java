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

import com.mammb.code.jsonstruct.JsonStruct;
import com.mammb.code.jsonstruct.JsonStructIgnore;
import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.JsonStructException;
import com.mammb.code.jsonstruct.processor.LangUtil;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Objectify.
 * @author Naotsugu Kobayashi
 */
public class Objectify {

    /** The lang model utility. */
    private final LangUtil lang;

    /** The known basic classes. */
    private final Set<String> basicClasses;

    /** The backing codes. */
    private Code backingCodes;

    /** The max cyclic depth. */
    private final int cyclicDepth;

    /** The stack of handling type fqcn. */
    private final Deque<Name> stack;

    /** Already defined names. */
    private final Set<String> definedNames;


    /**
     * Constructor.
     * @param lang The lang model utility
     * @param basicClasses The known basic classes
     * @param backingCodes The backing methods
     * @param cyclicDepth The max cyclic depth
     */
    private Objectify(LangUtil lang, Set<String> basicClasses, Code backingCodes, int cyclicDepth) {
        this.lang = Objects.requireNonNull(lang);
        this.basicClasses = Objects.requireNonNull(basicClasses);
        this.backingCodes = Objects.requireNonNull(backingCodes);
        this.cyclicDepth = cyclicDepth;
        this.stack = new ArrayDeque<>();
        this.definedNames = new HashSet<>();
    }


    /**
     * Create a new Objectify instance.
     * @param lang The lang model utility
     * @param basicClasses The known basic classes
     * @param cyclicDepth The max cyclic depth
     * @return a new Objectify instance
     */
    public static Objectify of(LangUtil lang, Set<String> basicClasses, int cyclicDepth) {
        return new Objectify(lang, basicClasses, Code.of(), cyclicDepth);
    }


    /**
     * Build backingCode for given element.
     * @param element The type element
     * @return a backingCode
     */
    public BackingCode build(TypeElement element) {
        return BackingCode.of(
            withStack(element, e -> object(e, Path.of())),
            backingWithClear());
    }


    private Code toCode(TypeMirror type, Path path) {

        if (basicClasses.contains(type.toString())) {
            return basic(type, path);
        }
        if (lang.isEnum(type)) {
            return enumerate(type, path);
        }
        if (lang.isArrayLike(type)) {
            return array(type, path);
        }
        if (lang.isListLike(type)) {
            return list(type, path);
        }
        if (lang.isSetLike(type)) {
            return set(type, path);
        }
        if (lang.isMapLike(type)) {
            return map(type, path);
        }
        return withStack(lang.asTypeElement(type), element -> object(element, path));
    }


    private Code object(TypeElement element, Path path) {

        Code params = Code.of();

        ExecutableElement constructorLike = lang.selectConstructorLike(element, JsonStruct.class).orElseThrow();
        for (var param : Iterate.of(constructorLike.getParameters())) {
            Code paramCode = (lang.isAnnotated(param.value(), JsonStructIgnore.class))
                ? defaults(param.value().asType())
                : toCode(param.value().asType(), path.with(param.value().getSimpleName().toString()));
            paramCode.append(param.hasNext() ? "," : "");
            params.add(paramCode);
        }

        return Code.of("""
            !(json.at(#{pointerName}) instanceof JsonStructure) ? null
            : #{newMethod}(
                #{params}
            )
            """)
            .interpolate("#{pointerName}", createPointer(path))
            .interpolate("#{newMethod}", instantiation(constructorLike, lang))
            .interpolate("#{params}", params);
    }


    private Code defaults(TypeMirror type) {
        return Code.of("""
                convert.defaults(#{type}.class)""")
            .interpolateType("#{type}", lang.erasure(type).toString());
    }


    private Code basic(TypeMirror type, Path path) {

        if (path.isEmpty()) {
            return Code.of("""
                json.as(convert.to(#{type}.class))""")
                .interpolateType("#{type}", type.toString());
        }

        return Code.of("""
            json.as(#{pointerName}, convert.to(#{type}.class))""")
            .interpolate("#{pointerName}", createPointer(path))
            .interpolateType("#{type}", type.toString())
            .add(Imports.of("com.mammb.code.jsonstruct.parser.JsonStructure"));
    }


    private Code enumerate(TypeMirror type, Path path) {

        if (path.isEmpty()) {
            return Code.of("""
                Optional.<String>ofNullable(json.as(convert.to(String.class))).map(#{enumType}::valueOf).orElse(null)""")
                .interpolateType("#{enumType}", type.toString());
        }

        return Code.of("""
                Optional.<String>ofNullable(json.as(#{pointerName}, convert.to(String.class))).map(#{enumType}::valueOf).orElse(null)""")
            .interpolate("#{pointerName}", createPointer(path))
            .interpolateType("#{enumType}", type.toString());
    }


    private Code list(TypeMirror type, Path path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "ObjectifyList");

        backingCodes.addEmptyLine().add(Code.of("""
            private List<#{type}> #{methodName}(JsonValue array) {
                if (array == null || array instanceof JsonValue.JsonNull) return null;
                List<#{type}> list = new ArrayList<>();
                for (JsonValue json : (JsonArray) array) {
                    list.add(#{entry});
                }
                return list;
            }
            """)
            .interpolateType("#{type}", entryType.toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", toCode(entryType, Path.of())));

        return Code.of("""
            #{methodName}(json.at(#{pointerName}))""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{pointerName}", createPointer(path));
    }


    private Code set(TypeMirror type, Path path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "ObjectifySet");

        backingCodes.addEmptyLine().add(Code.of("""
            private Set<#{type}> #{methodName}(JsonValue array) {
                if (array == null || array instanceof JsonValue.JsonNull) return null;
                Set<#{type}> set = new LinkedHashSet<>();
                for (JsonValue json : (JsonArray) array) {
                    set.add(#{entry});
                }
                return set;
            }
            """)
            .interpolateType("#{type}", entryType.toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", toCode(entryType, Path.of())));

        return Code.of("""
            #{methodName}(json.at(#{pointerName}))""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{pointerName}", createPointer(path));
    }


    private Code array(TypeMirror type, Path path) {

        TypeMirror compType = lang.entryType(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "ObjectifyArray");

        backingCodes.addEmptyLine().add(Code.of("""
            private #{type}[] #{methodName}(JsonValue array) {
                if (array == null || array instanceof JsonValue.JsonNull) return null;
                List<#{type}> list = new ArrayList<>();
                for (JsonValue json : (JsonArray) array) {
                    list.add(#{entry});
                }
                return list.toArray(new #{typeNew}[0]);
            }
            """)
            .interpolateType("#{type}", compType.toString())
            .interpolateType("#{typeNew}", compType.toString().replace("[]", "[0]"))
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", toCode(compType, Path.of())));

        return Code.of("""
            #{methodName}(json.at(#{pointerName}))""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{pointerName}", createPointer(path));
    }


    private Code map(TypeMirror type, Path path) {

        TypeMirror[] entryTypes = lang.biEntryTypes(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "ObjectifyMap");

        backingCodes.addEmptyLine().add(Code.of("""
            private Map<#{keyType}, #{valType}> #{methodName}(JsonValue str) {
                if (str == null || str instanceof JsonValue.JsonNull) return null;
                Map<#{keyType}, #{valType}> map = new LinkedHashMap<>();
                if (str instanceof JsonObject obj) {
                    for (Map.Entry<String, JsonValue> e : obj.entrySet()) {
                        #{keyType} key = Optional.ofNullable(JsonString.of(e.getKey())).<#{keyType}>map(json -> #{key}).orElse(null);
                        JsonValue json = e.getValue();
                        #{valType} val = #{val};
                        map.put(key, val);
                    }
                } else if (str instanceof JsonArray array) {
                    JsonValue prev = null;
                    for (Iterate.Entry<JsonValue> e : Iterate.of(array)) {
                        if (e.isEven()) {
                            prev = e.value();
                            continue;
                        }
                        #{keyType} key = Optional.ofNullable(prev).<#{keyType}>map(json -> #{key}).orElse(null);
                        JsonValue json = e.value();
                        #{valType} val = #{val};
                        map.put(key, val);
                    }
                } else {
                    throw new JsonStructException();
                }
                return map;
            }
            """)
            .interpolateType("#{keyType}", entryTypes[0].toString())
            .interpolateType("#{valType}", entryTypes[1].toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{key}", toCode(entryTypes[0], Path.of()))
            .interpolate("#{val}", toCode(entryTypes[1], Path.of()))
            .add(Imports.of("""
                import com.mammb.code.jsonstruct.lang.*;
                import com.mammb.code.jsonstruct.parser.*;
                import com.mammb.code.jsonstruct.JsonStructException;
                """)));

        return Code.of("""
            #{methodName}(json.at(#{pointerName}))""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{pointerName}", createPointer(path));
    }


    private String createPointer(Path path) {
        String pointerName = path.camelJoinOr("self") + "Pointer";
        if (definedNames.contains(pointerName)) {
            // reuse if duplicate
            return pointerName;
        }
        backingCodes.addHead(Code.of("""
            private static final JsonPointer #{pointerName} = JsonPointer.of("#{name}");""")
            .interpolate("#{pointerName}", pointerName)
            .interpolate("#{name}", path.pointerJoin()));
        definedNames.add(pointerName);
        return pointerName;
    }


    private Code backingWithClear() {
        Code ret = backingCodes;
        backingCodes = Code.of();
        stack.clear();
        definedNames.clear();
        return ret;
    }


    private String uniqueName(String candidate) {
        for(int i = 1; ; i++) {
            if (definedNames.contains(candidate)) {
                candidate = candidate + i;
            } else {
                definedNames.add(candidate);
                return candidate;
            }
        }
    }


    private Code withStack(Element element, Function<TypeElement, Code> function) {
        if (!element.getKind().isClass()) {
            throw new JsonStructException("element must be type.[{}]", element);
        }
        TypeElement type = (TypeElement) element;
        if (stack.stream().filter(type.getQualifiedName()::equals).count() > cyclicDepth) {
            return Code.of("null");
        }
        stack.push(type.getQualifiedName());
        Code ret = function.apply(type);
        stack.pop();
        return ret;
    }


    private String instantiation(ExecutableElement executable, LangUtil lang) {

        if (lang.isConstructor(executable)) {
            // e.g. "new Book"
            var constructor = executable.toString();
            return "new " + constructor.substring(0, constructor.indexOf('('));
        }

        if (lang.isStaticFactory(executable)) {
            // e.g. "Book.of"
            var declaredType = (DeclaredType) executable.getReturnType();
            return declaredType.asElement().getSimpleName() + "." + executable.getSimpleName();
        }

        throw new JsonStructException("Name not identified. [{}]", executable);

    }

}
