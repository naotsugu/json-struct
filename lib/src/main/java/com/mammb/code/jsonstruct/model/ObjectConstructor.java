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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

public class ObjectConstructor implements Constructor {

    /** Context of processing. */
    private final Context context;

    private ExecutableElement executable;

    private List<Constructor> parameters;

    /** the json pointer. */
    private String pointer;

    public ObjectConstructor(Context context, ExecutableElement executable, String pointer) {
        if (executable.isVarArgs()) {
            throw new IllegalArgumentException("not supported");
        }
        this.context = context;
        this.executable = executable;
        this.pointer = pointer + '/';
        this.parameters = asParameters(context, executable.getParameters(), this.pointer);
    }


    private static List<Constructor> asParameters(
        Context context, List<? extends VariableElement> variables, String pointer) {
        List<Constructor> list = new ArrayList<>();
        for (VariableElement e : variables) {
            if (context.isBasic(e.asType())) {
                list.add(BasicConstructor.of(context, e, pointer));
            } else {
                var elm = context.getTypeUtils().asElement(e.asType());
                list.add(new ObjectConstructor(
                    context,
                    Utils.selectConstructorLike(elm),
                    pointer + e.getSimpleName().toString()));
            }
        }
        return list;
    }


    @Override
    public void writeTo(CodeTemplate code, String key) {
        var indent = (int) (pointer.chars().filter(c -> c == '/').count() + 2) * 4;
        code.bind(key, Utils.instantiateName(executable) + "(\n" + " ".repeat(indent) + key);
        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                code.bind(key, ",\n" + " ".repeat(indent) + key);
            }
            parameters.get(i).writeTo(code, key);
        }
        code.bind(key, ")" + key);
    }


    /**
     * Get simple name of the entity.
     * e.g. {@code Person}
     * @return simple name of the entity
     */
    String getSimpleName() {
        return targetType().getSimpleName().toString();
    }


    /**
     * Get qualified name of the entity.
     * e.g. {@code foo.bar.Person}
     * @return qualified name of the entity
     */
    String getQualifiedName() {
        return targetType().getQualifiedName().toString();
    }


    /**
     * Get package name of the entity.
     * @return package name of the entity
     */
    String getPackageName() {
        return Utils.getPackage(targetType()).getQualifiedName().toString();
    }


    private TypeElement targetType() {
        return (TypeElement) executable.getEnclosingElement();
    }

}
