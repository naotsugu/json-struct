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
import javax.lang.model.type.DeclaredType;

public class ListAssembly implements Assembly {

    /** Context of processing. */
    private final Context context;

    /** The name on json. */
    private final String nameOnJson;

    private final Element entryElement;

    private final Assembly entry;


    public ListAssembly(Context context, Element element) {
        this.context = context;
        this.nameOnJson = element.getSimpleName().toString();
        this.entryElement = entryElement(context, element);
        this.entry = Utils.toAssembly(context, entryElement);
    }

    public static ListAssembly of(Context context, Element variable) {
        return new ListAssembly(context, variable);
    }

    private Element entryElement(Context context, Element element) {
        DeclaredType declaredType = (DeclaredType) element.asType();
        var typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() != 1) {
            throw new RuntimeException();
        }
        return context.getTypeUtils().asElement(typeArguments.get(0));
    }

    @Override
    public String nameOnJson() {
        return nameOnJson;
    }


    @Override
    public void writeTo(CodeTemplate code, String key, String parent) {

        var methodName = nameOnJson + "List";

        code.bind(key, methodName + """
            ((JsonArray) json.at("%s"))""".formatted(parent + nameOnJson) + key);

        code.applyImport("java.util.List");
        code.applyImport("java.util.ArrayList");
        code.applyImport("com.mammb.code.jsonstruct.parser.JsonStructure");
        code.applyImport("com.mammb.code.jsonstruct.parser.JsonArray");

        var nestKey = key + key;
        code.add("""

            private List<%1$s> %2$s(JsonArray json) {
                List<%1$s> list = new ArrayList<>();
                for (int i = 0; i < json.size(); i++) {
                    list.add(%3$s);
                }
                return list;
            }
            """.formatted(
                code.applyImport(entryElement.asType().toString()),
                methodName,
                nestKey));

          entry.writeTo(code, nestKey, "i");
          code.bind(nestKey, "");
    }

}
