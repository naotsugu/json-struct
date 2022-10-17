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

public class BasicAssembly implements Assembly {

    /** Context of processing. */
    private final Context context;

    /** The element. */
    private final Element element;

    /** The name on json. */
    private final String nameOnJson;

    /** The type name like `java.lang.String` or 'int', etc. */
    private final String typeName;

    /** The parent assembly. */
    private final Assembly parent;


    private BasicAssembly(Context context, Element element, Assembly parent) {
        this.context = context;
        this.element = element;
        this.nameOnJson = element.getSimpleName().toString();
        this.typeName = element.asType().toString();
        this.parent = parent;
    }

    public static BasicAssembly of(Context context, Element element, Assembly parent) {
        return new BasicAssembly(context, element, parent);
    }

    @Override
    public String nameOnJson() {
        return nameOnJson;
    }

    @Override
    public Assembly parent() {
        return parent;
    }

    @Override
    public void writeTo(CodeTemplate code, String key) {
        code.bind(key, """
            json.as("%s", convert.to(%s.class))%s"""
            .formatted(
                namePath(),
                code.applyImport(typeName), key));
    }

}
