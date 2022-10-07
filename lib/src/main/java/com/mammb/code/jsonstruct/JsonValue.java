package com.mammb.code.jsonstruct;

public interface JsonValue {

    JsonValue NULL = new JsonNull();
    JsonValue TRUE = new JsonTrue();
    JsonValue FALSE = new JsonFalse();

    @Override
    String toString();


    record JsonNull() implements JsonValue { }
    record JsonTrue() implements JsonValue { }
    record JsonFalse() implements JsonValue { }
}
