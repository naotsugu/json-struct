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
package com.mammb.code.jsonstruct.model;

import com.mammb.code.jsonstruct.processor.CodeTemplate;
import com.mammb.code.jsonstruct.processor.Context;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
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

    /** Root entity. */
    private final ObjectAssembly root;


    /**
     * Constructor.
     */
    private JsonStructEntity(Context context, ExecutableElement execElement) {
        this.context = context;
        this.root = ObjectAssembly.rootOf(context, execElement);
    }

    public static Optional<JsonStructEntity> of(Context context, Element element) {

        if (!isInTarget(element)) {
            return Optional.empty();
        }

        if (element.getKind().isClass()) {
            return Optional.of(new JsonStructEntity(context, Utils.selectConstructorLike(element)));
        }
        if (Utils.isConstructor(element)) {
            return Optional.of(new JsonStructEntity(context, (ExecutableElement) element));
        }
        if (Utils.isStaticFactory(element)) {
            return Optional.of(new JsonStructEntity(context, (ExecutableElement) element));
        }

        return Optional.empty();

    }


    public void writeTo(CodeTemplate code) {
        code.add("""
            @Override
            public %s from(Reader reader) {
                var json = Parser.of(reader).parse();
                return #{};
            }
            """.formatted(getSimpleName()));
        root.writeTo(code, "#{}", "");
        code.bind("#{}", "");
    }

    /**
     * Get simple name of the entity.
     * e.g. {@code Person}
     * @return simple name of the entity
     */
    public String getSimpleName() {
        return root.getSimpleName();
    }


    /**
     * Get qualified name of the entity class.
     * e.g. {@code foo.bar.Person}
     * @return qualified name of the static metamodel class
     */
    public String getQualifiedName() {
        return root.getQualifiedName();
    }


    /**
     * Get package name of the entity.
     * @return package name of the entity
     */
    public String getPackageName() {
        return root.getPackageName();
    }


    private static boolean isInTarget(Element element) {
        return element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Object::toString)
            .anyMatch(ANNOTATION_TYPE::equals);
    }

}
