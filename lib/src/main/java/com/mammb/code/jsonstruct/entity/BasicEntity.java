package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.Context;
import javax.lang.model.element.Element;

public class BasicEntity implements Entity {

    /** Context of processing. */
    private final Context context;

    private Element element;

    /** the json pointer. */
    private String pointer;

    /** the type name like `java.lang.String`. */
    private String typeName;


    public BasicEntity(Context context, Element element) {
        this.context = context;
        this.element = element;
        this.pointer = "/" + element.getSimpleName().toString();
        this.typeName = element.asType().toString();
    }

    public static BasicEntity of(Context context, Element element) {
        return new BasicEntity(context, element);
    }

    @Override
    public String code() {
        return """
            json.as("%s", convert.to(%s.class))""".formatted(pointer, typeName);
    }

}
