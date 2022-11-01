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

import com.mammb.code.jsonstruct.JsonStruct;
import com.mammb.code.jsonstruct.convert.Converts;
import com.mammb.code.jsonstruct.processor.assembly.*;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

/**
 * JsonStructEntity.
 * @author Naotsugu Kobayashi
 */
public class JsonStructEntity {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "com.mammb.code.jsonstruct.JsonStruct";

    /** Utility of java lang model. */
    private final LangUtil lang;

    /** JsonStruct type element. */
    private final TypeElement element;

    /** The max cyclic depth. */
    private final int cyclicDepth;


    /**
     * Constructor.
     */
    private JsonStructEntity(LangUtil lang, TypeElement element, int cyclicDepth) {
        this.lang = lang;
        this.element = element;
        this.cyclicDepth = cyclicDepth;
    }


    /**
     * Create JsonStructEntity.
     * @param ctx the context of processing
     * @param element the entry point elements
     * @return optional of JsonStructEntity
     */
    public static Optional<JsonStructEntity> of(Context ctx, Element element) {

        if (!isInTarget(ctx, element)) {
            return Optional.empty();
        }

        LangUtil lang = LangUtil.of(ctx.getElementUtils(), ctx.getTypeUtils());

        int cyclicDepth = lang.attributeIntValue(element, ANNOTATION_TYPE, "cyclicDepth");

        if (lang.isClass(element) &&
            lang.selectConstructorLike(element, JsonStruct.class).isPresent()) {
            return Optional.of(new JsonStructEntity(lang, (TypeElement) element, cyclicDepth));
        }

        if (lang.isConstructor(element) || lang.isStaticFactory(element)) {
            return Optional.of(new JsonStructEntity(lang, (TypeElement) element.getEnclosingElement(), cyclicDepth));
        }

        return Optional.empty();

    }


    /**
     * Build code.
     * @return the code
     */
    public Code build() {

        Converts convert = Converts.of(); // TODO addon convert

        BackingCode objectifyCode = Objectify.of(lang, convert.typeClasses(), cyclicDepth).build(element);
        BackingCode stringifyCode = Stringify.of(lang, convert.stringifyClasses(), cyclicDepth).build(element);

        Imports imports = Imports.of("""
            import com.mammb.code.jsonstruct.Json;
            import com.mammb.code.jsonstruct.convert.*;
            import com.mammb.code.jsonstruct.parser.*;
            import com.mammb.code.jsonstruct.lang.*;
            import javax.annotation.processing.Generated;
            import java.io.*;
            import java.util.*;
            """);

        return Code.of("""
                @Generated(value = "#{processorName}")
                public class #{className} implements Json<#{entityName}> {

                    private final Converts convert;

                    public #{className}(Converts convert) {
                        this.convert = convert;
                    }

                    @Override
                    public #{entityName} from(Reader reader) {
                        JsonStructure json = Parser.of(reader).parse();
                        return
                            #{objectifyCode};
                    }

                    @Override
                    public void stringify(#{entityName} object, Appendable writer) throws IOException {
                        StringifyBuilder.of(writer, convert)
                            #{stringifyCode};
                    }

                    #{backingMethods}
                }
                """)
            .interpolateType("#{processorName}", JsonStructProcessor.class.getName())
            .interpolateType("#{className}", getEntityClassName())
            .interpolateType("#{entityName}", getClassName())
            .interpolate("#{objectifyCode}", objectifyCode.code())
            .interpolate("#{stringifyCode}", stringifyCode.code())
            .interpolate("#{backingMethods}", objectifyCode.backingMethods().add(stringifyCode.backingMethods()))
            .add(imports);
    }


    /**
     * Get package name of the entity.
     * @return package name of the entity
     */
    public String getPackageName() {
        return lang.getPackage(element).getQualifiedName().toString();
    }


    /**
     * Get class name of the entity.
     * e.g. {@code Person}
     * @return simple name of the entity
     */
    public String getClassName() {
        return element.getSimpleName().toString();
    }


    /**
     * Get class name of the entity.
     * e.g. {@code Person}
     * @return simple name of the entity
     */
    public String getEntityClassName() {
        return getClassName() + "_";
    }


    /**
     * Get qualified name of the entity class.
     * e.g. {@code foo.bar.Person}
     * @return qualified name of the static metamodel class
     */
    public String getQualifiedName() {
        return element.getQualifiedName().toString();
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
        if (count > 1) {
            ctx.logError("duplicate @JsonStruct. [{}]", count);
        }
        return count == 1;
    }

}
