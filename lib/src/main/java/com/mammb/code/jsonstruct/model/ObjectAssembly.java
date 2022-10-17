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

public class ObjectAssembly implements Assembly {

    /** Context of processing. */
    private final Context context;

    private final ExecutableElement constructorLike;

    /** The parameters for construction. */
    private final List<Assembly> parameters;

    /** The name on json. */
    private final String nameOnJson;

    private final Assembly parent;


    private ObjectAssembly(Context context, ExecutableElement executable, String nameOnJson, Assembly parent) {
        if (executable.isVarArgs()) {
            throw new IllegalArgumentException("not supported");
        }
        this.context = context;
        this.constructorLike = executable;
        this.nameOnJson = nameOnJson;
        this.parameters = asParameters(context, constructorLike.getParameters());
        this.parent = parent;
    }

    public static ObjectAssembly rootOf(Context context, ExecutableElement executable) {
        return new ObjectAssembly(context, executable, "/", null);
    }

    private static ObjectAssembly of(Context context, VariableElement variable, Assembly parent) {
        return new ObjectAssembly(context,
            Utils.selectConstructorLike(context.getTypeUtils().asElement(variable.asType())),
            variable.getSimpleName().toString() + "/",
            parent);
    }


    private List<Assembly> asParameters(
        Context context, List<? extends VariableElement> variables) {
        List<Assembly> list = new ArrayList<>();
        for (VariableElement e : variables) {
            if (context.isBasic(e.asType())) {
                list.add(BasicAssembly.of(context, e, this));
            } else {
                list.add(ObjectAssembly.of(context, e, this));
            }
        }
        return list;
    }


    @Override
    public void writeTo(CodeTemplate code, String key) {
        String indent = " ".repeat(4 * (depth() + 2));
        code.bind(key, Utils.instantiateName(constructorLike) + "(\n" + indent + key);
        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                code.bind(key, ",\n" + indent + key);
            }
            parameters.get(i).writeTo(code, key);
        }
        code.bind(key, ")" + key);
    }

    @Override
    public String nameOnJson() {
        return nameOnJson;
    }

    @Override
    public Assembly parent() {
        return parent;
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
        return (TypeElement) constructorLike.getEnclosingElement();
    }

}
