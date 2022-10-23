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
 * ArrayAssembly.
 * @author Naotsugu Kobayashi
 */
public class MapAssembly implements Assembly {

    private final Element element;

    /**
     * Constructor.
     * @param element
     */
    private MapAssembly(Element element) {
        if (element.asType().getKind() != TypeKind.DECLARED) {
            throw new JsonStructException("element type must be declared. [{}]", element);
        }
        this.element = Objects.requireNonNull(element);
    }


    /**
     * Create a new MapAssembly.
     * @return the MapAssembly
     */
    public static MapAssembly of(Element element) {
        return new MapAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        Element[] entryElements = ctx.lang().mapEntryElement(element);

        Assembly key = Assemblies.toAssembly(entryElements[0], ctx);
        BackingCode keyCode = key.execute(ctx.next("/"));

        Assembly val = Assemblies.toAssembly(entryElements[1], ctx);
        BackingCode valCode = val.execute(ctx.next("/"));


        var methodName = name() + "Map";

        BackingCode ret = BackingCode.of("""
            #{methodName}((JsonStructure) json.at("#{path}"))""")
            .interpolate("#{methodName}", methodName)
            .interpolateType("#{path}", ctx.path() + name());

        Imports imports = Imports.of("""
            import java.util.Map;
            import java.util.LinkedHashMap;
            import com.mammb.code.jsonstruct.lang.*;
            import com.mammb.code.jsonstruct.parser.*;
            import com.mammb.code.jsonstruct.processor.JsonStructException;
            """);

        Code backingMethod = Code.of("""
            private Map<#{keyType}, #{valType}> #{methodName}(JsonStructure str) {
                Map<#{keyType}, #{valType}> map = new LinkedHashMap<>();
                if (str instanceof JsonObject obj) {
                    for (Map.Entry<String, JsonValue> e : obj.entrySet()) {
                        JsonValue json = JsonString.of(e.getKey());
                        #{keyType} key = #{key};
                        json = e.getValue();
                        #{valType} val = #{val};
                        map.put(key, val);
                    }
                } else if (str instanceof JsonArray array) {
                    JsonValue prev = null;
                    for (Iterate.Entry<JsonValue> e : Iterate.of(array)) {
                        if (e.isOdd()) {
                            JsonValue json = prev;
                            #{keyType} key = #{key};
                            json = e.value();
                            #{valType} val = #{val};
                            map.put(key, val);
                        } else {
                            prev = e.value();
                        }
                    }
                } else {
                    throw new JsonStructException();
                }
                return map;
            }
            """)
            .interpolateType("#{keyType}", entryElements[0].asType().toString())
            .interpolateType("#{valType}", entryElements[1].asType().toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{key}", keyCode.code())
            .interpolate("#{val}", valCode.code())
            .add(imports);

        return ret.addBackingMethod(backingMethod)
            .addBackingMethod(keyCode.backingMethods())
            .addBackingMethod(valCode.backingMethods());

    }

}
