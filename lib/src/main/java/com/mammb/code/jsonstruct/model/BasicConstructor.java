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
import javax.lang.model.element.Element;

public class BasicConstructor implements Constructor {

    /** Context of processing. */
    private final Context context;

    /** the element. */
    private Element element;

    /** the json pointer. */
    private String pointer;

    /** the type name like `java.lang.String` or 'int'. */
    private String typeName;


    public BasicConstructor(Context context, Element element, String parentPointer) {
        this.context = context;
        this.element = element;
        this.pointer = parentPointer + element.getSimpleName().toString();
        this.typeName = element.asType().toString();
    }

    public static BasicConstructor of(Context context, Element element, String parentPointer) {
        return new BasicConstructor(context, element, parentPointer);
    }

    @Override
    public void writeTo(CodeTemplate code, String key) {
        code.bind(key, """
            json.as("%s", convert.to(%s.class))%s"""
            .formatted(
                pointer,
                code.applyImport(typeName), key));
    }

}
