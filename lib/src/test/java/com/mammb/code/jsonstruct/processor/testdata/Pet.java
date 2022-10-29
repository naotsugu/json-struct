package com.mammb.code.jsonstruct.processor.testdata;

import com.mammb.code.jsonstruct.JsonStruct;

import java.util.Map;

public class Pet {

    private final String name;
    private final Map<String, FullName> owners;

    private Pet(String name, Map<String, FullName> owners) {
        this.name = name;
        this.owners = owners;
    }

    @JsonStruct
    public static Pet of(String name, Map<String, FullName> owners) {
        return new Pet(name, owners);
    }

    public String getName() {
        return name;
    }

    public Map<String, FullName> getOwners() {
        return owners;
    }

}
