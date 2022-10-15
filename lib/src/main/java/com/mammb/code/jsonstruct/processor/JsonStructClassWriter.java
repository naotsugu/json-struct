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

import com.mammb.code.jsonstruct.entity.JsonStructEntity;

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

        var className = entity.getSimpleName() + "_";

        CodeTemplate code = CodeTemplate.of(entity.getPackageName(),
            """
            import com.mammb.code.jsonstruct.Json;
            import com.mammb.code.jsonstruct.converter.Converters;
            import com.mammb.code.jsonstruct.parser.JsonPointer;
            import com.mammb.code.jsonstruct.parser.Parser;
            import javax.annotation.processing.Generated;
            import java.io.Reader;
            import java.io.StringReader;

            @Generated(value = "#{processorName}")
            public class #{className} implements Json<#{entityName}> {

                private final Converters convert;

                public #{className}(Converters converters) {
                    this.convert = converters;
                }

                #{fromMethod}

                @Override
                public #{entityName} from(CharSequence cs) {
                    return from(new StringReader(cs.toString()));
                }
            }
            """)
            .bind("#{processorName}", JsonStructProcessor.class.getName())
            .bind("#{className}", className)
            .bind("#{entityName}", entity.getSimpleName())
            .bind("#{fromMethod}", entity.code());


        try {
            FileObject fo = context.getFiler().createSourceFile(entity.getPackageName() + "." + className);
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                code.writeTo(pw);
            }
        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", entity.getSimpleName(), e.getMessage());
        }
    }

}
