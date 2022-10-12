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
        try {

            FileObject fo = context.getFiler().createSourceFile(packageName + "." + className);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + packageName + ";");
                pw.println();
                pw.println("import java.io.Reader;");
                pw.println("import javax.annotation.processing.Generated;");
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JsonStructProcessor.class.getName()));
                pw.println("""
                    public class %s {
                        public static <T> Json<T> of(Class<T> clazz) {
                            return switch (clazz.getCanonicalName()) {
                                %s
                                default -> throw new RuntimeException();
                            };
                        }
                    }
                    """.formatted(
                        className,
                        caseExpression(context.getGenerated())));

                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", packageName, e.getMessage());
        }

    }

    private static String caseExpression(List<JsonStructEntity> entities) {
        var sb = new StringBuilder();
        for (JsonStructEntity entity : entities) {
            if (sb.length() > 0) sb.append("            ");
            sb.append("""
            case \"%s\" ->
                            (Json<T>) new %s();
            """.formatted(entity.getQualifiedName(), entity.getQualifiedName() + "_"));
        }
        return sb.toString();
    }

}
