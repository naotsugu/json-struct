package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.Context;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectEntity implements Entity {

    /** Context of processing. */
    private final Context context;

    private TypeElement typeElement;

    private ExecutableElement executable;

    private List<Entity> parameters;

    public ObjectEntity(Context context, ExecutableElement executable) {
        this.context = context;
        if (executable.isVarArgs()) {
            throw new IllegalArgumentException("not supported");
        }
        this.typeElement = (TypeElement) executable.getEnclosingElement();
        this.executable = executable;
        this.parameters = asParameters(context, executable.getParameters());
    }

    private static List<Entity> asParameters(
        Context context, List<? extends VariableElement> variables) {
        List<Entity> list = new ArrayList<>();
        for (VariableElement e : variables) {
            if (context.isBasic(e.asType())) {
                list.add(BasicEntity.of(context, e));
            } else {
                var elm = context.getTypeUtils().asElement(e.asType());
                list.add(new ObjectEntity(context,
                    Utils.getConstructor(elm).get()));
            }
        }
        return list;
    }

    @Override
    public String code() {
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.instantiateName(executable));
        sb.append('(');
        sb.append(parameters.stream().map(Entity::code)
            .collect(Collectors.joining(",\n")));
        sb.append(')');
        return sb.toString();
    }


}
