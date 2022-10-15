package com.mammb.code.jsonstruct.entity;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import java.util.Comparator;
import java.util.Optional;

public class Utils {

    public static Optional<ExecutableElement> getConstructor(Element element) {
        return element.getEnclosedElements().stream()
            .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
            .map(ExecutableElement.class::cast)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
    }

    public static String instantiateName(ExecutableElement executable) {
        if (executable.getKind() == ElementKind.CONSTRUCTOR &&
            executable.getModifiers().contains(Modifier.PUBLIC)) {
            // e.g. "new FullName"
            // public record FullName(String givenName, String familyName) { }
            var constructor = executable.toString();
            return "new " + constructor.substring(0, constructor.indexOf('('));
        }
        if (executable.getKind() == ElementKind.METHOD &&
            executable.getModifiers().contains(Modifier.STATIC)) {
            // e.g. "Pet.of"
            // public class Pet {
            //    public static Pet of(String name) { return new Pet(name); }
            // }
            DeclaredType declaredType = (DeclaredType) executable.getReturnType();
            return declaredType.asElement().getSimpleName() + "." + executable.getSimpleName();
        }
        return "";
    }


}
