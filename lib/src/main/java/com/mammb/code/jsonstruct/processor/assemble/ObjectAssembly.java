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
package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.JsonStruct;
import com.mammb.code.jsonstruct.processor.JsonStructException;
import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.lang.LangModels;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Objects;

/**
 * ObjectAssembly.
 * @author Naotsugu Kobayashi
 */
public class ObjectAssembly implements Assembly {

    private final Element element;


    /**
     * Constructor.
     */
    private ObjectAssembly(Element element) {
        if (element.asType().getKind() != TypeKind.DECLARED) {
            throw new JsonStructException("element type must be declared. [{}]", element);
        }
        this.element = Objects.requireNonNull(element);
    }


    public static ObjectAssembly of(Element element) {
        return new ObjectAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        var type = name().isEmpty()
            ? element : ctx.lang().asTypeElement(element.asType());

        ExecutableElement constructorLike = ctx.lang()
            .selectConstructorLike(type, JsonStruct.class)
            .orElseThrow();

        AssembleContext nextCtx = ctx.next(name().isEmpty() ? "" : name() + "/");
        BackingCode paramsCode = BackingCode.of();

        List<Assembly> params = Assemblies.parameters(constructorLike, ctx);
        for (Iterate.Entry<Assembly> param : Iterate.of(params)) {
            BackingCode ret = param.value().execute(nextCtx);
            ret.append(param.hasNext() ? "," : "");
            paramsCode.add(ret);
        }

        return BackingCode.of("""
            #{newMethod}(
                #{params}
            )""")
            .interpolate("#{newMethod}", instantiation(constructorLike, ctx.lang()))
            .interpolate("#{params}", paramsCode.code())
            .addBackingMethod(paramsCode.backingMethods());
    }


    public String instantiation(ExecutableElement executable, LangModels lang) {

        if (lang.isConstructor(executable)) {
            // e.g. "new Book"
            var constructor = executable.toString();
            return "new " + constructor.substring(0, constructor.indexOf('('));
        }

        if (lang.isStaticFactory(executable)) {
            // e.g. "Book.of"
            var declaredType = (DeclaredType) executable.getReturnType();
            return declaredType.asElement().getSimpleName() + "." + executable.getSimpleName();
        }

        throw new JsonStructException("Name not identified. [{}]", executable);

    }

}
