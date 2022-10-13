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

/**
 * JsonStructClassWriter.
 * @author Naotsugu Kobayashi
 */
public class JsonStructClassWriter {

    /** Context of processing. */
    private final Context context;

    /**
     * Constructor.
     */
    private JsonStructClassWriter(Context context) {
        this.context = context;
    }


    /**
     * Create a criteria {@link JsonStructClassWriter} instance.
     * @param context the context of processing
     * @return the class writer
     */
    public static JsonStructClassWriter of(Context context) {
        return new JsonStructClassWriter(context);
    }


    /**
     * Write a class file.
     */
    void write(JsonStructEntity entity) {

        var name = entity.getSimpleName() + "_";
        try {

            FileObject fo = context.getFiler().createSourceFile(entity.getPackageName() + "." + name);
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + entity.getPackageName() + ";");
                pw.println();
                pw.println("import com.mammb.code.jsonstruct.Json;");
                pw.println("import javax.annotation.processing.Generated;");
                pw.println("import java.io.Reader;");
                pw.println("import java.io.StringReader;");
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JsonStructProcessor.class.getName()));
                pw.println("""
                    public class %1$s implements Json<%2$s> {
                        @Override
                        public %2$s from(Reader reader) {
                            return null;
                        }
                        @Override
                        public %2$s from(CharSequence cs) {
                            return from(new StringReader(cs.toString()));
                        }
                    }
                    """.formatted(
                    name,
                    entity.getSimpleName()));

                pw.flush();
            }
        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", entity.getSimpleName(), e.getMessage());
        }
    }

}
