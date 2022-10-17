package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.JsonStruct;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Utils {

    public static PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }


    public static Optional<ExecutableElement> getConstructor(Element element) {
        return element.getEnclosedElements().stream()
            .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
            .map(ExecutableElement.class::cast)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
    }


    public static boolean isConstructor(Element element) {
        return element.getKind() == ElementKind.CONSTRUCTOR &&
               element.getModifiers().contains(Modifier.PUBLIC);
    }


    public static boolean isStaticFactory(Element element) {

        if (element.getKind() == ElementKind.METHOD &&
            element.getModifiers().contains(Modifier.PUBLIC) &&
            element.getModifiers().contains(Modifier.STATIC)) {

            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;
            return typeElement.getQualifiedName().toString()
                .equals(executableElement.getReturnType().toString());
        }
        return false;
    }


    public static boolean isConstructorLike(Element element) {
        return isConstructor(element) || isStaticFactory(element);
    }


    public static String instantiateName(ExecutableElement executable) {
        if (isConstructor(executable)) {
            // e.g. "new FullName"
            // public record FullName(String givenName, String familyName) { }
            var constructor = executable.toString();
            return "new " + constructor.substring(0, constructor.indexOf('('));
        }
        if (isStaticFactory(executable)) {
            // e.g. "Pet.of"
            // public class Pet {
            //    public static Pet of(String name) { return new Pet(name); }
            // }
            var declaredType = (DeclaredType) executable.getReturnType();
            return declaredType.asElement().getSimpleName() + "." + executable.getSimpleName();
        }
        return "";
    }


    public static ExecutableElement selectConstructorLike(Element element) {

        List<ExecutableElement> candidate = element.getEnclosedElements().stream()
            .filter(Utils::isConstructorLike)
            .map(ExecutableElement.class::cast)
            .toList();

        var ret = candidate.stream()
            .filter(e -> Objects.nonNull(e.getAnnotation(JsonStruct.class)))
            .findFirst();
        if (ret.isPresent()) return ret.get();

        ret = candidate.stream()
            .filter(Utils::isConstructor)
            .filter(e -> e.getParameters().size() > 0)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
        if (ret.isPresent()) return ret.get();

        ret = candidate.stream()
            .filter(Utils::isStaticFactory)
            .filter(e -> e.getParameters().size() > 0)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
        if (ret.isPresent()) return ret.get();

        return candidate.stream().findFirst().orElseThrow();

    }


}
