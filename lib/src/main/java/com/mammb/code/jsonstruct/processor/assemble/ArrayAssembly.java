package com.mammb.code.jsonstruct.processor.assemble;

import com.mammb.code.jsonstruct.JsonStructException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.Objects;

public class ArrayAssembly implements Assembly {

    private final Element element;

    /**
     * Constructor.
     * @param element
     */
    private ArrayAssembly(Element element) {
        if (element.asType().getKind() != TypeKind.ARRAY) {
            throw new JsonStructException("element type must be array. " + element);
        }
        this.element = Objects.requireNonNull(element);
    }


    /**
     * Create the ListAssembly.
     * @return the ListAssembly
     */
    public static ArrayAssembly of(Element element) {
        return new ArrayAssembly(element);
    }


    @Override
    public Element element() {
        return element;
    }


    @Override
    public BackingCode execute(AssembleContext ctx) {

        ArrayType arrayType = (ArrayType) element.asType();
        Element compElement = ctx.lang().asTypeElement(arrayType.getComponentType());
        Assembly comp = Assemblies.toAssembly(compElement, ctx);
        BackingCode entryCode = comp.execute(ctx.next("/"));

        var methodName = name() + "Array";

        BackingCode ret = BackingCode.of("""
            #{methodName}((JsonArray) json.at("#{path}"))""")
            .interpolate("#{methodName}", methodName)
            .interpolateType("#{path}", ctx.path() + name());

        Imports imports = Imports.of("""
            import java.util.List;
            import java.util.ArrayList;
            import com.mammb.code.jsonstruct.parser.JsonStructure;
            import com.mammb.code.jsonstruct.parser.JsonArray;
            import com.mammb.code.jsonstruct.parser.JsonValue;
            """);

        Code backingMethod = Code.of("""
            private #{type}[] #{methodName}(JsonArray array) {
                List<#{type}> list = new ArrayList<>();
                for (JsonValue json : array) {
                    list.add(#{entry});
                }
                return list.toArray(new #{type}[0]);
            }
            """)
            .interpolateType("#{type}", compElement.asType().toString())
            .interpolate("#{methodName}", methodName)
            .interpolate("#{entry}", entryCode.code())
            .add(imports);

        return ret.addBackingMethod(backingMethod)
            .addBackingMethod(entryCode.backingMethods());
    }

}
