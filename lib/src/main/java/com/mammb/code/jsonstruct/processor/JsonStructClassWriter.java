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
 *
 * @author Naotsugu Kobayashi
 */
public class JsonStructClassWriter {

    /** Context of processing. */
    private final Context context;

    /**
     * Constructor.
     * @param context the context of processing
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
     * Write a RootSource class file.
     */
    private void writeRootSourceClass() {

        var packageName = "com.mammb.code.jsonstruct";
        var className = "JsonImpl";
        try {

            FileObject fo = context.getFiler().createSourceFile(packageName + "." + className);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + packageName + ";");
                pw.println();
                pw.println("");
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JsonStructProcessor.class.getName()));
                pw.println("""
                    public class %s implements Json {
                        @Override
                        public <T> T as(Class<T> clazz, Reader reader) {
                            return null;
                        }
                        @Override
                        public <T> T as(Class<T> clazz, CharSequence cs) {
                            return null;
                        }
                    }
                    """.formatted(className));

                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", packageName, e.getMessage());
        }

    }

}
