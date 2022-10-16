package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.CodeTemplate;
import com.mammb.code.jsonstruct.processor.Context;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

public class ObjectEntity implements Entity {

    /** Context of processing. */
    private final Context context;

    private TypeElement typeElement;

    private ExecutableElement executable;

    private List<Entity> parameters;

    /** the json pointer. */
    private String pointer;

    public ObjectEntity(Context context, ExecutableElement executable, String pointer) {
        if (executable.isVarArgs()) {
            throw new IllegalArgumentException("not supported");
        }
        this.context = context;
        this.typeElement = (TypeElement) executable.getEnclosingElement();
        this.executable = executable;
        this.pointer = pointer + '/';
        this.parameters = asParameters(context, executable.getParameters(), this.pointer);
    }


    private static List<Entity> asParameters(
        Context context, List<? extends VariableElement> variables, String pointer) {
        List<Entity> list = new ArrayList<>();
        for (VariableElement e : variables) {
            if (context.isBasic(e.asType())) {
                list.add(BasicEntity.of(context, e, pointer));
            } else {
                var elm = context.getTypeUtils().asElement(e.asType());
                list.add(new ObjectEntity(
                    context,
                    Utils.getConstructor(elm).get(),
                    pointer + e.getSimpleName().toString()));
            }
        }
        return list;
    }


    @Override
    public void writeTo(CodeTemplate code, String key) {
        var indent = (int) (pointer.chars().filter(c -> c == '/').count() + 2) * 4;
        code.bind(key, Utils.instantiateName(executable) + "(\n" + " ".repeat(indent) + key);
        for (int i = 0; i < parameters.size(); i++) {
            if (i != 0) {
                code.bind(key, ",\n" + " ".repeat(indent) + key);
            }
            parameters.get(i).writeTo(code, key);
        }
        code.bind(key, ")" + key);
    }


    /**
     * Get simple name of the entity.
     * e.g. {@code Person}
     * @return simple name of the entity
     */
    String getSimpleName() {
        return typeElement.getSimpleName().toString();
    }


    /**
     * Get qualified name of the entity.
     * e.g. {@code foo.bar.Person}
     * @return qualified name of the entity
     */
    String getQualifiedName() {
        return typeElement.getQualifiedName().toString();
    }


    /**
     * Get package name of the entity.
     * @return package name of the entity
     */
    String getPackageName() {
        return Utils.getPackage(typeElement).getQualifiedName().toString();
    }

}
