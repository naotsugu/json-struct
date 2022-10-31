/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jsonstruct.testdata;

import com.mammb.code.jsonstruct.JsonStruct;

import java.util.Map;

/**
 * Pet.
 *
 * @author Naotsugu Kobayashi
 */
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
