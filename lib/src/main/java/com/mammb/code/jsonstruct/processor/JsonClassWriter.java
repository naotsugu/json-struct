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

import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.List;

/**
 * JsonStructClassWriter.
 * @author Naotsugu Kobayashi
 */
public class JsonClassWriter {

    /** Context of processing. */
    private final Context context;

    /**
     * Constructor.
     * @param context the context of processing
     */
    private JsonClassWriter(Context context) {
        this.context = context;
    }

    /**
     * Create a criteria {@link JsonClassWriter} instance.
     * @param context the context of processing
     * @return the class writer
     */
    public static JsonClassWriter of(Context context) {
        return new JsonClassWriter(context);
    }

    /**
     * Write a class file.
     */
    void write() {

        var packageName = "com.mammb.code.jsonstruct";
        var className = "Json_";

        CodeTemplate code = CodeTemplate.of(packageName,
            """
            import java.io.Reader;
            import javax.annotation.processing.Generated;
            import com.mammb.code.jsonstruct.converter.Converters;

            @SuppressWarnings("unchecked")
            @Generated(value = "#{processorName}")
            public class #{className} {
                public static <T> Json<T> of(Class<T> clazz) {
                    return switch (clazz.getCanonicalName()) {
                        #{cases}
                        default -> throw new RuntimeException();
                    };
                }
            }
            """)
            .bind("#{processorName}", JsonStructProcessor.class.getName())
            .bind("#{className}", className)
            .bind("#{cases}", caseExpression(context.getGenerated()));

        try {
            FileObject fo = context.getFiler().createSourceFile(packageName + "." + className);
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                code.writeTo(pw);
            }
        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", packageName, e.getMessage());
        }

    }

    private static String caseExpression(List<JsonStructEntity> entities) {
        var sb = new StringBuilder();
        for (JsonStructEntity entity : entities) {
            sb.append("case \"%s\" -> (Json<T>) new %s(Converters.of());\n".formatted(
                entity.getQualifiedName(),
                entity.getQualifiedName() + "_"));
        }
        return sb.toString();
    }

}
