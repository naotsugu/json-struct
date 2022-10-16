package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.CodeTemplate;
import com.mammb.code.jsonstruct.processor.Context;
import javax.lang.model.element.Element;

public class BasicEntity implements Entity {

    /** Context of processing. */
    private final Context context;

    /** the element. */
    private Element element;

    /** the json pointer. */
    private String pointer;

    /** the type name like `java.lang.String` or 'int'. */
    private String typeName;


    public BasicEntity(Context context, Element element, String parentPointer) {
        this.context = context;
        this.element = element;
        this.pointer = parentPointer + element.getSimpleName().toString();
        this.typeName = element.asType().toString();
    }

    public static BasicEntity of(Context context, Element element, String parentPointer) {
        return new BasicEntity(context, element, parentPointer);
    }

    @Override
    public void writeTo(CodeTemplate code, String key) {
        code.bind(key, """
            json.as("%s", convert.to(%s.class))%s"""
            .formatted(
                pointer,
                code.applyImport(typeName), key));
    }

}
