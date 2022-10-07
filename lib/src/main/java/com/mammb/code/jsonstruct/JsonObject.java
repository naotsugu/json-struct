package com.mammb.code.jsonstruct;

import java.util.HashMap;
import java.util.Map;

public interface JsonObject extends JsonStructure {

    void put(String name, JsonValue value);
    JsonValue get(String name);

    static JsonObject of() {
        return new JsonObject() {
            private Map<String, JsonValue> values = new HashMap<>();

            @Override
            public void put(String name, JsonValue value) {
                values.put(name, value);
            }

            @Override
            public JsonValue get(String name) {
                return values.get(name);
            }

        };
    }
}
