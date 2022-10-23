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
import com.mammb.code.jsonstruct.lang.LangModels;
import com.mammb.code.jsonstruct.processor.assemble.AssembleContext;
import com.mammb.code.jsonstruct.processor.assemble.Assemblies;
import com.mammb.code.jsonstruct.processor.assemble.BackingCode;
import com.mammb.code.jsonstruct.processor.assemble.Code;
import com.mammb.code.jsonstruct.processor.assemble.Imports;
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
    private final LangModels lang;

    /** JsonStruct type element. */
    private final TypeElement element;


    /**
     * Constructor.
     */
    private JsonStructEntity(LangModels lang, TypeElement element) {
        this.lang = lang;
        this.element = element;
    }


    /**
     * Create JsonStructEntity.
     * @param ctx the context of processing
     * @param element the entry point elements
     * @return optional of JsonStructEntity
     */
    public static Optional<JsonStructEntity> of(Context ctx, Element element) {

        if (!isInTarget(element)) {
            return Optional.empty();
        }

        LangModels lang = LangModels.of(ctx.getElementUtils(), ctx.getTypeUtils());

        if (lang.isClass(element) &&
            lang.selectConstructorLike(element, JsonStruct.class).isPresent()) {
            return Optional.of(new JsonStructEntity(lang, (TypeElement) element));
        }

        if (lang.isConstructor(element) || lang.isStaticFactory(element)) {
            return Optional.of(new JsonStructEntity(lang, (TypeElement) element.getEnclosingElement()));
        }

        return Optional.empty();

    }


    public Code build() {

        Converts convert = Converts.of(); // TODO addon convert
        AssembleContext ctx = AssembleContext.of(lang, convert.classes());

        BackingCode backingCode = Assemblies.toAssembly(element, ctx).execute(ctx);

        Imports imports = Imports.of("""
            import com.mammb.code.jsonstruct.Json;
            import com.mammb.code.jsonstruct.convert.Converts;
            import com.mammb.code.jsonstruct.parser.JsonPointer;
            import com.mammb.code.jsonstruct.parser.Parser;
            import javax.annotation.processing.Generated;
            import java.io.Reader;
            import java.io.StringReader;
            """);

        return Code.of("""
            @Generated(value = "#{processorName}")
            public class #{className} implements Json<#{entityName}> {

                private final Converts convert;

                public #{className}(Converts convert) {
                    this.convert = convert;
                }

                @Override
                public #{entityName} from(CharSequence cs) {
                    return from(new StringReader(cs.toString()));
                }

                @Override
                public #{entityName} from(Reader reader) {
                    var json = Parser.of(reader).parse();
                    return #{assemblyCode};
                }

                #{backingMethods}
            }
            """)
            .interpolateType("#{processorName}", JsonStructProcessor.class.getName())
            .interpolateType("#{className}", getEntityClassName())
            .interpolateType("#{entityName}", getClassName())
            .interpolate("#{assemblyCode}", backingCode.code())
            .interpolate("#{backingMethods}", backingCode.backingMethods())
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
     * @param element the element
     * @return {@code true} if whether the element is subject to this Entity
     */
    private static boolean isInTarget(Element element) {
        return element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Object::toString)
            .anyMatch(ANNOTATION_TYPE::equals);
    }

}
