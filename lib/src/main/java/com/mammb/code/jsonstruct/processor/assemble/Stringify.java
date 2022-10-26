package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.lang.Iterate;
import com.mammb.code.jsonstruct.lang.LangModels;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.Writer;
import java.util.List;

public class Stringify {

    private final AssembleContext ctx;
    private final LangModels lang;
    private final Code backingMethods;

    private Stringify(AssembleContext ctx, LangModels lang, Code backingMethods) {
        this.ctx = ctx;
        this.lang = lang;
        this.backingMethods = backingMethods;
    }

    public static Stringify of(AssembleContext ctx) {
        return new Stringify(ctx, ctx.lang(), Code.of());
    }


    public BackingCode build(TypeElement type) {
        return BackingCode.of(object(type, "object"), backingMethods);
    }


    public Code toCode(ExecutableElement accessor, String parent) {
        TypeMirror type = accessor.getReturnType();
        if (ctx.isKnown(type.toString())) {
            return basic(accessor, parent);
        }
        if (lang.isListLike(type) ||
            lang.isSetLike(type) ||
            lang.isArrayLike(type)) {
            return array(accessor, parent);
        }
        if (lang.isMapLike(type)) {
            return map(accessor, parent);
        }
        return object(accessor, parent);
    }


    public Code basic(ExecutableElement accessor, String parent) {
        return Code.of("""
            .append(convert.stringify(#{path}))""")
            .interpolate("#{path}", parent + "." + accessor.getSimpleName() + "()");
    }


    public Code object(ExecutableElement accessor, String parent) {
        return object((TypeElement) lang.asTypeElement(accessor.getReturnType()), parent);
    }

    public Code object(TypeElement type, String parent) {
        Code props = Code.of();
        List<ExecutableElement> accessors = lang.selectAccessors(type);
        for (Iterate.Entry<ExecutableElement> accessor : Iterate.of(accessors)) {
            props.add(Code.of("""
                .append("\\"#{name}\\": ")#{value}""")
                .interpolate("#{name}", lang.getPropertyName(accessor.value()))
                .interpolate("#{value}", toCode(accessor.value(), parent))
                .append(accessor.hasNext() ? ".append(',')" : "")
            );
        }
        return Code.of("""
            writer.append("{")
            #{props}
            .append("}")
            """).interpolate("#{props}", props);
    }


    public Code array(ExecutableElement accessor, String parent) {
        return Code.of("""
            .append("[")
            #{elms}
            .append("]")
            """).interpolate("#{elms}", "");
    }

    public Code map(ExecutableElement accessor, String parent) {
        return Code.of("""
            .append("{")

            .append("}")
            """);
    }

}
