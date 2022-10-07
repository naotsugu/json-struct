package com.mammb.code.jsonstruct;

import java.util.ArrayList;
import java.util.List;

public interface JsonArray extends JsonStructure {

    void add(JsonValue value);

    static JsonArray of() {
        return new JsonArray() {
            private List<JsonValue> values = new ArrayList<>();
            @Override
            public void add(JsonValue value) {
                values.add(value);
            }
        };
    }
}
