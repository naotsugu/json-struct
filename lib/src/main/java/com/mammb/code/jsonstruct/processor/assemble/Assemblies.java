package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.lang.LangModels;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;

public class Assemblies {

    public static List<Assembly> parameters(ExecutableElement element, LangModels lang) {
        return element.getParameters().stream()
            .map(p -> toAssembly(p, lang)).toList();
    }

    public static Assembly toAssembly(Element element, LangModels lang) {
        if (isBasic(element.asType())) {
            return BasicAssembly.of(element);
        }
        if (lang.isListLike(element.asType())) {
            return ListAssembly.of(element);
        }
        return ObjectAssembly.of(element);
    }

    public static boolean isBasic(TypeMirror type) {
        return Objects.equals("java.lang.String", type.toString()) ||
            Objects.equals("int", type.toString());
    }

}
