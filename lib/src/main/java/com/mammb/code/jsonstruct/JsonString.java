package com.mammb.code.jsonstruct;

public interface JsonString extends JsonValue {

    static JsonString of(CharSource cs) {
        return new JsonString() {
            final CharSource source = cs;
        };
    }

}
