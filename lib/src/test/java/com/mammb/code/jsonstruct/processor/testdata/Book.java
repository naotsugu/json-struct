package com.mammb.code.jsonstruct.processor.testdata;

import com.mammb.code.jsonstruct.JsonStruct;

public class Book {
    private final String name;

    @JsonStruct
    public Book(String name) {
        this.name = name;
    }
}
