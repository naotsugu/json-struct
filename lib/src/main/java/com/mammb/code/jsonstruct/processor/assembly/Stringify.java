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

import com.mammb.code.jsonstruct.JsonStructIgnore;
import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.JsonStructException;
import com.mammb.code.jsonstruct.processor.LangUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Function;

/**
 * Stringify.
 * @author Naotsugu Kobayashi
 */
public class Stringify {

    /** The lang model utility. */
    private final LangUtil lang;

    /** The known basic classes. */
    private final Set<String> basicClasses;

    /** The backing methods. */
    private Code backingMethods;

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
     * @param backingMethods The backing methods
     * @param cyclicDepth The max cyclic depth
     */
    private Stringify(LangUtil lang, Set<String> basicClasses, Code backingMethods, int cyclicDepth) {
        this.lang = Objects.requireNonNull(lang);
        this.basicClasses = Objects.requireNonNull(basicClasses);
        this.backingMethods = Objects.requireNonNull(backingMethods);
        this.stack = new ArrayDeque<>();
        this.cyclicDepth = cyclicDepth;
        this.definedNames = new HashSet<>();
    }


    /**
     * Create a new Stringify instance.
     * @param lang The lang model utility
     * @param basicClasses The known basic classes
     * @param cyclicDepth The max cyclic depth
     * @return a new Stringify instance
     */
    public static Stringify of(LangUtil lang, Set<String> basicClasses, int cyclicDepth) {
        return new Stringify(lang, basicClasses, Code.of(), cyclicDepth);
    }


    /**
     * Build backingCode for given element.
     * @param element The type element
     * @return a backingCode
     */
    public BackingCode build(TypeElement element) {
        return BackingCode.of(
            withStack(element, e -> object(e, Path.of("object"))),
            backingWithClear());
    }


    private Code toCode(TypeMirror type, Path path) {
        if (basicClasses.contains(type.toString())  || lang.isEnum(type)) {
            return basic(path);
        }
        if (lang.isArrayLike(type)) {
            return array(type, path);
        }
        if (lang.isListLike(type) || lang.isSetLike(type)) {
            return collection(type, path);
        }
        if (lang.isMapLike(type)) {
            return map(type, path);
        }
        return withStack(lang.asTypeElement(type), element -> object(element, path));
    }


    private Code toCode(ExecutableElement accessor, Path path) {
        return toCode(accessor.getReturnType(),
            path.with(accessor.getSimpleName().toString()));
    }


    private Code basic(Path path) {
        return Code.of("""
            .appendObj(#{path}.orElse(null))""")
            .interpolate("#{path}", path.elvisJoin());
    }


    private Code object(TypeElement type, Path path) {

        Code props = Code.of();

        for (Iterate.Entry<ExecutableElement> accessor : Iterate.of(
                lang.selectAccessors(type, JsonStructIgnore.class))) {
            Code prop = Code.of("""
                .append("\\"#{name}\\":")
                    #{value}""")
                .interpolate("#{name}", lang.getPropertyName(accessor.value()))
                .interpolate("#{value}", toCode(accessor.value(), path))
                .append(accessor.hasNext() ? ".append(',')" : "");
            props.add(prop);
        }

        return Code.of("""
            .append("{")
                #{props}
            .append("}")
            """).interpolate("#{props}", props);

    }


    private Code array(TypeMirror type, Path path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "Stringify");
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .appendFun(b -> #{methodName}(Arrays.asList(#{path}.orElse(new #{type}[0])), b))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path.elvisJoin())
            .interpolateType("#{type}", entryType.toString().replace("[]", "[0]"));
    }


    private Code collection(TypeMirror type, Path path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "Stringify");
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .appendFun(b -> #{methodName}(#{path}.orElse(List.of()), b))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path.elvisJoin());
    }


    private void buildIterableMethod(TypeMirror entryType, String methodName) {
        Code backingMethod = Code.of("""
            private void #{methodName}(Iterable<#{type}> iterable, StringifyBuilder sb) {
                for (Iterate.Entry<#{type}> entry : Iterate.of(iterable)) {
                    if (!entry.isFirst()) sb.append(',');
                    sb#{entry};
                }
            }
            """)
            .interpolateType("#{type}", entryType.toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", toCode(entryType, Path.of("entry", "value")));
        backingMethods.addEmptyLine().add(backingMethod);
    }


    private Code map(TypeMirror type, Path path) {

        TypeMirror[] entryTypes = lang.biEntryTypes(type);
        String methodName = uniqueName(path.camelJoinOr("self") + "Stringify");

        TypeMirror key = entryTypes[0];
        TypeMirror val = entryTypes[1];

        if (basicClasses.contains(key.toString())) {
            Code backingMethod = Code.of("""
            private void #{methodName}(Iterable<Map.Entry<#{keyType}, #{valType}>> iterable, StringifyBuilder sb) {
                for (Iterate.Entry<Map.Entry<#{keyType}, #{valType}>> entry : Iterate.of(iterable)) {
                    if (!entry.isFirst()) sb.append(',');
                    sb#{keyEntry}.append(":")
                        #{valEntry};
                }
            }
            """)
                .interpolateType("#{keyType}", key.toString())
                .interpolateType("#{valType}", val.toString())
                .interpolate("#{methodName}", methodName)
                .interpolate("#{keyEntry}", toCode(key, Path.of("entry", "value", "getKey")))
                .interpolate("#{valEntry}", toCode(val, Path.of("entry", "value", "getValue")));
            backingMethods.addEmptyLine().add(backingMethod);

            return Code.of("""
            .append("{")
                .appendFun(b -> #{methodName}(#{path}.orElse(Map.of()).entrySet(), b))
            .append("}")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path.elvisJoin());

        } else {
            Code backingMethod = Code.of("""
            private void #{methodName}(Iterable<Map.Entry<#{keyType}, #{valType}>> iterable, StringifyBuilder sb) {
                for (Iterate.Entry<Map.Entry<#{keyType}, #{valType}>> entry : Iterate.of(iterable)) {
                    if (!entry.isFirst()) sb.append(',');
                    sb#{keyEntry}.append(",")
                        #{valEntry};
                }
            }
            """)
                .interpolateType("#{keyType}", key.toString())
                .interpolateType("#{valType}", val.toString())
                .interpolate("#{methodName}", methodName)
                .interpolate("#{keyEntry}", toCode(key, Path.of("entry", "value", "getKey")))
                .interpolate("#{valEntry}", toCode(val, Path.of("entry", "value", "getValue")));
            backingMethods.addEmptyLine().add(backingMethod);

            return Code.of("""
            .append("[")
                .appendFun(b -> #{methodName}(#{path}.orElse(Map.of()).entrySet(), b))
            .append("]")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path.elvisJoin());
        }

    }


    private Code backingWithClear() {
        Code ret = backingMethods;
        backingMethods = Code.of();
        stack.clear();
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
            return Code.of();
        }
        stack.push(type.getQualifiedName());
        Code ret = function.apply(type);
        stack.pop();
        return ret;
    }

}
