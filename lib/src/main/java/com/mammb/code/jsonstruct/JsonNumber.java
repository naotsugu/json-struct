package com.mammb.code.jsonstruct;

public interface JsonNumber extends JsonValue {

    static JsonNumber of(NumberSource ns) {
        return new JsonNumber() {
            final NumberSource source = ns;
        };
    }

}
