package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.lang.LangModels;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
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
        return BackingCode.of(object(type, AccessPath.of("object")), clearBacking());
    }


    public Code toCode(TypeMirror type, AccessPath path) {
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


    public Code toCode(ExecutableElement accessor, AccessPath path) {
        return toCode(accessor.getReturnType(),
            path.with(accessor.getSimpleName().toString()));
    }


    public Code basic(AccessPath path) {
        return Code.of("""
            .append(convert.stringify(#{path}.orElse(null)))""")
            .interpolate("#{path}", path.elvis());
    }


    public Code object(TypeElement type, AccessPath path) {

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


    public Code array(TypeMirror type, AccessPath path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = path.camelJoin("") + "Stringify";
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .append(#{methodName}(Arrays.asList(#{path}.orElse(new #{type}[0]))))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path.elvis())
            .interpolateType("#{type}", entryType.toString());
    }


    public Code collection(TypeMirror type, AccessPath path) {

        TypeMirror entryType = lang.entryType(type);
        String methodName = path.camelJoin("") + "Stringify";
        buildIterableMethod(entryType, methodName);

        return Code.of("""
            .append("[")
                .append(#{methodName}(#{path}.orElse(List.of())))
            .append("]")""")
            .interpolate("#{methodName}", methodName)
            .interpolate("#{path}", path.elvis());
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
            .interpolate("#{entry}", toCode(entryType, AccessPath.of("object")));
        backingMethods.add(backingMethod);
    }


    public Code map(TypeMirror type, AccessPath path) {

        TypeMirror[] entryTypes = lang.mapEntryTypes(type);
        String methodName = path.camelJoin("") + "Stringify";

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
                .interpolate("#{keyEntry}", toCode(key, AccessPath.of("entry", "getKey")))
                .interpolate("#{valEntry}", toCode(val, AccessPath.of("entry", "getValue")));
            backingMethods.add(backingMethod);

            return Code.of("""
            .append("{")
                .append(#{methodName}(#{path}.orElse(Map.of()).entrySet()))
            .append("}")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path.elvis());

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
                .interpolate("#{keyEntry}", toCode(key, AccessPath.of("entry", "getKey")))
                .interpolate("#{valEntry}", toCode(val, AccessPath.of("entry", "getValue")));
            backingMethods.add(backingMethod);

            return Code.of("""
            .append("[")
                .append(#{methodName}(#{path}.orElse(Map.of()).entrySet()))
            .append("]")""")
                .interpolate("#{methodName}", methodName)
                .interpolate("#{path}", path.elvis());
        }

    }

    private Code clearBacking() {
        Code ret = backingMethods;
        backingMethods = Code.of();
        return ret;
    }

}
