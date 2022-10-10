package com.mammb.code.jsonstruct.processor.testdata;

import com.mammb.code.jsonstruct.JsonStruct;

public class Pet {
    private final String name;

    private Pet(String name) {
        this.name = name;
    }

    @JsonStruct
    public static Pet of(String name) {
        return new Pet(name);
    }
}
