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

        try {

            FileObject fo = context.getFiler().createSourceFile(
                entity.getPackageName() + "." + entity.getEntityClassName());

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                Code code = entity.build();

                pw.println("package " + entity.getPackageName() + ";");
                pw.println("");
                pw.println(code.imports().toString());
                pw.println("");
                entity.build().content().lines().forEach(pw::println);

            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}",
                entity.getClassName(), e.getMessage());
            context.logError(e);
        }
    }

}
