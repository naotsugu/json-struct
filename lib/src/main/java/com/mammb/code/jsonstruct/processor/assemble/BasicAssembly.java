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

import javax.lang.model.element.Element;
import java.util.Objects;

/**
 * BasicAssembly.
 * @author Naotsugu Kobayashi
 */
public class BasicAssembly implements Assembly {

    private final Element element;


    /**
     * Constructor.
     * @param element
     */
    private BasicAssembly(Element element) {
        this.element = Objects.requireNonNull(element);
    }


    /**
     * Create the BasicAssembly.
     * @return the BasicAssembly
     */
    public static BasicAssembly of(Element element) {
        return new BasicAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        return name().isEmpty()

            ? BackingCode.of("""
                json.as(convert.to(#{type}.class))""")
                  .interpolateType("#{type}", element.asType().toString())

            : BackingCode.of("""
                ((JsonStructure) json).as("#{name}", convert.to(#{type}.class))""")
                .interpolate("#{name}", ctx.path() + name())
                .interpolateType("#{type}", element.asType().toString())
                .addImports(Imports.of("com.mammb.code.jsonstruct.parser.JsonStructure"));
    }

}
