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

import com.mammb.code.jsonstruct.lang.LangModels;

import java.util.Set;

/**
 * AssemblyContext.
 * @author Naotsugu Kobayashi
 */
public record AssembleContext(String path, LangModels lang, Set<String> basicClasses) {

    public static AssembleContext of(LangModels lang, Set<String> basicClasses) {
        return new AssembleContext("/", lang, basicClasses);
    }

    public AssembleContext next(String pathNext) {
        return new AssembleContext(path + pathNext, lang, basicClasses);
    }

    public AssembleContext with(String path) {
        return new AssembleContext(path, lang, basicClasses);
    }


    public boolean isKnown(String type) {
        return basicClasses.contains(type);
    }

}

