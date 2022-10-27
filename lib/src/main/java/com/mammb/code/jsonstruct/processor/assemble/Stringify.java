package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.lang.LangModels;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Stringify {

    private final LangModels lang;
    private final Set<String> basicClasses;
    private Code backingMethods;


    private Stringify(LangModels lang, Set<String> basicClasses, Code backingMethods) {
        this.lang = lang;
        this.basicClasses = basicClasses;
        this.backingMethods = backingMethods;
    }


    public static Stringify of(LangModels lang, Set<String> basicClasses) {
        return new Stringify(lang, basicClasses, Code.of());
    }


    public BackingCode build(TypeElement type) {
        return BackingCode.of(object(type, "object"), clearBacking());
    }


    public Code toCode(TypeMirror type, String path) {
        if (basicClasses.contains(type.toString())  || lang.isEnum(type)) {
            return basic(path);
        }
        if (lang.isArrayLike(type)) {
            return array(type, path);
        }
        if (lang.isListLike(type) || lang.isSetLike(type)) {
            return collection(type, path);
        }
        if (lang.isMapLike(type)) {
            return map(type, path);
        }
        return object((TypeElement) lang.asTypeElement(type), path);
    }


    public Code toCode(ExecutableElement accessor, String path) {
        return toCode(accessor.getReturnType(),
            path + "." + accessor.getSimpleName() + "()");
    }


    public Code basic(String path) {
        return Code.of("""
            .append(convert.stringify(#{path}))""")
            .interpolate("#{path}", path);
    }


    public Code object(TypeElement type, String path) {

        Code props = Code.of();

        for (Iterate.Entry<ExecutableElement> accessor : Iterate.of(lang.selectAccessors(type))) {
            Code prop = Code.of("""
                .append("\\"#{name}\\": ")
                    #{value}""")
                .interpolate("#{name}", lang.getPropertyName(accessor.value()))
                .interpolate("#{value}", toCode(accessor.value(), path))
                .append(accessor.hasNext() ? ".append(',')" : "");
            props.add(prop);
        }

        return Code.of("""
            .append("{")
                #{props}
            .append("}")
            """).interpolate("#{props}", props);

    }


    public Code array(TypeMirror type, String path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = path.replaceAll("\\W", "") + "Stringify";
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .append(#{methodName}(Arrays.asList(#{path})))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path);
    }


    public Code collection(TypeMirror type, String path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = path.replaceAll("\\W", "") + "Stringify";
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .append(#{methodName}(#{path}))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path);
    }

    private void buildIterableMethod(TypeMirror entryType, String methodName) {
        Code backingMethod = Code.of("""
            private CharSequence #{methodName}(Iterable<#{type}> iterable) {
                StringBuilder sb = new StringBuilder();
                for (#{type} object : iterable) {
                    if (sb.length() > 0) sb.append(',');
                    sb#{entry};
                }
                return sb;
            }
            """)
            .interpolateType("#{type}", entryType.toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", toCode(entryType, "object"));
        backingMethods.add(backingMethod);
    }


    public Code map(TypeMirror type, String path) {

        TypeMirror[] entryTypes = lang.mapEntryTypes(type);
        String methodName = path.replaceAll("\\W", "") + "Stringify";

        TypeMirror key = entryTypes[0];
        TypeMirror val = entryTypes[1];

        if (basicClasses.contains(key.toString())) {
            Code backingMethod = Code.of("""
            private CharSequence #{methodName}(Iterable<Map.Entry<#{keyType}, #{valType}>> iterable) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<#{keyType}, #{valType}> entry : iterable) {
                    if (sb.length() > 0) sb.append(',');
                    sb#{keyEntry}.append(": ")
                        #{valEntry};
                }
                return sb;
            }
            """)
                .interpolateType("#{keyType}", key.toString())
                .interpolateType("#{valType}", val.toString())
                .interpolate("#{methodName}", methodName)
                .interpolate("#{keyEntry}", toCode(key, "entry.getKey()"))
                .interpolate("#{valEntry}", toCode(val, "entry.getValue()"));
            backingMethods.add(backingMethod);

            return Code.of("""
            .append("{")
                .append(#{methodName}(#{path}.entrySet()))
            .append("}")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path);

        } else {
            Code backingMethod = Code.of("""
            private CharSequence #{methodName}(Iterable<Map.Entry<#{keyType}, #{valType}>> iterable) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<#{keyType}, #{valType}> entry : iterable) {
                    if (sb.length() > 0) sb.append(',');
                    sb#{keyEntry}.append(",")
                        #{valEntry};
                }
                return sb;
            }
            """)
                .interpolateType("#{keyType}", key.toString())
                .interpolateType("#{valType}", val.toString())
                .interpolate("#{methodName}", methodName)
                .interpolate("#{keyEntry}", toCode(key, "entry.getKey()"))
                .interpolate("#{valEntry}", toCode(val, "entry.getValue()"));
            backingMethods.add(backingMethod);

            return Code.of("""
            .append("[")
                .append(#{methodName}(#{path}.entrySet()))
            .append("]")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path);
        }

    }

    private Code clearBacking() {
        Code ret = backingMethods;
        backingMethods = Code.of();
        return ret;
    }

}
