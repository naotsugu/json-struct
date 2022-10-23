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

import com.mammb.code.jsonstruct.processor.JsonStructException;
import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import java.util.Objects;

/**
 * ArrayAssembly.
 * @author Naotsugu Kobayashi
 */
public class ArrayAssembly implements Assembly {

    private final Element element;

    /**
     * Constructor.
     * @param element
     */
    private ArrayAssembly(Element element) {
        if (element.asType().getKind() != TypeKind.ARRAY) {
            throw new JsonStructException("element type must be array. [{}]", element);
        }
        this.element = Objects.requireNonNull(element);
    }


    /**
     * Create a ArrayAssembly.
     * @return the ArrayAssembly
     */
    public static ArrayAssembly of(Element element) {
        return new ArrayAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        ArrayType arrayType = (ArrayType) element.asType();
        Element compElement = ctx.lang().asTypeElement(arrayType.getComponentType());
        Assembly comp = Assemblies.toAssembly(compElement, ctx);
        BackingCode entryCode = comp.execute(ctx.next("/"));

        var methodName = name() + "Array";

        BackingCode ret = BackingCode.of("""
            #{methodName}((JsonArray) json.at("#{path}"))""")
            .interpolate("#{methodName}", methodName)
            .interpolateType("#{path}", ctx.path() + name());

        Imports imports = Imports.of("""
            import java.util.List;
            import java.util.ArrayList;
            import com.mammb.code.jsonstruct.parser.JsonStructure;
            import com.mammb.code.jsonstruct.parser.JsonArray;
            import com.mammb.code.jsonstruct.parser.JsonValue;
            """);

        Code backingMethod = Code.of("""
            private #{type}[] #{methodName}(JsonArray array) {
                List<#{type}> list = new ArrayList<>();
                for (JsonValue json : array) {
                    list.add(#{entry});
                }
                return list.toArray(new #{type}[0]);
            }
            """)
            .interpolateType("#{type}", compElement.asType().toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", entryCode.code())
            .add(imports);

        return ret.addBackingMethod(backingMethod)
            .addBackingMethod(entryCode.backingMethods());
    }

}
