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

import com.mammb.code.jsonstruct.processor.assemble.Code;
import com.mammb.code.jsonstruct.processor.assemble.Imports;
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

        Imports imports = Imports.of("""
            import java.io.Reader;
            import javax.annotation.processing.Generated;
            import com.mammb.code.jsonstruct.convert.Converts;
            """);

        Code code = Code.of("""
            @SuppressWarnings("unchecked")
            @Generated(value = "#{processorName}")
            public class #{className} {

                private static final Converts converts = Converts.of();
                static {
                    // converts.addAll(null);
                }

                public static <T> Json<T> of(Class<T> clazz) {
                    return switch (clazz.getCanonicalName()) {
                        #{cases}
                        default -> throw new RuntimeException();
                    };
                }
            }
            """)
            .interpolateType("#{processorName}", JsonStructProcessor.class.getName())
            .interpolateType("#{className}", className)
            .interpolate("#{cases}", caseExpression(context.getProcessed(JsonStructEntity.class)))
            .add(imports);

        try {
            FileObject fo = context.getFiler().createSourceFile(packageName + "." + className);
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + packageName + ";");
                pw.println("");
                pw.println(code.imports().toString());
                pw.println("");
                pw.println(code.content());
                pw.flush();
            }
        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", packageName, e.getMessage());
        }

    }

    private static Code caseExpression(List<JsonStructEntity> entities) {

        var code = Code.of();
        for (JsonStructEntity entity : entities) {
            code.add(Code.of("""
                    case "#{qualifiedName}" -> (Json<T>) new #{type}(converts);""")
                    .interpolate("#{qualifiedName}", entity.getQualifiedName())
                    .interpolateType("#{type}", entity.getQualifiedName() + "_"));
        }
        return code;
    }

}
