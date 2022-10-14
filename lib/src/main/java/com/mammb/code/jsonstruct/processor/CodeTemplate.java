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
package com.mammb.code.jsonstruct.processor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The utility of the code template.
 * @author Naotsugu Kobayashi
 */
public class CodeTemplate {

    private String packageName;
    private Set<String> imports;
    private List<String> codes;

    /**
     * Create a {@link CodeTemplate} instance.
     * @param packageName ths package name
     */
    CodeTemplate(String packageName) {
        this.packageName = Objects.requireNonNull(packageName);
        this.imports = new HashSet<>();
        this.codes = new ArrayList<>();
    }

    /**
     * Create a {@link CodeTemplate} instance.
     * @param packageName ths package name
     * @param code  ths string code lines
     * @return a {@link CodeTemplate} instance
     */
    public static CodeTemplate of(String packageName, String code) {

        var ret = new CodeTemplate(packageName);

        code.lines().forEach(line -> {

            var trimmed = line.trim();

            if (trimmed.startsWith("package ") && trimmed.endsWith(";")) {
                // if contains the package statement, overwrite the package name
                if (ret.packageName.isEmpty()) {
                    ret.packageName = trimmed.substring(
                        "package ".length(),
                        trimmed.length() - 1);
                }

            } else if (trimmed.startsWith("import ") && trimmed.endsWith(";")) {
                // if contains the import statement, add to the imports holder
                ret.imports.add(trimmed.substring(
                    "import ".length(),
                    trimmed.length() - 1));

            } else {
                // add as a code line
                ret.codes.add(line);

            }
        });

        return ret;
    }

    /**
     * Bind value to the template by the given key.
     * @param key the key name
     * @param value the bind value
     * @return this template
     */
    public CodeTemplate bind(String key, String value) {
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).trim().equals(key) && value.contains("\n")) {
                var indent = codes.get(i).indexOf(key);
                var lines = value.split("\n");
                codes.set(i, codes.get(i).replace(key, lines[0]));
                for (int j = 1; j < lines.length; j++) {
                    codes.add(i + j, " ".repeat(indent) + lines[j]);
                }
            } else if (codes.get(i).contains(key)) {
                codes.set(i, codes.get(i).replace(key, value));
            }
        }
        return this;
    }


    public CodeTemplate bindType(String key, String fqcn) {
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).contains(key)) {
                codes.set(i, codes.get(i).replace(key, applyImport(fqcn)));
            }
        }
        return this;
    }


    public void writeTo(PrintWriter pw) {
        pw.println("package " + packageName + ";");
        pw.println();
        normalizeImports().forEach(s -> pw.println("import " + s + ";"));
        pw.println();
        codes.forEach(pw::println);
        pw.flush();
    }


    private String applyImport(String fqcn) {
        if (fqcn.isBlank() || fqcn.contains(" ") || fqcn.contains("\n")) {
            throw new IllegalArgumentException();
        }
        var index = fqcn.lastIndexOf('.');
        if (index <= 0) {
            throw new IllegalArgumentException();
        }
        imports.add(fqcn.substring(0, index));
        return fqcn.substring(index + 1);
    }


    private List<String> normalizeImports() {
        return imports.stream()
            .filter(s -> !s.startsWith("java.lang."))
            .filter(s -> !s.equals(packageName))
            .filter(s -> !s.isBlank())
            .sorted()
            .toList();
    }

}
