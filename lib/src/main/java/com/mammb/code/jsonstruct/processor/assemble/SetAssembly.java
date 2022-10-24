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
import javax.lang.model.type.TypeKind;
import java.util.Objects;

/**
 * SetAssembly.
 * @author Naotsugu Kobayashi
 */
public class SetAssembly implements Assembly {

    private final Element element;

    /**
     * Constructor.
     * @param element
     */
    private SetAssembly(Element element) {
        if (element.asType().getKind() != TypeKind.DECLARED) {
            throw new JsonStructException("element type must be declared. [{}]", element);
        }
        this.element = Objects.requireNonNull(element);
    }


    /**
     * Create a SetAssembly.
     * @return the SetAssembly
     */
    public static SetAssembly of(Element element) {
        return new SetAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        Element entryElement = ctx.lang().entryElement(element);
        Assembly entry = Assemblies.toAssembly(entryElement, ctx);
        BackingCode entryCode = entry.execute(ctx.with("/"));

        var methodName = name() + "Set";

        BackingCode ret = BackingCode.of("""
            #{methodName}((JsonArray) json.at("#{path}"))""")
            .interpolate("#{methodName}", methodName)
            .interpolateType("#{path}", ctx.path() + name());

        Imports imports = Imports.of("""
            import java.util.Set;
            import java.util.LinkedHashSet;
            import com.mammb.code.jsonstruct.parser.JsonStructure;
            import com.mammb.code.jsonstruct.parser.JsonArray;
            import com.mammb.code.jsonstruct.parser.JsonValue;
            """);

        Code backingMethod = Code.of("""
            private Set<#{type}> #{methodName}(JsonArray array) {
                Set<#{type}> set = new LinkedHashSet<>();
                for (JsonValue json : array) {
                    set.add(#{entry});
                }
                return set;
            }
            """)
            .interpolateType("#{type}", entryElement.asType().toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", entryCode.code())
            .add(imports);

        return ret.addBackingMethod(backingMethod)
            .addBackingMethod(entryCode.backingMethods());

    }

}
