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
import javax.lang.model.element.ElementKind;

/**
 * BasicAssembly.
 * @author Naotsugu Kobayashi
 */
public class BasicAssembly implements Assembly {

    /** Context of processing. */
    private final Context context;

    /** The element. */
    private final Element element;

    /** The name on json. */
    private final String nameOnJson;

    /** The type name like `java.lang.String` or 'int', etc. */
    private final String typeName;


    private BasicAssembly(Context context, Element element) {
        this.context = context;
        this.element = element;
        this.nameOnJson = (element.getKind() == ElementKind.PARAMETER)
            ? element.getSimpleName().toString()
            : "";
        this.typeName = element.asType().toString();
    }


    public static BasicAssembly of(Context context, Element element) {
        return new BasicAssembly(context, element);
    }


    @Override
    public String nameOnJson() {
        return nameOnJson;
    }


    @Override
    public void writeTo(CodeTemplate code, String key, String parent) {

        if (nameOnJson.isEmpty()) {
            code.bind(key, """
            json.as(convert.to(%s.class))%s""".formatted(
                code.applyImport(typeName),
                key));

        } else {
            code.bind(key, """
            json.as("%s", convert.to(%s.class))%s""".formatted(
                parent + nameOnJson,
                code.applyImport(typeName),
                key));
        }
    }

}
