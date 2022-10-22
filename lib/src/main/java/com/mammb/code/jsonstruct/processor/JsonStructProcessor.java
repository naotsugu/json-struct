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
package com.mammb.code.jsonstruct.processor;

import com.mammb.code.jsonstruct.JsonStruct;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Objects;
import java.util.Set;

/**
 * Main annotation processor.
 * @author Naotsugu Kobayashi
 */
@SupportedAnnotationTypes({ JsonStructEntity.ANNOTATION_TYPE })
@SupportedOptions({ Context.DEBUG_OPTION_KEY })
public class JsonStructProcessor extends AbstractProcessor {

    /** Context of processing. */
    private Context context;


    @Override
    public void init(ProcessingEnvironment env) {

        super.init(env);
        this.context = new Context(env);

        var version = getClass().getPackage().getImplementationVersion();
        context.logInfo("JsonStructProcessor {}", (Objects.isNull(version) ? "-" : version));

    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (Objects.isNull(context) || annotations.isEmpty() ||
            roundEnv.errorRaised() || roundEnv.processingOver()) {
            return false;
        }

        try {

            var writer = JsonStructClassWriter.of(context);

            for (Element element : roundEnv.getElementsAnnotatedWith(JsonStruct.class)) {

                var entity = JsonStructEntity.of(context, element);
                if (entity.isPresent() && !context.isProcessed(entity.get())) {
                    writer.write(entity.get());
                    context.addProcessed(entity.get());
                }

            }

            JsonClassWriter.of(context).write();

        } catch (Exception e) {
            e.printStackTrace();
            context.logError("Exception : {}", e.getMessage());
        }

        return false;
    }

}
