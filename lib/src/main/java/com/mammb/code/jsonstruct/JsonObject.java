package com.mammb.code.jsonstruct;

import java.util.Map;

public interface JsonObject extends JsonStructure {

    void put(String name, JsonValue value);

    static JsonObject of() {
        return new JsonObject() {
            private Map<String, JsonValue> values;
            @Override
            public void put(String name, JsonValue value) {
                values.put(name, value);
            }
        };
    }
}
